package potatowolfie.earth_and_water.entity.brine;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.fish.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import potatowolfie.earth_and_water.entity.ModEntities;
import potatowolfie.earth_and_water.entity.goals.HostileWaterCreatureEntity;
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
    private Vec3 lastShootPosition = null;
    private boolean hasMovedEnoughToShoot = true;

    private BlockPos homePos = null;
    private static final int MAX_DISTANCE_FROM_HOME = 30;

    public enum BrineState {
        IDLE,
        UNDERWATER_IDLE,
        SHOOTING
    }

    private static final EntityDataAccessor<Integer> DATA_ID_STATE =
            SynchedEntityData.defineId(BrineEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> MOVING =
            SynchedEntityData.defineId(BrineEntity.class, EntityDataSerializers.BOOLEAN);

    private BrineState brineState = BrineState.UNDERWATER_IDLE;
    private BrineState previousState = BrineState.UNDERWATER_IDLE;
    private boolean isChangingState = false;

    public BrineEntity(EntityType<? extends BrineEntity> entityType, Level world) {
        super(entityType, world);
        this.moveControl = new BrineHybridMoveControl(this);
    }

    public static AttributeSupplier.Builder createBrineAttributes() {
        return WaterAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23000000417232513);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new BrineAvoidProjectileGoal(this));
        this.goalSelector.addGoal(1, new BrineSeekWaterGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new BrineHybridSwimGoal(this, 1.25D));
        this.goalSelector.addGoal(3, new BrineShootGoal(this));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0, 80));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.wasTouchingWater ? ModSounds.BRINE_UNDERWATER_DEATH : ModSounds.BRINE_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return this.wasTouchingWater ? ModSounds.BRINE_UNDERWATER_DEATH : ModSounds.BRINE_DEATH;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.wasTouchingWater ? ModSounds.BRINE_UNDERWATER_AMBIENT : ModSounds.BRINE_AMBIENT;
    }

    public void setHomePosition(BlockPos pos) {
        this.homePos = pos;
    }

    private boolean isWithinHomeBounds(BlockPos pos) {
        if (this.homePos == null) {
            return true;
        }
        return this.homePos.closerThan(pos, MAX_DISTANCE_FROM_HOME);
    }

    private boolean isWithinHomeBounds(Vec3 pos) {
        if (this.homePos == null) {
            return true;
        }
        return this.homePos.closerToCenterThan(pos, MAX_DISTANCE_FROM_HOME);
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        return new BrineNavigation(this, world);
    }

    public static boolean canSpawn(
            EntityType<BrineEntity> type,
            ServerLevelAccessor world,
            EntitySpawnReason spawnReason,
            BlockPos pos,
            RandomSource random
    ) {
        return world.getFluidState(pos).is(FluidTags.WATER);
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
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.brine.isVehicle() || this.brine.getTarget() != null) {
                return false;
            }

            if (this.brine.getRandom().nextInt(this.chance) != 0) {
                return false;
            }

            return this.brine.isInWater();
        }

        @Override
        public void start() {
            this.chooseWaterTarget();
            this.ticksSinceLastTarget = 0;
        }

        @Override
        public boolean canContinueToUse() {
            return this.brine.isInWater();
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
                    this.brine.distanceToSqr(this.targetX, this.targetY, this.targetZ) < 1.0D) {
                this.chooseWaterTarget();
                this.ticksSinceLastTarget = 0;
            }
        }

        private void chooseWaterTarget() {
            Vec3 currentPos = this.brine.position();
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

                if (this.brine.level().getFluidState(checkPos).is(FluidTags.WATER)) {
                    this.targetX = potentialX;
                    this.targetY = potentialY;
                    this.targetZ = potentialZ;

                    if (this.brine.isFullySubmerged()) {
                        this.brine.getMoveControl().setWantedPosition(this.targetX, this.targetY, this.targetZ, this.speed);
                    } else {
                        this.brine.getNavigation().moveTo(this.targetX, this.targetY, this.targetZ, this.speed);
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
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.brine.isInWater()) {
                return false;
            }

            this.targetWaterPos = findDeepWater();
            return this.targetWaterPos != null;
        }

        @Override
        public boolean canContinueToUse() {
            return !this.brine.isInWater() &&
                    this.targetWaterPos != null &&
                    !this.brine.getNavigation().isDone();
        }

        @Override
        public void start() {
            if (this.targetWaterPos != null) {
                this.brine.getNavigation().moveTo(
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
            BlockPos entityPos = this.brine.blockPosition();
            int searchRange = 16;

            for (int range = 4; range <= searchRange; range += 4) {
                for (int x = -range; x <= range; x += 2) {
                    for (int y = -8; y <= 8; y += 2) {
                        for (int z = -range; z <= range; z += 2) {
                            BlockPos checkPos = entityPos.offset(x, y, z);

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
            Level world = this.brine.level();
            return world.getFluidState(pos).is(FluidTags.WATER) &&
                    world.getFluidState(pos.above()).is(FluidTags.WATER);
        }
    }

    static class BrineMoveControl extends MoveControl {
        private final BrineEntity brine;

        public BrineMoveControl(BrineEntity brine) {
            super(brine);
            this.brine = brine;
        }

        public void tick() {
            if (this.brine.isFullySubmerged() && this.operation == Operation.MOVE_TO) {
                Vec3 vec3d = new Vec3(this.wantedX - this.brine.getX(),
                        this.wantedY - this.brine.getY(),
                        this.wantedZ - this.brine.getZ());
                double d = vec3d.length();

                if (d < 0.5) {
                    this.operation = Operation.WAIT;
                    this.brine.setMoving(false);
                    return;
                }

                double e = vec3d.x / d;
                double f = vec3d.y / d;
                double g = vec3d.z / d;

                float h = (float)(Mth.atan2(vec3d.z, vec3d.x) * 57.2957763671875) - 90.0F;
                this.brine.setYRot(this.rotlerp(this.brine.getYRot(), h, 90.0F));
                this.brine.yBodyRot = this.brine.getYRot();

                float i = (float)(this.speedModifier * this.brine.getAttributeValue(Attributes.MOVEMENT_SPEED));
                float j = Mth.lerp(0.125F, this.brine.getSpeed(), i);
                this.brine.setSpeed(j);

                double k = Math.sin((double)(this.brine.tickCount + this.brine.getId()) * 0.5) * 0.05;
                double l = Math.cos((double)(this.brine.getYRot() * 0.017453292F));
                double m = Math.sin((double)(this.brine.getYRot() * 0.017453292F));
                double n = Math.sin((double)(this.brine.tickCount + this.brine.getId()) * 0.75) * 0.05;

                this.brine.setDeltaMovement(this.brine.getDeltaMovement().add(
                        k * l,
                        n * (m + l) * 0.25 + (double)j * f * 0.1,
                        k * m
                ));

                LookControl lookControl = this.brine.getLookControl();
                double o = this.brine.getX() + e * 2.0;
                double p = this.brine.getEyeY() + f / d;
                double q = this.brine.getZ() + g * 2.0;
                double r = lookControl.getWantedX();
                double s = lookControl.getWantedY();
                double t = lookControl.getWantedZ();

                if (!lookControl.isLookingAtTarget()) {
                    r = o;
                    s = p;
                    t = q;
                }

                this.brine.getLookControl().setLookAt(
                        Mth.lerp(0.125, r, o),
                        Mth.lerp(0.125, s, p),
                        Mth.lerp(0.125, t, q),
                        10.0F, 40.0F
                );

                this.brine.setMoving(true);
            }
            else {
                super.tick();

                if (this.operation == Operation.MOVE_TO) {
                    this.brine.setMoving(true);
                } else {
                    this.brine.setMoving(false);
                }
            }
        }
    }

    public boolean isMoving() {
        return this.entityData.get(MOVING);
    }

    void setMoving(boolean moving) {
        this.entityData.set(MOVING, moving);
    }

    private void updateAnimations() {
        if (this.level().isClientSide()) {
            if (this.brineState == BrineState.SHOOTING) {
                if (!isAttackAnimationRunning) {
                    this.attackAnimationState.start(this.tickCount);
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
                        this.idleAnimationState.start(this.tickCount);
                        this.isIdleAnimationRunning = true;
                        this.isAttackAnimationRunning = false;
                        this.isUnderwaterAnimationRunning = false;
                    }
                }
            }
            else if (this.brineState == BrineState.UNDERWATER_IDLE) {
                if (!isUnderwaterAnimationRunning) {
                    this.underwaterAnimationState.start(this.tickCount);
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

            if (!this.level().isClientSide()) {
                this.entityData.set(DATA_ID_STATE, newState.ordinal());
            } else {
                startStateAnimation(newState);
            }

            isChangingState = false;
        }
    }

    private void startStateAnimation(BrineState state) {
        if (!this.level().isClientSide() || animationStartedThisTick) return;

        animationStartedThisTick = true;

        switch (state) {
            case IDLE -> {
                stopAllAnimations();
                this.idleAnimationTimeout = this.random.nextInt(40) + 80;
                this.idleAnimationState.start(this.tickCount);
                this.isIdleAnimationRunning = true;
                this.isUnderwaterAnimationRunning = false;
                this.isAttackAnimationRunning = false;
            }
            case UNDERWATER_IDLE -> {
                stopAllAnimations();
                this.underwaterAnimationState.start(this.tickCount);
                this.isUnderwaterAnimationRunning = true;
                this.isIdleAnimationRunning = false;
                this.isAttackAnimationRunning = false;
            }
            case SHOOTING -> {
                stopAllAnimations();
                this.attackAnimationState.start(this.tickCount);
                this.isAttackAnimationRunning = true;
                this.isIdleAnimationRunning = false;
                this.isUnderwaterAnimationRunning = false;
            }
        }
    }

    private void stopAllAnimations() {
        if (this.level().isClientSide()) {
            idleAnimationState.stop();
            underwaterAnimationState.stop();
            attackAnimationState.stop();
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        if (DATA_ID_STATE.equals(data) && this.level().isClientSide()) {
            BrineState newState = BrineState.values()[this.entityData.get(DATA_ID_STATE)];
            if (this.brineState != newState && !isChangingState) {
                isChangingState = true;

                this.previousState = this.brineState;
                this.brineState = newState;

                startStateAnimation(newState);

                isChangingState = false;
            }
        }
        super.onSyncedDataUpdated(data);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_STATE, BrineState.UNDERWATER_IDLE.ordinal());
        builder.define(MOVING, false);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    public int getMaxAirSupply() {
        return 300;
    }

    @Override
    protected int decreaseAirSupply(int air) {
        return this.getMaxAirSupply();
    }

    @Override
    protected int increaseAirSupply(int air) {
        return this.getMaxAirSupply();
    }

    public boolean canBreatheFluids() {
        return true;
    }

    @Override
    public void baseTick() {
        int maxAir = this.getMaxAirSupply();
        if (this.getAirSupply() < maxAir) {
            this.setAirSupply(maxAir);
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
        if (this.isRemoved() || this.level() == null) {
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

            if (shootingStateTimer == 20 && !this.level().isClientSide()) {
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
    public void aiStep() {
        super.aiStep();

        if (this.isInWater()) {
            this.setAirSupply(300);
        }

        if (this.isEyeInFluid(FluidTags.WATER) || this.wasTouchingWater) {
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
    public void travel(Vec3 movementInput) {
        if (this.isFullySubmerged()) {
            this.moveRelative(0.02F, movementInput);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));

            if (!this.isMoving() && this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.003, 0.0));
            }
        } else if (this.isInWater()) {
            this.moveRelative(0.1F, movementInput);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.8));

            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
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
            if (this.operation != Operation.MOVE_TO) {
                this.brine.setMoving(false);
                this.brine.setSpeed(0.0F);
                return;
            }

            double dx = this.wantedX - this.brine.getX();
            double dy = this.wantedY - this.brine.getY();
            double dz = this.wantedZ - this.brine.getZ();
            double distanceSquared = dx * dx + dy * dy + dz * dz;

            if (distanceSquared < 0.25) {
                this.brine.setSpeed(0.0F);
                this.brine.setMoving(false);
                return;
            }

            double distance = Math.sqrt(distanceSquared);

            float targetYaw = (float)(Mth.atan2(dz, dx) * 57.2957763671875) - 90.0F;
            this.brine.setYRot(this.rotlerp(this.brine.getYRot(), targetYaw, 90.0F));
            this.brine.yBodyRot = this.brine.getYRot();

            float baseSpeed = (float)(this.speedModifier * this.brine.getAttributeValue(Attributes.MOVEMENT_SPEED));
            float lerpedSpeed = Mth.lerp(0.125F, this.brine.getSpeed(), baseSpeed);
            this.brine.setSpeed(lerpedSpeed);

            if (this.brine.isFullySubmerged()) {
                double nx = dx / distance;
                double ny = dy / distance;
                double nz = dz / distance;

                double wave1 = Math.sin((double)(this.brine.tickCount + this.brine.getId()) * 0.5) * 0.05;
                double wave2 = Math.sin((double)(this.brine.tickCount + this.brine.getId()) * 0.75) * 0.05;
                double yawCos = Math.cos((double)(this.brine.getYRot() * 0.017453292F));
                double yawSin = Math.sin((double)(this.brine.getYRot() * 0.017453292F));

                Vec3 currentVel = this.brine.getDeltaMovement();
                this.brine.setDeltaMovement(currentVel.add(
                        nx * baseSpeed * 0.1 + wave1 * yawCos,
                        ny * baseSpeed * 0.1 + wave2 * (yawSin + yawCos) * 0.25,
                        nz * baseSpeed * 0.1 + wave1 * yawSin
                ));

                this.brine.getLookControl().setLookAt(this.wantedX, this.wantedY, this.wantedZ, 10.0F, 40.0F);
                this.brine.setMoving(true);
            } else {
                this.brine.setZza(lerpedSpeed);
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
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!this.brine.isInWater() || this.brine.getTarget() != null) {
                return false;
            }
            return this.brine.getRandom().nextInt(40) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return this.brine.isInWater() && this.retargetTimer > 0;
        }

        @Override
        public void start() {
            this.pickNewTarget();
            this.retargetTimer = 200;
        }

        @Override
        public void stop() {
            this.retargetTimer = 0;
            this.brine.getMoveControl().setWantedPosition(this.brine.getX(), this.brine.getY(), this.brine.getZ(), this.speed);
        }

        @Override
        public void tick() {
            this.retargetTimer--;

            double distSq = this.brine.distanceToSqr(this.targetX, this.targetY, this.targetZ);

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
            Vec3 pos = this.brine.position();
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

                if (this.brine.level().getFluidState(testPos).is(FluidTags.WATER)) {
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
                this.brine.getMoveControl().setWantedPosition(this.targetX, this.targetY, this.targetZ, this.speed);
            } else {
                this.brine.getNavigation().moveTo(this.targetX, this.targetY, this.targetZ, this.speed);
            }
        }
    }

    public boolean isKelpCovered() {
        return true;
    }

    public float getLeftVisionAngle() {
        return this.getYRot() - 90.0F;
    }

    private void updateMovementTracking() {
        if (lastShootPosition != null) {
            Vec3 currentPos = this.position();
            if (currentPos != null) {
                double distanceMoved = currentPos.distanceTo(lastShootPosition);

                if (distanceMoved >= 4.0) {
                    hasMovedEnoughToShoot = true;
                }
            }
        }
    }

    private boolean isNearbyProjectileDangerous() {
        if (this.level() == null) return false;

        try {
            List<WaterChargeProjectileEntity> projectiles = this.level().getEntitiesOfClass(
                    WaterChargeProjectileEntity.class,
                    this.getBoundingBox().inflate(PROJECTILE_DANGER_RADIUS),
                    projectile -> projectile != null && projectile.getOwner() != this
            );

            return !projectiles.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isNearFriendlyProjectile() {
        if (this.level() == null) return false;

        try {
            List<WaterChargeProjectileEntity> friendlyProjectiles = this.level().getEntitiesOfClass(
                    WaterChargeProjectileEntity.class,
                    this.getBoundingBox().inflate(FRIENDLY_PROJECTILE_AVOIDANCE_RADIUS),
                    projectile -> projectile != null && projectile.getOwner() instanceof BrineEntity && projectile.getOwner() != this
            );

            return !friendlyProjectiles.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private Vec3 getProjectileAvoidanceDirection() {
        if (this.level() == null) return null;

        try {
            List<WaterChargeProjectileEntity> projectiles = this.level().getEntitiesOfClass(
                    WaterChargeProjectileEntity.class,
                    this.getBoundingBox().inflate(Math.max(PROJECTILE_DANGER_RADIUS, FRIENDLY_PROJECTILE_AVOIDANCE_RADIUS)),
                    projectile -> projectile != null && projectile.getOwner() != this
            );

            if (projectiles.isEmpty()) return null;

            Vec3 avoidanceDirection = Vec3.ZERO;
            for (WaterChargeProjectileEntity projectile : projectiles) {
                if (projectile != null && projectile.position() != null) {
                    Vec3 directionAway = this.position().subtract(projectile.position()).normalize();
                    double weight = (projectile.getOwner() instanceof BrineEntity) ? 1.5 : 1.0;
                    avoidanceDirection = avoidanceDirection.add(directionAway.scale(weight));
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
        lastShootPosition = this.position();
        hasMovedEnoughToShoot = false;
        shootingDelay = 5 + this.random.nextInt(10);
    }

    private void fireWaterCharge() {
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive() || target.isRemoved()) return;

        Vec3 targetPos = predictTargetPosition(target);
        if (targetPos == null) return;

        Vec3 direction = targetPos.subtract(this.position()).normalize();

        try {
            WaterChargeProjectileEntity charge = new WaterChargeProjectileEntity(ModEntities.WATER_CHARGE, this.level());
            charge.setOwner(this);
            charge.setPos(this.getX(), this.getEyeY(), this.getZ());
            charge.shoot(direction.x, direction.y, direction.z, 1.2f, 0.05f);
            this.level().addFreshEntity(charge);
        } catch (Exception e) {
        }
    }

    private Vec3 predictTargetPosition(LivingEntity target) {
        if (target == null || !target.isAlive() || target.isRemoved()) {
            return this.position();
        }

        try {
            Vec3 targetVelocity = target.getDeltaMovement();
            if (targetVelocity == null) {
                targetVelocity = Vec3.ZERO;
            }

            double projectileSpeed = 1.2;
            double distance = this.distanceTo(target);
            double timeToHit = distance / projectileSpeed;

            Vec3 predictedPos = target.position().add(targetVelocity.scale(timeToHit));
            return predictedPos.add(0, target.getEyeHeight() - 1.0, 0);
        } catch (Exception e) {
            return target.position();
        }
    }

    private static class BrineAvoidProjectileGoal extends Goal {
        private final BrineEntity brine;
        private Vec3 avoidanceDirection;

        public BrineAvoidProjectileGoal(BrineEntity brine) {
            this.brine = brine;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (brine.getBrineState() != BrineState.UNDERWATER_IDLE &&
                    brine.getBrineState() != BrineState.IDLE) return false;

            avoidanceDirection = brine.getProjectileAvoidanceDirection();
            return avoidanceDirection != null;
        }

        @Override
        public void tick() {
            if (avoidanceDirection != null) {
                Vec3 targetPos = brine.position().add(avoidanceDirection.scale(6.0));
                brine.getMoveControl().setWantedPosition(targetPos.x, targetPos.y, targetPos.z, 1.5);
            }
        }

        @Override
        public boolean canContinueToUse() {
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
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        @Override
        public boolean canUse() {
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
                    brine.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ());

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
        public boolean canContinueToUse() {
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
    public void addAdditionalSaveData(ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
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
    public void readAdditionalSaveData(ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        String stateString = nbt.getStringOr("BrineState", "UNDERWATER_IDLE");
        if (!stateString.equals("UNDERWATER_IDLE")) {
            try {
                BrineState loadedState = BrineState.valueOf(stateString);
                this.brineState = loadedState;
                if (!this.level().isClientSide()) {
                    this.entityData.set(DATA_ID_STATE, loadedState.ordinal());
                }
            } catch (IllegalArgumentException e) {
                this.brineState = BrineState.UNDERWATER_IDLE;
            }
        }

        this.hasMovedEnoughToShoot = nbt.getBooleanOr("HasMovedEnoughToShoot", false);
        this.shootingDelay = nbt.getIntOr("ShootingDelay", 0);
        this.shootCooldown = nbt.getIntOr("ShootCooldown", 0);

        double lastShootX = nbt.getDoubleOr("LastShootX", Double.NaN);
        if (!Double.isNaN(lastShootX)) {
            this.lastShootPosition = new Vec3(
                    lastShootX,
                    nbt.getDoubleOr("LastShootY", 0.0),
                    nbt.getDoubleOr("LastShootZ", 0.0)
            );
        }

        if (nbt.contains("HomeX")) {
            this.homePos = new BlockPos(
                    nbt.getIntOr("HomeX", 0),
                    nbt.getIntOr("HomeY", 0),
                    nbt.getIntOr("HomeZ", 0)
            );
        }
    }
}