package potatowolfie.earth_and_water.entity.brine;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.registry.tag.FluidTags;
import potatowolfie.earth_and_water.entity.ModEntities;
import potatowolfie.earth_and_water.entity.custom.HostileWaterCreatureEntity;
import potatowolfie.earth_and_water.entity.water_charge.WaterChargeProjectileEntity;
import potatowolfie.earth_and_water.sound.ModSounds;

import java.util.EnumSet;
import java.util.List;

public class BrineEntity extends HostileWaterCreatureEntity {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState underwaterAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();

    private int idleAnimationTimeout = 0;
    private boolean isIdleAnimationRunning = false;
    private boolean isUnderwaterAnimationRunning = false;
    private boolean isAttackAnimationRunning = false;
    private boolean animationStartedThisTick = false;

    private int shootingDelay = 0;
    private int shootingStateTimer = 0;
    private static final double PROJECTILE_DANGER_RADIUS = 6.0;
    private static final double FRIENDLY_PROJECTILE_AVOIDANCE_RADIUS = 8.0;
    private int shootCooldown = 0;
    private Vec3d lastShootPosition = null;
    private boolean hasMovedEnoughToShoot = true;

    private BlockPos homePos = null;
    private static final int MAX_DISTANCE_FROM_HOME = 30;

    public enum BrineState {
        IDLE,
        UNDERWATER_IDLE,
        SHOOTING
    }

    private static final TrackedData<Integer> DATA_ID_STATE =
            DataTracker.registerData(BrineEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> MOVING =
            DataTracker.registerData(BrineEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private BrineState brineState = BrineState.UNDERWATER_IDLE;
    private BrineState previousState = BrineState.UNDERWATER_IDLE;
    private boolean isChangingState = false;

    public BrineEntity(EntityType<? extends BrineEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new BrineHybridMoveControl(this);
    }

    public static DefaultAttributeContainer.Builder createBrineAttributes() {
        return WaterCreatureEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23000000417232513);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new BrineAvoidProjectileGoal(this));
        this.goalSelector.add(1, new BrineSeekWaterGoal(this, 1.0D));
        this.goalSelector.add(2, new BrineHybridSwimGoal(this, 1.25D));
        this.goalSelector.add(3, new BrineShootGoal(this));
        this.goalSelector.add(7, new WanderAroundGoal(this, 1.0, 80));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(9, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new RevengeGoal(this));
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.touchingWater ? ModSounds.BRINE_UNDERWATER_DEATH : ModSounds.BRINE_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return this.touchingWater ? ModSounds.BRINE_UNDERWATER_DEATH : ModSounds.BRINE_DEATH;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.touchingWater ? ModSounds.BRINE_UNDERWATER_AMBIENT : ModSounds.BRINE_AMBIENT;
    }

    public void setHomePosition(BlockPos pos) {
        this.homePos = pos;
    }

    private boolean isWithinHomeBounds(BlockPos pos) {
        if (this.homePos == null) {
            return true;
        }
        return this.homePos.isWithinDistance(pos, MAX_DISTANCE_FROM_HOME);
    }

    private boolean isWithinHomeBounds(Vec3d pos) {
        if (this.homePos == null) {
            return true;
        }
        return this.homePos.isWithinDistance(pos, MAX_DISTANCE_FROM_HOME);
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new BrineNavigation(this, world);
    }

    public static boolean canSpawn(
            EntityType<BrineEntity> type,
            ServerWorldAccess world,
            SpawnReason spawnReason,
            BlockPos pos,
            Random random
    ) {
        return world.getFluidState(pos).isIn(FluidTags.WATER);
    }

    public static class BrineSwimInWaterGoal extends Goal {
        private final BrineEntity brine;
        private final double speed;
        private final int chance;
        private double targetX;
        private double targetY;
        private double targetZ;
        private int ticksSinceLastTarget = 0;

        public BrineSwimInWaterGoal(BrineEntity brine, double speed, int chance) {
            this.brine = brine;
            this.speed = speed;
            this.chance = chance;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (this.brine.hasPassengers() || this.brine.getTarget() != null) {
                return false;
            }

            if (this.brine.getRandom().nextInt(this.chance) != 0) {
                return false;
            }

            return this.brine.isTouchingWater();
        }

        @Override
        public void start() {
            this.chooseWaterTarget();
            this.ticksSinceLastTarget = 0;
        }

        @Override
        public boolean shouldContinue() {
            return this.brine.isTouchingWater();
        }

        @Override
        public void stop() {
            if (!this.brine.isFullySubmerged()) {
                this.brine.getNavigation().stop();
            }
        }

        @Override
        public void tick() {
            this.ticksSinceLastTarget++;

            if (this.ticksSinceLastTarget >= 150 ||
                    this.brine.squaredDistanceTo(this.targetX, this.targetY, this.targetZ) < 1.0D) {
                this.chooseWaterTarget();
                this.ticksSinceLastTarget = 0;
            }
        }

        private void chooseWaterTarget() {
            Vec3d currentPos = this.brine.getPos();
            int range = 15;
            int minDistance = 7;

            for (int attempts = 0; attempts < 50; attempts++) {
                double offsetX = (this.brine.getRandom().nextDouble() - 0.5D) * range * 2;
                double offsetY = (this.brine.getRandom().nextDouble() - 0.5D) * range * 2;
                double offsetZ = (this.brine.getRandom().nextDouble() - 0.5D) * range * 2;

                double potentialX = currentPos.x + offsetX;
                double potentialY = currentPos.y + offsetY;
                double potentialZ = currentPos.z + offsetZ;

                double dx = potentialX - currentPos.x;
                double dy = potentialY - currentPos.y;
                double dz = potentialZ - currentPos.z;
                double distanceSquared = dx * dx + dy * dy + dz * dz;

                if (distanceSquared < minDistance * minDistance) {
                    continue;
                }

                BlockPos checkPos = new BlockPos((int)potentialX, (int)potentialY, (int)potentialZ);

                if (!this.brine.isWithinHomeBounds(checkPos)) {
                    continue;
                }

                if (this.brine.getEntityWorld().getFluidState(checkPos).isIn(FluidTags.WATER)) {
                    this.targetX = potentialX;
                    this.targetY = potentialY;
                    this.targetZ = potentialZ;

                    if (this.brine.isFullySubmerged()) {
                        this.brine.getMoveControl().moveTo(this.targetX, this.targetY, this.targetZ, this.speed);
                    } else {
                        this.brine.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
                    }
                    return;
                }
            }

            this.targetX = currentPos.x;
            this.targetY = currentPos.y;
            this.targetZ = currentPos.z;
        }
    }

    public static class BrineSeekWaterGoal extends Goal {
        private final BrineEntity brine;
        private final double speed;
        private BlockPos targetWaterPos;

        public BrineSeekWaterGoal(BrineEntity brine, double speed) {
            this.brine = brine;
            this.speed = speed;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (this.brine.isTouchingWater()) {
                return false;
            }

            this.targetWaterPos = findDeepWater();
            return this.targetWaterPos != null;
        }

        @Override
        public boolean shouldContinue() {
            return !this.brine.isTouchingWater() &&
                    this.targetWaterPos != null &&
                    !this.brine.getNavigation().isIdle();
        }

        @Override
        public void start() {
            if (this.targetWaterPos != null) {
                this.brine.getNavigation().startMovingTo(
                        this.targetWaterPos.getX() + 0.5,
                        this.targetWaterPos.getY() + 0.5,
                        this.targetWaterPos.getZ() + 0.5,
                        this.speed
                );
            }
        }

        @Override
        public void stop() {
            this.targetWaterPos = null;
        }

        private BlockPos findDeepWater() {
            BlockPos entityPos = this.brine.getBlockPos();
            int searchRange = 16;

            for (int range = 4; range <= searchRange; range += 4) {
                for (int x = -range; x <= range; x += 2) {
                    for (int y = -8; y <= 8; y += 2) {
                        for (int z = -range; z <= range; z += 2) {
                            BlockPos checkPos = entityPos.add(x, y, z);

                            if (isDeepWater(checkPos)) {
                                return checkPos;
                            }
                        }
                    }
                }
            }
            return null;
        }

        private boolean isDeepWater(BlockPos pos) {
            World world = this.brine.getEntityWorld();
            return world.getFluidState(pos).isIn(FluidTags.WATER) &&
                    world.getFluidState(pos.up()).isIn(FluidTags.WATER);
        }
    }

    static class BrineMoveControl extends MoveControl {
        private final BrineEntity brine;

        public BrineMoveControl(BrineEntity brine) {
            super(brine);
            this.brine = brine;
        }

        public void tick() {
            if (this.brine.isFullySubmerged() && this.state == State.MOVE_TO) {
                Vec3d vec3d = new Vec3d(this.targetX - this.brine.getX(),
                        this.targetY - this.brine.getY(),
                        this.targetZ - this.brine.getZ());
                double d = vec3d.length();

                if (d < 0.5) {
                    this.state = State.WAIT;
                    this.brine.setMoving(false);
                    return;
                }

                double e = vec3d.x / d;
                double f = vec3d.y / d;
                double g = vec3d.z / d;

                float h = (float)(MathHelper.atan2(vec3d.z, vec3d.x) * 57.2957763671875) - 90.0F;
                this.brine.setYaw(this.wrapDegrees(this.brine.getYaw(), h, 90.0F));
                this.brine.bodyYaw = this.brine.getYaw();

                float i = (float)(this.speed * this.brine.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
                float j = MathHelper.lerp(0.125F, this.brine.getMovementSpeed(), i);
                this.brine.setMovementSpeed(j);

                double k = Math.sin((double)(this.brine.age + this.brine.getId()) * 0.5) * 0.05;
                double l = Math.cos((double)(this.brine.getYaw() * 0.017453292F));
                double m = Math.sin((double)(this.brine.getYaw() * 0.017453292F));
                double n = Math.sin((double)(this.brine.age + this.brine.getId()) * 0.75) * 0.05;

                this.brine.setVelocity(this.brine.getVelocity().add(
                        k * l,
                        n * (m + l) * 0.25 + (double)j * f * 0.1,
                        k * m
                ));

                LookControl lookControl = this.brine.getLookControl();
                double o = this.brine.getX() + e * 2.0;
                double p = this.brine.getEyeY() + f / d;
                double q = this.brine.getZ() + g * 2.0;
                double r = lookControl.getLookX();
                double s = lookControl.getLookY();
                double t = lookControl.getLookZ();

                if (!lookControl.isLookingAtSpecificPosition()) {
                    r = o;
                    s = p;
                    t = q;
                }

                this.brine.getLookControl().lookAt(
                        MathHelper.lerp(0.125, r, o),
                        MathHelper.lerp(0.125, s, p),
                        MathHelper.lerp(0.125, t, q),
                        10.0F, 40.0F
                );

                this.brine.setMoving(true);
            }
            else {
                super.tick();

                if (this.state == State.MOVE_TO) {
                    this.brine.setMoving(true);
                } else {
                    this.brine.setMoving(false);
                }
            }
        }
    }

    public boolean isMoving() {
        return this.dataTracker.get(MOVING);
    }

    void setMoving(boolean moving) {
        this.dataTracker.set(MOVING, moving);
    }

    private void updateAnimations() {
        if (this.getEntityWorld().isClient()) {
            if (this.brineState == BrineState.SHOOTING) {
                if (!isAttackAnimationRunning) {
                    this.attackAnimationState.start(this.age);
                    this.isAttackAnimationRunning = true;
                    this.isIdleAnimationRunning = false;
                    this.isUnderwaterAnimationRunning = false;
                }
            }
            else if (this.brineState == BrineState.IDLE) {
                if (!isIdleAnimationRunning) {
                    --this.idleAnimationTimeout;
                    if (this.idleAnimationTimeout <= 0) {
                        this.idleAnimationTimeout = this.random.nextInt(40) + 80;
                        this.idleAnimationState.start(this.age);
                        this.isIdleAnimationRunning = true;
                        this.isAttackAnimationRunning = false;
                        this.isUnderwaterAnimationRunning = false;
                    }
                }
            }
            else if (this.brineState == BrineState.UNDERWATER_IDLE) {
                if (!isUnderwaterAnimationRunning) {
                    this.underwaterAnimationState.start(this.age);
                    this.isUnderwaterAnimationRunning = true;
                    this.isIdleAnimationRunning = false;
                    this.isAttackAnimationRunning = false;
                }
            }

            if (this.brineState != BrineState.IDLE && isIdleAnimationRunning) {
                this.idleAnimationState.stop();
                this.isIdleAnimationRunning = false;
                this.idleAnimationTimeout = 0;
            }
            if (this.brineState != BrineState.UNDERWATER_IDLE && isUnderwaterAnimationRunning) {
                this.underwaterAnimationState.stop();
                this.isUnderwaterAnimationRunning = false;
            }
            if (this.brineState != BrineState.SHOOTING && isAttackAnimationRunning) {
                this.attackAnimationState.stop();
                this.isAttackAnimationRunning = false;
            }
        }
    }

    public BrineState getBrineState() {
        return brineState;
    }

    public BrineState getPreviousState() {
        return previousState;
    }

    public void setBrineState(BrineState newState) {
        if (this.brineState != newState && !isChangingState) {
            isChangingState = true;

            this.previousState = this.brineState;
            this.brineState = newState;

            if (!this.getEntityWorld().isClient()) {
                this.dataTracker.set(DATA_ID_STATE, newState.ordinal());
            } else {
                startStateAnimation(newState);
            }

            isChangingState = false;
        }
    }

    private void startStateAnimation(BrineState state) {
        if (!this.getEntityWorld().isClient() || animationStartedThisTick) return;

        animationStartedThisTick = true;

        switch (state) {
            case IDLE -> {
                stopAllAnimations();
                this.idleAnimationTimeout = this.random.nextInt(40) + 80;
                this.idleAnimationState.start(this.age);
                this.isIdleAnimationRunning = true;
                this.isUnderwaterAnimationRunning = false;
                this.isAttackAnimationRunning = false;
            }
            case UNDERWATER_IDLE -> {
                stopAllAnimations();
                this.underwaterAnimationState.start(this.age);
                this.isUnderwaterAnimationRunning = true;
                this.isIdleAnimationRunning = false;
                this.isAttackAnimationRunning = false;
            }
            case SHOOTING -> {
                stopAllAnimations();
                this.attackAnimationState.start(this.age);
                this.isAttackAnimationRunning = true;
                this.isIdleAnimationRunning = false;
                this.isUnderwaterAnimationRunning = false;
            }
        }
    }

    private void stopAllAnimations() {
        if (this.getEntityWorld().isClient()) {
            idleAnimationState.stop();
            underwaterAnimationState.stop();
            attackAnimationState.stop();
        }
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (DATA_ID_STATE.equals(data) && this.getEntityWorld().isClient()) {
            BrineState newState = BrineState.values()[this.dataTracker.get(DATA_ID_STATE)];
            if (this.brineState != newState && !isChangingState) {
                isChangingState = true;

                this.previousState = this.brineState;
                this.brineState = newState;

                startStateAnimation(newState);

                isChangingState = false;
            }
        }
        super.onTrackedDataSet(data);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(DATA_ID_STATE, BrineState.UNDERWATER_IDLE.ordinal());
        builder.add(MOVING, false);
    }

    @Override
    public boolean isPushedByFluids() {
        return false;
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    public int getMaxAir() {
        return 300;
    }

    @Override
    protected int getNextAirUnderwater(int air) {
        return this.getMaxAir();
    }

    @Override
    protected int getNextAirOnLand(int air) {
        return this.getMaxAir();
    }

    public boolean canBreatheFluids() {
        return true;
    }

    @Override
    public void baseTick() {
        int maxAir = this.getMaxAir();
        if (this.getAir() < maxAir) {
            this.setAir(maxAir);
        }
        super.baseTick();
    }

    @Override
    protected int getXpToDrop() {
        return 8 + this.random.nextInt(5);
    }

    @Override
    public boolean shouldDropXp() {
        return true;
    }

    @Override
    public void tick() {
        if (this.isRemoved() || this.getEntityWorld() == null) {
            return;
        }

        animationStartedThisTick = false;
        super.tick();

        if (shootCooldown > 0) {
            shootCooldown--;
        }

        if (shootingDelay > 0) {
            shootingDelay--;
        }

        if (this.getBrineState() == BrineState.SHOOTING) {
            shootingStateTimer++;

            if (shootingStateTimer == 20 && !this.getEntityWorld().isClient()) {
                fireWaterCharge();
            }

            if (shootingStateTimer >= 40) {
                setBrineState(this.isFullySubmerged() ?
                        BrineState.UNDERWATER_IDLE : BrineState.IDLE);
                shootingStateTimer = 0;
            }
        } else {
            shootingStateTimer = 0;
        }

        try {
            updateMovementTracking();
            updateAnimations();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void tickMovement() {
        super.tickMovement();

        if (this.isTouchingWater()) {
            this.setAir(300);
        }

        if (this.isSubmergedIn(FluidTags.WATER) || this.touchingWater) {
            handleUnderwaterAnimationState();
        } else {
            handleOutOfWaterAnimationState();
        }
    }

    private void handleUnderwaterAnimationState() {
        if (this.isFullySubmerged()) {
            if (this.brineState != BrineState.UNDERWATER_IDLE && this.brineState != BrineState.SHOOTING) {
                setBrineState(BrineState.UNDERWATER_IDLE);
            }
        } else {
            if (this.brineState != BrineState.IDLE && this.brineState != BrineState.SHOOTING) {
                setBrineState(BrineState.IDLE);
            }
        }
    }

    private void handleOutOfWaterAnimationState() {
        if (this.brineState != BrineState.IDLE) {
            setBrineState(BrineState.IDLE);
        }
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (this.isFullySubmerged()) {
            this.updateVelocity(0.02F, movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.9));

            if (!this.isMoving() && this.getTarget() == null) {
                this.setVelocity(this.getVelocity().add(0.0, -0.003, 0.0));
            }
        } else if (this.isTouchingWater()) {
            this.updateVelocity(0.1F, movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.8));

            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
        } else {
            super.travel(movementInput);
        }
    }

    static class BrineHybridMoveControl extends MoveControl {
        private final BrineEntity brine;

        public BrineHybridMoveControl(BrineEntity brine) {
            super(brine);
            this.brine = brine;
        }

        @Override
        public void tick() {
            if (this.state != State.MOVE_TO) {
                this.brine.setMoving(false);
                this.brine.setMovementSpeed(0.0F);
                return;
            }

            double dx = this.targetX - this.brine.getX();
            double dy = this.targetY - this.brine.getY();
            double dz = this.targetZ - this.brine.getZ();
            double distanceSquared = dx * dx + dy * dy + dz * dz;

            if (distanceSquared < 0.25) {
                this.brine.setMovementSpeed(0.0F);
                this.brine.setMoving(false);
                return;
            }

            double distance = Math.sqrt(distanceSquared);

            float targetYaw = (float)(MathHelper.atan2(dz, dx) * 57.2957763671875) - 90.0F;
            this.brine.setYaw(this.wrapDegrees(this.brine.getYaw(), targetYaw, 90.0F));
            this.brine.bodyYaw = this.brine.getYaw();

            float baseSpeed = (float)(this.speed * this.brine.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
            float lerpedSpeed = MathHelper.lerp(0.125F, this.brine.getMovementSpeed(), baseSpeed);
            this.brine.setMovementSpeed(lerpedSpeed);

            if (this.brine.isFullySubmerged()) {
                double nx = dx / distance;
                double ny = dy / distance;
                double nz = dz / distance;

                double wave1 = Math.sin((double)(this.brine.age + this.brine.getId()) * 0.5) * 0.05;
                double wave2 = Math.sin((double)(this.brine.age + this.brine.getId()) * 0.75) * 0.05;
                double yawCos = Math.cos((double)(this.brine.getYaw() * 0.017453292F));
                double yawSin = Math.sin((double)(this.brine.getYaw() * 0.017453292F));

                Vec3d currentVel = this.brine.getVelocity();
                this.brine.setVelocity(currentVel.add(
                        nx * baseSpeed * 0.1 + wave1 * yawCos,
                        ny * baseSpeed * 0.1 + wave2 * (yawSin + yawCos) * 0.25,
                        nz * baseSpeed * 0.1 + wave1 * yawSin
                ));

                this.brine.getLookControl().lookAt(this.targetX, this.targetY, this.targetZ, 10.0F, 40.0F);
                this.brine.setMoving(true);
            } else {
                this.brine.setForwardSpeed(lerpedSpeed);
                this.brine.setMoving(true);
            }
        }
    }

    public static class BrineHybridSwimGoal extends Goal {
        private final BrineEntity brine;
        private final double speed;
        private double targetX;
        private double targetY;
        private double targetZ;
        private int retargetTimer;
        private boolean usingVelocityMode;

        public BrineHybridSwimGoal(BrineEntity brine, double speed) {
            this.brine = brine;
            this.speed = speed;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (!this.brine.isTouchingWater() || this.brine.getTarget() != null) {
                return false;
            }
            return this.brine.getRandom().nextInt(40) == 0;
        }

        @Override
        public boolean shouldContinue() {
            return this.brine.isTouchingWater() && this.retargetTimer > 0;
        }

        @Override
        public void start() {
            this.pickNewTarget();
            this.retargetTimer = 200;
        }

        @Override
        public void stop() {
            this.retargetTimer = 0;
            this.brine.getMoveControl().moveTo(this.brine.getX(), this.brine.getY(), this.brine.getZ(), this.speed);
        }

        @Override
        public void tick() {
            this.retargetTimer--;

            double distSq = this.brine.squaredDistanceTo(this.targetX, this.targetY, this.targetZ);

            if (distSq < 2.0 || this.retargetTimer <= 0 || this.retargetTimer % 80 == 0) {
                this.pickNewTarget();
                this.retargetTimer = 200;
            }

            boolean nowFullySubmerged = this.brine.isFullySubmerged();
            if (nowFullySubmerged != this.usingVelocityMode) {
                this.usingVelocityMode = nowFullySubmerged;
                this.applyMovement();
            }
        }

        private void pickNewTarget() {
            Vec3d pos = this.brine.getPos();
            int range = 15;

            for (int i = 0; i < 30; i++) {
                double ox = (this.brine.getRandom().nextDouble() - 0.5) * range * 2;
                double oy = (this.brine.getRandom().nextDouble() - 0.5) * range * 2;
                double oz = (this.brine.getRandom().nextDouble() - 0.5) * range * 2;

                double px = pos.x + ox;
                double py = pos.y + oy;
                double pz = pos.z + oz;

                if (ox * ox + oy * oy + oz * oz < 49) continue;

                BlockPos testPos = new BlockPos((int)px, (int)py, (int)pz);

                if (!this.brine.isWithinHomeBounds(testPos)) continue;

                if (this.brine.getEntityWorld().getFluidState(testPos).isIn(FluidTags.WATER)) {
                    this.targetX = px;
                    this.targetY = py;
                    this.targetZ = pz;
                    this.usingVelocityMode = this.brine.isFullySubmerged();
                    this.applyMovement();
                    return;
                }
            }
        }

        private void applyMovement() {
            if (this.usingVelocityMode) {
                this.brine.getMoveControl().moveTo(this.targetX, this.targetY, this.targetZ, this.speed);
            } else {
                this.brine.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
            }
        }
    }

    public boolean isKelpCovered() {
        return true;
    }

    public float getLeftVisionAngle() {
        return this.getYaw() - 90.0F;
    }

    private void updateMovementTracking() {
        if (lastShootPosition != null) {
            Vec3d currentPos = this.getPos();
            if (currentPos != null) {
                double distanceMoved = currentPos.distanceTo(lastShootPosition);

                if (distanceMoved >= 4.0) {
                    hasMovedEnoughToShoot = true;
                }
            }
        }
    }

    private boolean isNearbyProjectileDangerous() {
        if (this.getEntityWorld() == null) return false;

        try {
            List<WaterChargeProjectileEntity> projectiles = this.getEntityWorld().getEntitiesByClass(
                    WaterChargeProjectileEntity.class,
                    this.getBoundingBox().expand(PROJECTILE_DANGER_RADIUS),
                    projectile -> projectile != null && projectile.getOwner() != this
            );

            return !projectiles.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isNearFriendlyProjectile() {
        if (this.getEntityWorld() == null) return false;

        try {
            List<WaterChargeProjectileEntity> friendlyProjectiles = this.getEntityWorld().getEntitiesByClass(
                    WaterChargeProjectileEntity.class,
                    this.getBoundingBox().expand(FRIENDLY_PROJECTILE_AVOIDANCE_RADIUS),
                    projectile -> projectile != null && projectile.getOwner() instanceof BrineEntity && projectile.getOwner() != this
            );

            return !friendlyProjectiles.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private Vec3d getProjectileAvoidanceDirection() {
        if (this.getEntityWorld() == null) return null;

        try {
            List<WaterChargeProjectileEntity> projectiles = this.getEntityWorld().getEntitiesByClass(
                    WaterChargeProjectileEntity.class,
                    this.getBoundingBox().expand(Math.max(PROJECTILE_DANGER_RADIUS, FRIENDLY_PROJECTILE_AVOIDANCE_RADIUS)),
                    projectile -> projectile != null && projectile.getOwner() != this
            );

            if (projectiles.isEmpty()) return null;

            Vec3d avoidanceDirection = Vec3d.ZERO;
            for (WaterChargeProjectileEntity projectile : projectiles) {
                if (projectile != null && projectile.getPos() != null) {
                    Vec3d directionAway = this.getPos().subtract(projectile.getPos()).normalize();
                    double weight = (projectile.getOwner() instanceof BrineEntity) ? 1.5 : 1.0;
                    avoidanceDirection = avoidanceDirection.add(directionAway.multiply(weight));
                }
            }

            return avoidanceDirection.normalize();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean canShoot() {
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive() || target.isRemoved()) return false;
        if (!this.isFullySubmerged()) return false;
        if (brineState == BrineState.SHOOTING) return false;

        try {
            double distance = this.distanceTo(target);
            if (distance < 4.0 || distance > 16.0) return false;
            if (!hasMovedEnoughToShoot || isNearbyProjectileDangerous()) return false;
            if (isNearFriendlyProjectile()) return false;

            return shootingDelay <= 0;
        } catch (Exception e) {
            return false;
        }
    }

    public void tryShootAtTarget() {
        if (shootCooldown > 0 || !canShoot()) return;

        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive() || target.isRemoved()) return;

        shootCooldown = 40 + this.random.nextInt(20);
        this.setBrineState(BrineState.SHOOTING);
        lastShootPosition = this.getPos();
        hasMovedEnoughToShoot = false;
        shootingDelay = 5 + this.random.nextInt(10);
    }

    private void fireWaterCharge() {
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive() || target.isRemoved()) return;

        Vec3d targetPos = predictTargetPosition(target);
        if (targetPos == null) return;

        Vec3d direction = targetPos.subtract(this.getPos()).normalize();

        try {
            WaterChargeProjectileEntity charge = new WaterChargeProjectileEntity(ModEntities.WATER_CHARGE, this.getEntityWorld());
            charge.setOwner(this);
            charge.setPosition(this.getX(), this.getEyeY(), this.getZ());
            charge.setVelocity(direction.x, direction.y, direction.z, 1.2f, 0.05f);
            this.getEntityWorld().spawnEntity(charge);
        } catch (Exception e) {
        }
    }

    private Vec3d predictTargetPosition(LivingEntity target) {
        if (target == null || !target.isAlive() || target.isRemoved()) {
            return this.getPos();
        }

        try {
            Vec3d targetVelocity = target.getVelocity();
            if (targetVelocity == null) {
                targetVelocity = Vec3d.ZERO;
            }

            double projectileSpeed = 1.2;
            double distance = this.distanceTo(target);
            double timeToHit = distance / projectileSpeed;

            Vec3d predictedPos = target.getPos().add(targetVelocity.multiply(timeToHit));
            return predictedPos.add(0, target.getStandingEyeHeight() - 1.0, 0);
        } catch (Exception e) {
            return target.getPos();
        }
    }

    private static class BrineAvoidProjectileGoal extends Goal {
        private final BrineEntity brine;
        private Vec3d avoidanceDirection;

        public BrineAvoidProjectileGoal(BrineEntity brine) {
            this.brine = brine;
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (brine.getBrineState() != BrineState.UNDERWATER_IDLE &&
                    brine.getBrineState() != BrineState.IDLE) return false;

            avoidanceDirection = brine.getProjectileAvoidanceDirection();
            return avoidanceDirection != null;
        }

        @Override
        public void tick() {
            if (avoidanceDirection != null) {
                Vec3d targetPos = brine.getPos().add(avoidanceDirection.multiply(6.0));
                brine.getMoveControl().moveTo(targetPos.x, targetPos.y, targetPos.z, 1.5);
            }
        }

        @Override
        public boolean shouldContinue() {
            return (brine.isNearbyProjectileDangerous() || brine.isNearFriendlyProjectile()) &&
                    (brine.getBrineState() == BrineState.UNDERWATER_IDLE ||
                            brine.getBrineState() == BrineState.IDLE);
        }
    }

    private static class BrineShootGoal extends Goal {
        private final BrineEntity brine;
        private int aimTimer = 0;

        public BrineShootGoal(BrineEntity brine) {
            this.brine = brine;
            this.setControls(EnumSet.of(Control.LOOK));
        }

        @Override
        public boolean canStart() {
            LivingEntity target = brine.getTarget();
            return target != null &&
                    brine.shootCooldown <= 0 &&
                    brine.canShoot() &&
                    brine.distanceTo(target) >= 4.0 &&
                    brine.distanceTo(target) <= 16.0f &&
                    (brine.getBrineState() == BrineState.UNDERWATER_IDLE ||
                            brine.getBrineState() == BrineState.IDLE);
        }

        @Override
        public void start() {
            aimTimer = 15;
        }

        @Override
        public void tick() {
            LivingEntity target = brine.getTarget();
            if (target != null && target.isAlive() && !target.isRemoved() &&
                    brine.getBrineState() != BrineState.SHOOTING) {
                try {
                    brine.getLookControl().lookAt(target.getX(), target.getEyeY(), target.getZ());

                    if (--aimTimer <= 0) {
                        brine.tryShootAtTarget();
                        aimTimer = 60;
                    }
                } catch (Exception e) {
                    aimTimer = 60;
                }
            }
        }

        @Override
        public boolean shouldContinue() {
            LivingEntity target = brine.getTarget();
            return target != null &&
                    brine.getBrineState() != BrineState.SHOOTING &&
                    brine.distanceTo(target) >= 4.0 &&
                    brine.distanceTo(target) <= 16.0f;
        }

        @Override
        public void stop() {
            aimTimer = 0;
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString("BrineState", brineState.name());
        nbt.putBoolean("HasMovedEnoughToShoot", hasMovedEnoughToShoot);
        nbt.putInt("ShootingDelay", shootingDelay);
        nbt.putInt("ShootCooldown", shootCooldown);

        if (lastShootPosition != null) {
            nbt.putDouble("LastShootX", lastShootPosition.x);
            nbt.putDouble("LastShootY", lastShootPosition.y);
            nbt.putDouble("LastShootZ", lastShootPosition.z);
        }

        if (homePos != null) {
            nbt.putInt("HomeX", homePos.getX());
            nbt.putInt("HomeY", homePos.getY());
            nbt.putInt("HomeZ", homePos.getZ());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        String stateString = nbt.getString("BrineState");
        if (!stateString.isEmpty() && !stateString.equals("UNDERWATER_IDLE")) {
            try {
                BrineState loadedState = BrineState.valueOf(stateString);
                this.brineState = loadedState;
                if (!this.getWorld().isClient()) {
                    this.dataTracker.set(DATA_ID_STATE, loadedState.ordinal());
                }
            } catch (IllegalArgumentException e) {
                this.brineState = BrineState.UNDERWATER_IDLE;
            }
        }

        this.hasMovedEnoughToShoot = nbt.getBoolean("HasMovedEnoughToShoot");
        this.shootingDelay = nbt.getInt("ShootingDelay");
        this.shootCooldown = nbt.getInt("ShootCooldown");

        if (nbt.contains("LastShootX")) {
            this.lastShootPosition = new Vec3d(
                    nbt.getDouble("LastShootX"),
                    nbt.getDouble("LastShootY"),
                    nbt.getDouble("LastShootZ")
            );
        }

        if (nbt.contains("HomeX")) {
            this.homePos = new BlockPos(
                    nbt.getInt("HomeX"),
                    nbt.getInt("HomeY"),
                    nbt.getInt("HomeZ")
            );
        }
    }
}