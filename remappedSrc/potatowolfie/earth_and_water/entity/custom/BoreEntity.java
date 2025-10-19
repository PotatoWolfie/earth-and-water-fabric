package potatowolfie.earth_and_water.entity.custom;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class BoreEntity extends HostileEntity {

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState shootingAnimationState = new AnimationState();
    public final AnimationState burrowingAnimationState = new AnimationState();
    public final AnimationState unburrowingAnimationState = new AnimationState();
    public final AnimationState whileburrowingAnimationState = new AnimationState();

    private int idleAnimationTimeout = 0;
    private boolean isIdleAnimationRunning = false;

    private BlockPos burrowDestination = null;
    private boolean isWalkingWhileBurrowed = false;
    private int burrowCooldown = 0;
    private int nextBurrowTime = 0;
    private int burrowCooldownTimer = 0;
    private static final int BURROW_COOLDOWN_TICKS = 80;
    private boolean burrowAnimPlayed = false;
    private boolean animationStartedThisTick = false;
    private boolean whileburrowAnimPlayed = false;

    private int shootingDelay = 0;
    private static final double PROJECTILE_DANGER_RADIUS = 6.0;
    private static final double FRIENDLY_PROJECTILE_AVOIDANCE_RADIUS = 8.0;

    private boolean isInCombat = false;
    private int combatStartTime = 0;
    private LivingEntity lastTarget = null;
    private Vec3d circlingCenter = null;
    private double circlingAngle = 0;
    private int circlingDirection = 1;

    private Vec3d stuckCheckPosition = null;
    private int stuckTimer = 0;
    private static final int STUCK_TIME_THRESHOLD = 60;
    private static final double STUCK_AREA_SIZE = 2.5;

    protected int getXpToDrop() {
        return 8 + this.random.nextInt(5);
    }

    public AnimationState getAnimationState(String name) {
        return switch (name) {
            case "BORE_SHOOTING" -> shootingAnimationState;
            case "BORE_BURROWING" -> burrowingAnimationState;
            case "BORE_WHILE_BURROWING" -> whileburrowingAnimationState;
            case "BORE_UNBURROWING" -> unburrowingAnimationState;
            default -> idleAnimationState;
        };
    }

    public enum BoreState {
        IDLE,
        SHOOTING,
        BURROWING,
        UNBURROWING
    }

    public enum BoreVariant {
        NORMAL(0),
        DARK(1);

        private final int id;

        BoreVariant(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static BoreVariant byId(int id) {
            for (BoreVariant variant : values()) {
                if (variant.getId() == id) {
                    return variant;
                }
            }
            return NORMAL;
        }
    }

    private static final TrackedData<Integer> DATA_ID_TYPE_VARIANT =
            DataTracker.registerData(BoreEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private BoreState boreState = BoreState.IDLE;
    private BoreState previousState = BoreState.IDLE;
    private int animationTick = 0;
    private int stateTimer = 0;
    private int shootCooldown = 0;
    private BlockPos relocateTarget = null;
    private Vec3d lastShootPosition = null;
    private boolean hasMovedEnoughToShoot = true;

    public BoreEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 8;
    }

    public static DefaultAttributeContainer.Builder createBoreAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 30.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.23)
                .add(EntityAttributes.ATTACK_DAMAGE, 8.0)
                .add(EntityAttributes.FOLLOW_RANGE, 32.0)
                .add(EntityAttributes.JUMP_STRENGTH, 0.42);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new BoreAvoidProjectileGoal(this));
        this.goalSelector.add(1, new BoreBurrowingMovementGoal(this));
        this.goalSelector.add(2, new BoreShootGoal(this));
        this.goalSelector.add(3, new BoreCircleGoal(this));
        this.goalSelector.add(4, new BoreRelocateGoal(this));
        this.goalSelector.add(5, new BoreFleeGoal(this));
        this.goalSelector.add(6, new BoreSmartPositioningGoal(this));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(9, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(3, new RevengeGoal(this, BoreEntity.class));
    }

    @Override
    public void tick() {
        if (this.isRemoved() || this.getWorld() == null) {
            return;
        }

        animationStartedThisTick = false;

        super.tick();
        animationTick++;
        stateTimer++;

        if (shootCooldown > 0) {
            shootCooldown--;
        }

        if (shootingDelay > 0) {
            shootingDelay--;
        }

        if (burrowCooldownTimer > 0) {
            burrowCooldownTimer--;
        }

        try {
            updateCombatState();
            updateMovementTracking();
            updateMovementSpeed();
            handleStateTransitions();
            updateAnimations();

            if (this.getWorld().isClient()) {
                switch (this.getBoreState()) {
                    case BURROWING -> {
                        if (this.stateTimer < 20) {
                            this.addBurrowParticles(this.burrowingAnimationState);
                        }
                    }
                    case UNBURROWING -> {
                        if (this.stateTimer < 10) {
                            this.addBurrowParticles(this.unburrowingAnimationState);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(DATA_ID_TYPE_VARIANT, 0);
        builder.add(DATA_ID_STATE, BoreState.IDLE.ordinal());
    }

    private void updateCombatState() {
        LivingEntity target = this.getTarget();
        boolean wasInCombat = isInCombat;

        if (target != null && target.isAlive() && !target.isRemoved() && target != lastTarget) {
            isInCombat = true;
            combatStartTime = this.age;
            lastTarget = target;
            circlingCenter = target.getPos();
            circlingAngle = this.random.nextDouble() * Math.PI * 2;
            circlingDirection = this.random.nextBoolean() ? 1 : -1;

        } else if (target == null || !target.isAlive() || target.isRemoved()) {
            isInCombat = false;
            lastTarget = null;
            circlingCenter = null;
            shootingDelay = 0;
            burrowCooldown = 0;
            nextBurrowTime = 0;
        }

        if (isInCombat && target != null && target.isAlive() && !target.isRemoved()) {
            circlingCenter = target.getPos();

            handleCombatBurrowing();
        }
    }

    private boolean isStuckInSmallArea() {
        if (boreState != BoreState.BURROWING || !isWalkingWhileBurrowed) {
            stuckCheckPosition = null;
            stuckTimer = 0;
            return false;
        }

        Vec3d currentPos = this.getPos();

        if (stuckCheckPosition == null) {
            stuckCheckPosition = currentPos;
            stuckTimer = 0;
            return false;
        }

        double distance = currentPos.distanceTo(stuckCheckPosition);

        if (distance <= STUCK_AREA_SIZE) {
            stuckTimer++;
            return stuckTimer >= STUCK_TIME_THRESHOLD;
        } else {
            stuckCheckPosition = currentPos;
            stuckTimer = 0;
            return false;
        }
    }

    private void handleCombatBurrowing() {
        if (burrowCooldown > 0) {
            burrowCooldown--;
        }

        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive() && !target.isRemoved()) {
            double distanceToTarget = this.distanceTo(target);

            List<BoreEntity> nearbyBurrowers = this.getWorld().getEntitiesByClass(
                    BoreEntity.class,
                    this.getBoundingBox().expand(16.0),
                    bore -> bore != this && bore.getBoreState() == BoreState.BURROWING
            );

            boolean withinLimit = nearbyBurrowers.size() < 4;

            if (distanceToTarget <= 4.0 &&
                    burrowCooldown <= 0 &&
                    burrowCooldownTimer <= 0 &&
                    boreState == BoreState.IDLE &&
                    !isNearbyProjectileDangerous() &&
                    withinLimit) {

                startBurrowing();
                burrowCooldown = 200 + this.random.nextInt(100);

                for (BoreEntity bore : nearbyBurrowers) {
                    Vec3d away = bore.getPos().subtract(target.getPos()).normalize();
                    Vec3d retreatPos = bore.getPos().add(away.multiply(6.0));
                    bore.getNavigation().startMovingTo(retreatPos.x, retreatPos.y, retreatPos.z, 1.3);
                }
            }
        }
    }

    private void updateAnimations() {
        if (this.getWorld().isClient()) {
            if (this.boreState == BoreState.IDLE) {
                if (!isIdleAnimationRunning) {
                    --this.idleAnimationTimeout;
                    if (this.idleAnimationTimeout <= 0) {
                        this.idleAnimationTimeout = this.random.nextInt(40) + 80;
                        this.idleAnimationState.start(this.age);
                        this.isIdleAnimationRunning = true;
                    }
                }
            } else {
                this.idleAnimationTimeout = 0;
                this.isIdleAnimationRunning = false;
                this.idleAnimationState.stop();
            }
        }
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

    private void updateMovementSpeed() {
        double baseSpeed = 0.23;

        if (isInCombat && this.getTarget() != null) {
            int timeSinceCombatStart = this.age - combatStartTime;
            double progressionFactor = Math.min(timeSinceCombatStart / 120.0, 1.0);

            progressionFactor = easeInOutQuad(progressionFactor);

            double targetSpeed = 0.35;
            baseSpeed = baseSpeed + (targetSpeed - baseSpeed) * progressionFactor;
        }

        switch (boreState) {
            case BURROWING:
                if (isWalkingWhileBurrowed) {
                    if (isInCombat && this.getTarget() != null) {
                        Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED)).setBaseValue(0.35);
                    } else {
                        Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED)).setBaseValue(0.6);
                    }
                } else {
                    Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED)).setBaseValue(baseSpeed * 0.1);
                }
                break;
            case UNBURROWING:
                Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED)).setBaseValue(baseSpeed * 0.1);
                break;
            default:
                Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED)).setBaseValue(baseSpeed);
                break;
        }
    }

    private double easeInOutQuad(double t) {
        return t < 0.5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2;
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource damageSource, float amount) {
        if (damageSource.isOf(DamageTypes.FALL) && boreState == BoreState.BURROWING) {
            return false;
        }

        if (this.getBoreState() == BoreState.BURROWING) {
            if (damageSource.isOf(DamageTypes.OUT_OF_WORLD) ||
                    damageSource.isOf(DamageTypes.GENERIC_KILL)) {
                return super.damage(world, damageSource, amount);
            }

            Entity attacker = damageSource.getAttacker();
            if (attacker instanceof LivingEntity living) {
                ItemStack weapon = living.getMainHandStack();
                if (weapon.getItem().toString().contains("pickaxe")) {
                    this.forceUnburrow();
                    this.burrowCooldownTimer = 80;
                    return super.damage(world, damageSource, amount);
                }
            }
            return false;
        }
        return super.damage(world, damageSource, amount);
    }


    private void handleStateTransitions() {
        switch (boreState) {
            case SHOOTING:
                if (stateTimer == 20 && !this.getWorld().isClient()) {
                    fireEarthCharge();
                }
                if (stateTimer >= 40) {
                    setBoreState(BoreState.IDLE);
                    setRelocationTarget();
                }
                break;
            case BURROWING:
                if (stateTimer == 20) {
                    if (!isWalkingWhileBurrowed) {
                        setBurrowDestination();
                        isWalkingWhileBurrowed = true;

                        if (this.getWorld().isClient() && burrowDestination != null && !whileburrowAnimPlayed) {
                            burrowingAnimationState.stop();
                            whileburrowingAnimationState.start(this.age);
                            whileburrowAnimPlayed = true;
                        } else if (this.getWorld().isClient() && burrowDestination == null) {
                            forceUnburrow();
                            return;
                        }
                    }
                }

                if (isWalkingWhileBurrowed && burrowDestination != null) {
                    if (this.getNavigation().isIdle()) {
                        this.getNavigation().startMovingTo(
                                burrowDestination.getX(),
                                this.getY(),
                                burrowDestination.getZ(),
                                isInCombat && this.getTarget() != null ? 1.2 : 1.4
                        );
                    }

                    double distanceToDestination = Math.sqrt(this.squaredDistanceTo(
                            burrowDestination.getX(), burrowDestination.getY(), burrowDestination.getZ()));

                    if (distanceToDestination < 1.5 || stateTimer >= 300) {
                        forceUnburrow();
                    }
                } else {
                    if (stateTimer >= 70) {
                        forceUnburrow();
                    }
                }
                break;
            case UNBURROWING:
                if (stateTimer >= 40) {
                    setBoreState(BoreState.IDLE);
                    burrowCooldownTimer = BURROW_COOLDOWN_TICKS;
                }
                break;
        }
    }

    private void forceUnburrow() {
        if (this.getWorld().isClient()) {
            whileburrowingAnimationState.stop();
            burrowingAnimationState.stop();
        }
        setBoreState(BoreState.UNBURROWING);
        burrowDestination = null;
        isWalkingWhileBurrowed = false;
        stuckCheckPosition = null;
        stuckTimer = 0;
    }

    private void setBurrowDestination() {
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive() || target.isRemoved()) {
            setBurrowDestinationRandom();
            return;
        }

        double currentY = this.getY();
        Vec3d targetPos = target.getPos();
        Vec3d currentPos = this.getPos();

        Vec3d awayDirection = currentPos.subtract(targetPos).normalize();

        BlockPos bestDestination = null;
        double bestScore = Double.MAX_VALUE;

        for (int attempts = 0; attempts < 24; attempts++) {
            double baseAngle = Math.atan2(awayDirection.z, awayDirection.x);
            double variance = (this.random.nextDouble() - 0.5) * Math.PI * 0.6;
            double angle = baseAngle + variance;

            double distance = 8 + this.random.nextDouble() * 6;

            double offsetX = Math.cos(angle) * distance;
            double offsetZ = Math.sin(angle) * distance;

            double targetX = this.getX() + offsetX;
            double targetZ = this.getZ() + offsetZ;

            double groundY = findGroundLevel(targetX, targetZ);

            if (groundY != -1) {
                if (groundY < currentY) {
                    continue;
                }

                BlockPos targetPosBlock = new BlockPos((int) targetX, (int) groundY, (int) targetZ);

                if (isPositionSafeForBurrowing(targetPosBlock)) {
                    double distanceFromTarget = Math.sqrt(Math.pow(targetX - targetPos.x, 2) + Math.pow(targetZ - targetPos.z, 2));
                    double score = Math.abs(groundY - currentY) + (distance * 0.1) - (distanceFromTarget * 0.2);

                    if (!isPathBlocked(new Vec3d(targetPosBlock.getX(), targetPosBlock.getY(), targetPosBlock.getZ()))) {
                        score -= 5.0;
                    }

                    if (score < bestScore) {
                        bestScore = score;
                        bestDestination = targetPosBlock;
                    }
                }
            }
        }

        if (bestDestination != null) {
            this.burrowDestination = bestDestination;
            return;
        }

        for (int attempts = 0; attempts < 16; attempts++) {
            double baseAngle = Math.atan2(awayDirection.z, awayDirection.x);
            double variance = (this.random.nextDouble() - 0.5) * Math.PI * 0.8;
            double angle = baseAngle + variance;
            double distance = 4 + this.random.nextDouble() * 4;

            double offsetX = Math.cos(angle) * distance;
            double offsetZ = Math.sin(angle) * distance;

            double targetX = this.getX() + offsetX;
            double targetZ = this.getZ() + offsetZ;

            BlockPos targetPosBlock = new BlockPos((int) targetX, (int) currentY, (int) targetZ);

            if (targetPosBlock.getY() < this.getBlockY()) {
                continue;
            }

            if (isPositionSafe(targetPosBlock)) {
                this.burrowDestination = targetPosBlock;
                return;
            }
        }

        this.burrowDestination = null;
    }

    private void setBurrowDestinationRandom() {
        double currentY = this.getY();
        BlockPos bestDestination = null;
        double bestScore = Double.MAX_VALUE;

        for (int attempts = 0; attempts < 24; attempts++) {
            double angle = this.random.nextDouble() * 2 * Math.PI;
            double distance = 8 + this.random.nextDouble() * 6;

            double offsetX = Math.cos(angle) * distance;
            double offsetZ = Math.sin(angle) * distance;

            double targetX = this.getX() + offsetX;
            double targetZ = this.getZ() + offsetZ;

            double groundY = findGroundLevel(targetX, targetZ);

            if (groundY != -1) {
                BlockPos targetPos = new BlockPos((int)targetX, (int)groundY, (int)targetZ);

                if (isPositionSafeForBurrowing(targetPos)) {
                    double score = Math.abs(groundY - currentY) + (distance * 0.1);

                    if (!isPathBlocked(new Vec3d(targetPos.getX(), targetPos.getY(), targetPos.getZ()))) {
                        score -= 5.0;
                    }

                    if (score < bestScore) {
                        bestScore = score;
                        bestDestination = targetPos;
                    }
                }
            }
        }

        if (bestDestination != null) {
            this.burrowDestination = bestDestination;
            return;
        }

        for (int attempts = 0; attempts < 16; attempts++) {
            double angle = this.random.nextDouble() * 2 * Math.PI;
            double distance = 4 + this.random.nextDouble() * 4;

            double offsetX = Math.cos(angle) * distance;
            double offsetZ = Math.sin(angle) * distance;

            double targetX = this.getX() + offsetX;
            double targetZ = this.getZ() + offsetZ;

            BlockPos targetPos = new BlockPos((int)targetX, (int)currentY, (int)targetZ);

            if (isPositionSafe(targetPos)) {
                this.burrowDestination = targetPos;
                return;
            }
        }

        this.burrowDestination = null;
    }

    private boolean isPositionSafeForBurrowing(BlockPos pos) {
        if (this.getWorld() == null || pos == null) return false;

        if (this.getWorld().getBlockState(pos).getBlock().toString().contains("lava") ||
                this.getWorld().getBlockState(pos).getBlock().toString().contains("water")) {
            return false;
        }

        int topY = this.getWorld().getBottomY() + this.getWorld().getHeight() - 1;
        if (pos.getY() <= this.getWorld().getBottomY() || pos.getY() >= topY - 2) {
            return false;
        }

        BlockPos belowPos = pos.down();
        if (!this.getWorld().getBlockState(belowPos).isSolidBlock(this.getWorld(), belowPos)) {
            return false;
        }

        if (this.getWorld().getBlockState(pos).isSolidBlock(this.getWorld(), pos) ||
                this.getWorld().getBlockState(pos.up()).isSolidBlock(this.getWorld(), pos.up())) {
            return false;
        }

        return true;
    }

    private static class BoreBurrowingMovementGoal extends Goal {
        private final BoreEntity bore;
        private int stuckCounter = 0;
        private Vec3d lastPosition = null;
        private int forceMovementTimer = 0;

        public BoreBurrowingMovementGoal(BoreEntity bore) {
            this.bore = bore;
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return bore.getBoreState() == BoreState.BURROWING &&
                    bore.isWalkingWhileBurrowed &&
                    bore.burrowDestination != null;
        }

        @Override
        public void start() {
            stuckCounter = 0;
            forceMovementTimer = 0;
            lastPosition = bore.getPos();
        }

        @Override
        public void tick() {
            if (bore.burrowDestination != null) {
                double currentY = bore.getY();
                double moveSpeed = (bore.isInCombat() && bore.getTarget() != null) ? 1.2 : 1.4;

                Vec3d currentPos = bore.getPos();

                if (lastPosition != null && currentPos.distanceTo(lastPosition) < 0.1) {
                    stuckCounter++;
                    forceMovementTimer++;

                    if (stuckCounter > 10) {
                        BlockPos alternative = bore.findAlternativeBurrowDestination();
                        if (alternative != null) {
                            if (alternative.getY() < bore.getBlockY()) {
                                bore.forceUnburrow();
                                return;
                            }

                            bore.burrowDestination = alternative;
                            bore.getNavigation().stop();
                            bore.getNavigation().startMovingTo(
                                    alternative.getX(), currentY, alternative.getZ(), moveSpeed
                            );
                            stuckCounter = 0;
                            forceMovementTimer = 0;
                        }
                    }

                    if (forceMovementTimer > 5 && bore.burrowDestination != null) {
                        Vec3d direction = new Vec3d(
                                bore.burrowDestination.getX(), currentY, bore.burrowDestination.getZ()
                        ).subtract(currentPos).normalize();

                        Vec3d forceMovement = direction.multiply(0.15);
                        bore.setVelocity(bore.getVelocity().add(forceMovement));
                        forceMovementTimer = 0;
                    }
                } else {
                    stuckCounter = 0;
                    forceMovementTimer = 0;
                }

                lastPosition = currentPos;

                if (bore.getNavigation().isIdle()) {
                    if (bore.burrowDestination.getY() < bore.getBlockY()) {
                        bore.forceUnburrow();
                        return;
                    }

                    bore.getNavigation().startMovingTo(
                            bore.burrowDestination.getX(),
                            currentY,
                            bore.burrowDestination.getZ(),
                            moveSpeed
                    );
                }
            }
        }

        @Override
        public boolean shouldContinue() {
            return bore.getBoreState() == BoreState.BURROWING &&
                    bore.isWalkingWhileBurrowed &&
                    bore.burrowDestination != null;
        }
    }

    private boolean isNavigationStuck() {
        return this.getNavigation().isIdle() &&
                boreState == BoreState.BURROWING &&
                isWalkingWhileBurrowed &&
                burrowDestination != null;
    }

    private boolean wouldFallOffEdge(Vec3d targetPos) {
        if (this.getWorld() == null || targetPos == null) return true;

        BlockPos blockPos = new BlockPos((int)targetPos.x, (int)targetPos.y, (int)targetPos.z);
        BlockPos belowPos = blockPos.down();

        for (int i = 1; i <= 3; i++) {
            BlockPos checkPos = belowPos.down(i);
            if (this.getWorld().getBlockState(checkPos).isSolidBlock(this.getWorld(), checkPos)) {
                return false;
            }
        }

        return true;
    }

    private void setRelocationTarget() {
        for (int attempts = 0; attempts < 10; attempts++) {
            double angle = this.random.nextDouble() * 2 * Math.PI;
            double distance = 10 + this.random.nextDouble() * 2;

            double offsetX = Math.cos(angle) * distance;
            double offsetZ = Math.sin(angle) * distance;

            BlockPos targetPos = new BlockPos((int)(this.getX() + offsetX), (int)this.getY(), (int)(this.getZ() + offsetZ));

            if (isPositionSafe(targetPos)) {
                this.relocateTarget = targetPos;
                break;
            }
        }
    }

    private boolean isPositionSafe(BlockPos pos) {
        if (this.getWorld() == null || pos == null) return false;
        return this.getWorld().isAir(pos) &&
                this.getWorld().isAir(pos.up()) &&
                this.getWorld().getBlockState(pos.down()).isSolidBlock(this.getWorld(), pos.down());
    }

    public Vec3d getCirclingPosition() {
        if (circlingCenter == null) return null;

        double radius = 8.0;
        double x = circlingCenter.x + Math.cos(circlingAngle) * radius;
        double z = circlingCenter.z + Math.sin(circlingAngle) * radius;

        double y = circlingCenter.y;
        BlockPos testPos = new BlockPos((int)x, (int)y, (int)z);

        if (!isPositionSafe(testPos)) {
            for (int yOffset = -2; yOffset <= 3; yOffset++) {
                BlockPos adjustedPos = testPos.add(0, yOffset, 0);
                if (isPositionSafe(adjustedPos)) {
                    y = adjustedPos.getY();
                    break;
                }
            }
        }

        return new Vec3d(x, y, z);
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (getBoreState() == BoreState.BURROWING && isWalkingWhileBurrowed) {
            Vec3d currentPos = this.getPos();
            Vec3d targetPos = currentPos.add(movementInput);

            BlockPos currentBlock = BlockPos.ofFloored(currentPos);
            BlockPos targetBlock = BlockPos.ofFloored(targetPos);

            if (targetBlock.getY() < currentBlock.getY()) {
                this.setVelocity(0, 0, 0);
                return;
            }

            Vec3d flatMovement = new Vec3d(movementInput.x, 0, movementInput.z);
            super.travel(flatMovement);
            return;
        }

        super.travel(movementInput);
    }

    private double findGroundLevel(double x, double z) {
        if (this.getWorld() == null) return -1;

        int blockX = (int) Math.floor(x);
        int blockZ = (int) Math.floor(z);
        int startY = (int) this.getY();
        int topY = this.getWorld().getBottomY() + this.getWorld().getHeight() - 1;

        for (int y = startY; y >= this.getWorld().getBottomY(); y--) {
            BlockPos checkPos = new BlockPos(blockX, y, blockZ);
            BlockPos abovePos = checkPos.up();

            if (this.getWorld().getBlockState(checkPos).isSolidBlock(this.getWorld(), checkPos) &&
                    (!this.getWorld().getBlockState(abovePos).isSolidBlock(this.getWorld(), abovePos) ||
                            this.getWorld().isAir(abovePos))) {

                BlockPos aboveAbove = abovePos.up();
                if (!this.getWorld().getBlockState(aboveAbove).isSolidBlock(this.getWorld(), aboveAbove) ||
                        this.getWorld().isAir(aboveAbove)) {
                    return y + 1.0;
                }
            }
        }

        for (int y = startY + 1; y <= topY - 2; y++) {
            BlockPos checkPos = new BlockPos(blockX, y, blockZ);
            BlockPos abovePos = checkPos.up();

            if (this.getWorld().getBlockState(checkPos).isSolidBlock(this.getWorld(), checkPos) &&
                    (!this.getWorld().getBlockState(abovePos).isSolidBlock(this.getWorld(), abovePos) ||
                            this.getWorld().isAir(abovePos))) {

                BlockPos aboveAbove = abovePos.up();
                if (!this.getWorld().getBlockState(aboveAbove).isSolidBlock(this.getWorld(), aboveAbove) ||
                        this.getWorld().isAir(aboveAbove)) {
                    return y + 1.0;
                }
            }
        }

        return -1;
    }

    @Override
    protected float getJumpVelocity() {
        if (boreState == BoreState.BURROWING) {
            return 0.0f;
        }
        return super.getJumpVelocity();
    }

    @Override
    public boolean isClimbing() {
        return super.isClimbing() && boreState != BoreState.BURROWING;
    }

    public void updateCirclingAngle() {
        double angularSpeed = 0.05;
        circlingAngle += circlingDirection * angularSpeed;

        while (circlingAngle > Math.PI * 2) circlingAngle -= Math.PI * 2;
        while (circlingAngle < 0) circlingAngle += Math.PI * 2;
    }

    public boolean isInCombat() {
        return isInCombat;
    }

    public int getCombatDuration() {
        return isInCombat ? this.age - combatStartTime : 0;
    }

    private boolean isNearbyProjectileDangerous() {
        if (this.getWorld() == null) return false;

        try {
            List<EarthChargeProjectileEntity> projectiles = this.getWorld().getEntitiesByClass(
                    EarthChargeProjectileEntity.class,
                    this.getBoundingBox().expand(PROJECTILE_DANGER_RADIUS),
                    projectile -> projectile != null && projectile.getOwner() != this
            );

            return !projectiles.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isNearFriendlyProjectile() {
        if (this.getWorld() == null) return false;

        try {
            List<EarthChargeProjectileEntity> friendlyProjectiles = this.getWorld().getEntitiesByClass(
                    EarthChargeProjectileEntity.class,
                    this.getBoundingBox().expand(FRIENDLY_PROJECTILE_AVOIDANCE_RADIUS),
                    projectile -> projectile != null && projectile.getOwner() instanceof BoreEntity && projectile.getOwner() != this
            );

            return !friendlyProjectiles.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private Vec3d getProjectileAvoidanceDirection() {
        if (this.getWorld() == null) return null;

        try {
            List<EarthChargeProjectileEntity> projectiles = this.getWorld().getEntitiesByClass(
                    EarthChargeProjectileEntity.class,
                    this.getBoundingBox().expand(Math.max(PROJECTILE_DANGER_RADIUS, FRIENDLY_PROJECTILE_AVOIDANCE_RADIUS)),
                    projectile -> projectile != null && projectile.getOwner() != this
            );

            if (projectiles.isEmpty()) return null;

            Vec3d avoidanceDirection = Vec3d.ZERO;
            for (EarthChargeProjectileEntity projectile : projectiles) {
                if (projectile != null && projectile.getPos() != null) {
                    Vec3d directionAway = this.getPos().subtract(projectile.getPos()).normalize();
                    double weight = (projectile.getOwner() instanceof BoreEntity) ? 1.5 : 1.0;
                    avoidanceDirection = avoidanceDirection.add(directionAway.multiply(weight));
                }
            }

            return avoidanceDirection.normalize();
        } catch (Exception e) {
            return null;
        }
    }

    private int countNearbyShootingAllies() {
        if (this.getWorld() == null) return 0;

        try {
            List<BoreEntity> nearbyAllies = this.getWorld().getEntitiesByClass(
                    BoreEntity.class,
                    this.getBoundingBox().expand(16.0),
                    bore -> bore != null && bore != this && bore.isAlive() && !bore.isRemoved()
            );

            int shootingCount = 0;
            for (BoreEntity ally : nearbyAllies) {
                if (this.distanceTo(ally) <= 16.0 &&
                        (ally.getBoreState() == BoreState.SHOOTING || ally.shootCooldown > 45)) {
                    shootingCount++;
                }
            }

            return shootingCount;
        } catch (Exception e) {
            return 0;
        }
    }

    public BlockPos getRelocationTarget() {
        return relocateTarget;
    }

    public void clearRelocationTarget() {
        this.relocateTarget = null;
    }

    public int getAnimationTick() {
        return animationTick;
    }

    public int getStateTimer() {
        return stateTimer;
    }

    public BoreState getBoreState() {
        return boreState;
    }

    public BoreState getPreviousState() {
        return previousState;
    }

    private boolean isChangingState = false;

    public void setBoreState(BoreState newState) {
        if (this.boreState != newState && !isChangingState) {
            if (!isValidStateTransition(this.boreState, newState)) {
                return;
            }

            isChangingState = true;

            this.previousState = this.boreState;
            this.boreState = newState;
            this.animationTick = 0;
            this.stateTimer = 0;

            if (!this.getWorld().isClient()) {
                this.dataTracker.set(DATA_ID_STATE, newState.ordinal());
            } else {
                startStateAnimation(newState);
            }

            if (newState == BoreState.IDLE && previousState == BoreState.UNBURROWING) {
                burrowAnimPlayed = false;
                whileburrowAnimPlayed = false;
            }

            isChangingState = false;
        }
    }

    private void startStateAnimation(BoreState state) {
        if (!this.getWorld().isClient() || animationStartedThisTick) return;

        animationStartedThisTick = true;

        switch (state) {
            case IDLE -> {
                if (previousState == BoreState.UNBURROWING) {
                    burrowingAnimationState.stop();
                    whileburrowingAnimationState.stop();
                    unburrowingAnimationState.stop();

                    burrowAnimPlayed = false;
                    whileburrowAnimPlayed = false;
                }

                if (previousState == BoreState.SHOOTING) {
                    shootingAnimationState.stop();
                }

                this.idleAnimationTimeout = this.random.nextInt(40) + 80;
                this.idleAnimationState.start(this.age);
                this.isIdleAnimationRunning = true;
            }
            case SHOOTING -> {
                stopAllAnimations();
                this.shootingAnimationState.start(this.age);
                this.isIdleAnimationRunning = false;
            }
            case BURROWING -> {
                idleAnimationState.stop();
                shootingAnimationState.stop();
                unburrowingAnimationState.stop();

                if (!burrowAnimPlayed) {
                    this.burrowingAnimationState.start(this.age);
                    burrowAnimPlayed = true;
                }
                this.isIdleAnimationRunning = false;
            }
            case UNBURROWING -> {
                whileburrowingAnimationState.stop();
                burrowingAnimationState.stop();
                idleAnimationState.stop();
                shootingAnimationState.stop();

                this.unburrowingAnimationState.start(this.age);
                this.isIdleAnimationRunning = false;
            }
        }
    }

    private boolean isValidStateTransition(BoreState from, BoreState to) {
        switch (from) {
            case IDLE:
                return to == BoreState.SHOOTING || to == BoreState.BURROWING;
            case SHOOTING:
                return to == BoreState.IDLE;
            case BURROWING:
                return to == BoreState.UNBURROWING;
            case UNBURROWING:
                return to == BoreState.IDLE;
            default:
                return false;
        }
    }

    private boolean isPathBlocked(Vec3d targetPos) {
        if (this.getWorld() == null || targetPos == null) return true;

        Vec3d currentPos = this.getPos();
        Vec3d direction = targetPos.subtract(currentPos).normalize();

        for (double step = 1.0; step <= 3.0; step += 0.5) {
            Vec3d checkPos = currentPos.add(direction.multiply(step));
            BlockPos blockPos = new BlockPos((int)checkPos.x, (int)checkPos.y, (int)checkPos.z);

            if (this.getWorld().getBlockState(blockPos).isSolidBlock(this.getWorld(), blockPos) ||
                    this.getWorld().getBlockState(blockPos.up()).isSolidBlock(this.getWorld(), blockPos.up())) {
                return true;
            }
        }

        return false;
    }

    private BlockPos findAlternativeBurrowDestination() {
        double currentY = this.getY();

        for (int attempts = 0; attempts < 24; attempts++) {
            double angle = (Math.PI * 2 * attempts) / 24.0;
            double distance = 8 + this.random.nextDouble() * 4;

            double offsetX = Math.cos(angle) * distance;
            double offsetZ = Math.sin(angle) * distance;

            BlockPos targetPos = new BlockPos(
                    (int)(this.getX() + offsetX),
                    (int)currentY,
                    (int)(this.getZ() + offsetZ)
            );

            if (isPositionSafeForBurrowing(targetPos) && !isPathBlocked(new Vec3d(targetPos.getX(), targetPos.getY(), targetPos.getZ()))) {
                return targetPos;
            }
        }

        return null;
    }

    private void stopAllAnimations() {
        if (this.getWorld().isClient()) {
            idleAnimationState.stop();
            shootingAnimationState.stop();
            burrowingAnimationState.stop();
            whileburrowingAnimationState.stop();
            unburrowingAnimationState.stop();
        }
    }

    public boolean canShoot() {
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive() || target.isRemoved()) return false;
        if (boreState == BoreState.BURROWING || boreState == BoreState.UNBURROWING) return false;

        try {
            double distance = this.distanceTo(target);
            if (distance < 4.0) return false;
            if (!hasMovedEnoughToShoot || isNearbyProjectileDangerous()) return false;
            if (isNearFriendlyProjectile()) return false;

            if (isStuckInSmallArea() && distance < 6.0) return false;

            return shootingDelay <= 0;
        } catch (Exception e) {
            return false;
        }
    }

    public void tryShootAtPlayer() {
        if (shootCooldown > 0 || !canShoot()) return;

        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive() || target.isRemoved()) return;

        int nearbyShooters = (int) this.getWorld().getEntitiesByClass(
                BoreEntity.class,
                this.getBoundingBox().expand(16.0),
                bore -> bore != this && bore.getBoreState() == BoreState.SHOOTING
        ).size();

        if (nearbyShooters >= 2) return;

        this.getWorld().getEntitiesByClass(
                BoreEntity.class,
                this.getBoundingBox().expand(8.0),
                bore -> bore != this && bore.isInCombat()
        ).forEach(ally -> {
            Vec3d away = ally.getPos().subtract(this.getPos()).normalize();
            ally.getNavigation().startMovingTo(
                    ally.getX() + away.x * 6,
                    ally.getY(),
                    ally.getZ() + away.z * 6,
                    1.4
            );
        });

        shootCooldown = 40 + this.random.nextInt(20);
        this.setBoreState(BoreState.SHOOTING);
        lastShootPosition = this.getPos();
        hasMovedEnoughToShoot = false;
        shootingDelay = 5 + this.random.nextInt(10);
    }



    public void startBurrowing() {
        if (boreState == BoreState.IDLE && !isWalkingWhileBurrowed) {
            boolean canBurrow = false;

            for (int quickCheck = 0; quickCheck < 8; quickCheck++) {
                double angle = (Math.PI * 2 * quickCheck) / 8.0;
                double distance = 6 + this.random.nextDouble() * 4;

                double offsetX = Math.cos(angle) * distance;
                double offsetZ = Math.sin(angle) * distance;

                BlockPos testPos = new BlockPos(
                        (int)(this.getX() + offsetX),
                        (int)this.getY(),
                        (int)(this.getZ() + offsetZ)
                );

                if (isPositionSafeForBurrowing(testPos)) {
                    canBurrow = true;
                    break;
                }
            }

            if (canBurrow) {
                setBoreState(BoreState.BURROWING);
            }
        }
    }

    private void fireEarthCharge() {
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive() || target.isRemoved()) return;

        Vec3d targetPos = predictTargetPosition(target);
        if (targetPos == null) return;

        Vec3d direction = targetPos.subtract(this.getPos()).normalize();

        try {
            EarthChargeProjectileEntity charge = new EarthChargeProjectileEntity(this.getWorld(), this);
            charge.setPosition(this.getX(), this.getEyeY(), this.getZ());
            charge.setVelocity(direction.x, direction.y, direction.z, 1.2f, 0.05f);
            this.getWorld().spawnEntity(charge);
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

    public String getCurrentAnimation() {
        switch (boreState) {
            case IDLE -> {
                return "BORE_IDLE";
            }
            case SHOOTING -> {
                return "BORE_SHOOTING";
            }
            case BURROWING -> {
                return "BORE_BURROWING";
            }
            case UNBURROWING -> {
                return "BORE_UNBURROWING";
            }
            default -> {
                return "BORE_IDLE";
            }
        }
    }

    private int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    public BoreVariant getVariant() {
        return BoreVariant.byId(this.getTypeVariant() & 255);
    }

    public void setVariant(BoreVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    public boolean isDarkVariant() {
        return getVariant() == BoreVariant.DARK;
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        BoreVariant variant = world.getRandom().nextFloat() < 0.005f ? BoreVariant.DARK : BoreVariant.NORMAL;
        setVariant(variant);
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    @Override
    public void writeCustomData(WriteView nbt) {
        super.writeCustomData(nbt);
        nbt.putString("BoreState", boreState.name());
        nbt.putInt("StateTimer", stateTimer);
        nbt.putInt("Variant", this.getTypeVariant());
        nbt.putBoolean("HasMovedEnoughToShoot", hasMovedEnoughToShoot);
        nbt.putBoolean("IsInCombat", isInCombat);
        nbt.putInt("CombatStartTime", combatStartTime);
        nbt.putDouble("CirclingAngle", circlingAngle);
        nbt.putInt("CirclingDirection", circlingDirection);
        nbt.putInt("ShootingDelay", shootingDelay);
        nbt.putInt("StuckTimer", stuckTimer);
        nbt.putInt("BurrowCooldownTimer", burrowCooldownTimer);

        if (stuckCheckPosition != null) {
            nbt.putDouble("StuckCheckX", stuckCheckPosition.x);
            nbt.putDouble("StuckCheckY", stuckCheckPosition.y);
            nbt.putDouble("StuckCheckZ", stuckCheckPosition.z);
        }

        if (relocateTarget != null) {
            nbt.putLong("RelocateTarget", relocateTarget.asLong());
        }
        if (lastShootPosition != null) {
            nbt.putDouble("LastShootX", lastShootPosition.x);
            nbt.putDouble("LastShootY", lastShootPosition.y);
            nbt.putDouble("LastShootZ", lastShootPosition.z);
        }
        if (circlingCenter != null) {
            nbt.putDouble("CirclingCenterX", circlingCenter.x);
            nbt.putDouble("CirclingCenterY", circlingCenter.y);
            nbt.putDouble("CirclingCenterZ", circlingCenter.z);
        }
    }

    public void readCustomData(ReadView nbt) {
        super.readCustomData(nbt);
        String stateString = nbt.getString("BoreState", "IDLE");
        if (!stateString.equals("IDLE")) {
            try {
                BoreState loadedState = BoreState.valueOf(stateString);
                this.boreState = loadedState;
                if (!this.getWorld().isClient()) {
                    this.dataTracker.set(DATA_ID_STATE, loadedState.ordinal());
                }
            } catch (IllegalArgumentException e) {
                this.boreState = BoreState.IDLE; // or whatever your default state is
            }
        }

        this.stateTimer = nbt.getInt("StateTimer", 0);
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, nbt.getInt("Variant", 0));
        this.hasMovedEnoughToShoot = nbt.getBoolean("HasMovedEnoughToShoot", false);
        this.isInCombat = nbt.getBoolean("IsInCombat", false);
        this.combatStartTime = nbt.getInt("CombatStartTime", 0);
        this.circlingAngle = nbt.getDouble("CirclingAngle", 0.0);
        this.circlingDirection = nbt.getInt("CirclingDirection", 1);
        this.shootingDelay = nbt.getInt("ShootingDelay", 0);
        this.stuckTimer = nbt.getInt("StuckTimer", 0);
        this.burrowCooldownTimer = nbt.getInt("BurrowCooldownTimer", 0);

        double stuckCheckX = nbt.getDouble("StuckCheckX", Double.NaN);
        if (!Double.isNaN(stuckCheckX)) {
            this.stuckCheckPosition = new Vec3d(
                    stuckCheckX,
                    nbt.getDouble("StuckCheckY", 0.0),
                    nbt.getDouble("StuckCheckZ", 0.0)
            );
        }

        long relocateTargetLong = nbt.getLong("RelocateTarget", Long.MIN_VALUE);
        if (relocateTargetLong != Long.MIN_VALUE) {
            this.relocateTarget = BlockPos.fromLong(relocateTargetLong);
        }

        double lastShootX = nbt.getDouble("LastShootX", Double.NaN);
        if (!Double.isNaN(lastShootX)) {
            this.lastShootPosition = new Vec3d(
                    lastShootX,
                    nbt.getDouble("LastShootY", 0.0),
                    nbt.getDouble("LastShootZ", 0.0)
            );
        }

        double circlingCenterX = nbt.getDouble("CirclingCenterX", Double.NaN);
        if (!Double.isNaN(circlingCenterX)) {
            this.circlingCenter = new Vec3d(
                    circlingCenterX,
                    nbt.getDouble("CirclingCenterY", 0.0),
                    nbt.getDouble("CirclingCenterZ", 0.0)
            );
        }
    }

    private static class BoreCircleGoal extends Goal {
        private final BoreEntity bore;
        private Vec3d targetPosition;
        private int repositionTimer = 0;

        public BoreCircleGoal(BoreEntity bore) {
            this.bore = bore;
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            LivingEntity target = bore.getTarget();
            return bore.isInCombat() &&
                    target != null &&
                    target.isAlive() &&
                    !target.isRemoved() &&
                    bore.getBoreState() == BoreState.IDLE &&
                    bore.getCombatDuration() < 120;
        }

        @Override
        public void start() {
            targetPosition = bore.getCirclingPosition();
            repositionTimer = 0;
        }

        @Override
        public void tick() {
            repositionTimer++;

            bore.updateCirclingAngle();

            if (repositionTimer >= 10 ||
                    (targetPosition != null && bore.squaredDistanceTo(targetPosition) < 2.0)) {
                targetPosition = bore.getCirclingPosition();
                repositionTimer = 0;
            }

            if (targetPosition != null) {
                bore.getNavigation().startMovingTo(targetPosition.x, targetPosition.y, targetPosition.z, 1.2);
            }

            LivingEntity target = bore.getTarget();
            if (target != null && target.isAlive() && !target.isRemoved()) {
                try {
                    bore.getLookControl().lookAt(target.getX(), target.getEyeY(), target.getZ());
                } catch (Exception e) {
                }
            }
        }

        @Override
        public boolean shouldContinue() {
            LivingEntity target = bore.getTarget();
            return bore.isInCombat() &&
                    target != null &&
                    target.isAlive() &&
                    !target.isRemoved() &&
                    bore.getBoreState() == BoreState.IDLE &&
                    bore.getCombatDuration() < 120;
        }
    }

    private static class BoreAvoidProjectileGoal extends Goal {
        private final BoreEntity bore;
        private Vec3d avoidanceDirection;

        public BoreAvoidProjectileGoal(BoreEntity bore) {
            this.bore = bore;
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (bore.getBoreState() != BoreState.IDLE) return false;

            avoidanceDirection = bore.getProjectileAvoidanceDirection();
            return avoidanceDirection != null;
        }

        @Override
        public void tick() {
            if (avoidanceDirection != null) {
                Vec3d targetPos = bore.getPos().add(avoidanceDirection.multiply(6.0));
                bore.getNavigation().startMovingTo(targetPos.x, targetPos.y, targetPos.z, 1.5);
            }
        }

        @Override
        public boolean shouldContinue() {
            return (bore.isNearbyProjectileDangerous() || bore.isNearFriendlyProjectile()) &&
                    bore.getBoreState() == BoreState.IDLE;
        }
    }

    private static class BoreSmartPositioningGoal extends Goal {
        private final BoreEntity bore;
        private Vec3d optimalPosition;

        public BoreSmartPositioningGoal(BoreEntity bore) {
            this.bore = bore;
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            LivingEntity target = bore.getTarget();
            if (target == null || target.isRemoved() || !target.isAlive() || bore.getBoreState() != BoreState.IDLE) return false;

            try {
                double distance = bore.distanceTo(target);

                if (distance < 6.0 || distance > 12.0) {
                    optimalPosition = findOptimalPosition(target);
                    return optimalPosition != null;
                }
            } catch (Exception e) {
                return false;
            }

            return false;
        }

        private Vec3d findOptimalPosition(LivingEntity target) {
            if (target == null || target.isRemoved() || !target.isAlive()) return null;

            try {
                Vec3d targetPos = target.getPos();
                if (targetPos == null) return null;

                double optimalDistance = 8.0;

                for (int attempts = 0; attempts < 8; attempts++) {
                    double angle = (Math.PI * 2 * attempts) / 8.0;
                    double x = targetPos.x + Math.cos(angle) * optimalDistance;
                    double z = targetPos.z + Math.sin(angle) * optimalDistance;

                    BlockPos testPos = new BlockPos((int)x, (int)targetPos.y, (int)z);

                    if (bore.isPositionSafe(testPos)) {
                        return new Vec3d(x, targetPos.y, z);
                    }
                }
            } catch (Exception e) {
            }

            return null;
        }

        @Override
        public void tick() {
            if (optimalPosition != null) {
                bore.getNavigation().startMovingTo(optimalPosition.x, optimalPosition.y, optimalPosition.z, 1.0);
            }
        }

        @Override
        public boolean shouldContinue() {
            return optimalPosition != null &&
                    bore.squaredDistanceTo(optimalPosition) > 4.0 &&
                    bore.getBoreState() == BoreState.IDLE;
        }
    }

    private static class BoreRelocateGoal extends Goal {
        private final BoreEntity bore;

        public BoreRelocateGoal(BoreEntity bore) {
            this.bore = bore;
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return bore.getRelocationTarget() != null &&
                    bore.getBoreState() == BoreState.IDLE;
        }

        @Override
        public boolean shouldContinue() {
            BlockPos target = bore.getRelocationTarget();
            return target != null && bore.squaredDistanceTo(target.getX(), target.getY(), target.getZ()) > 4.0;
        }

        @Override
        public void tick() {
            BlockPos target = bore.getRelocationTarget();
            if (target != null) {
                bore.getNavigation().startMovingTo(target.getX(), target.getY(), target.getZ(), 1.0);
            }
        }

        @Override
        public void stop() {
            bore.clearRelocationTarget();
        }
    }

    private static class BoreFleeGoal extends FleeEntityGoal<PlayerEntity> {
        private final BoreEntity bore;

        public BoreFleeGoal(BoreEntity bore) {
            super(bore, PlayerEntity.class, 6.0F, 1.2, 1.5);
            this.bore = bore;
        }

        @Override
        public boolean canStart() {
            LivingEntity target = bore.getTarget();
            return super.canStart() &&
                    bore.getBoreState() != BoreState.SHOOTING &&
                    bore.getBoreState() != BoreState.BURROWING &&
                    bore.getBoreState() != BoreState.UNBURROWING &&
                    target != null &&
                    target.isAlive() &&
                    !target.isRemoved() &&
                    bore.distanceTo(target) < 4.0 &&
                    (bore.burrowCooldown > 0 || bore.burrowCooldownTimer > 0);
        }
    }

    private static class BoreShootGoal extends Goal {
        private final BoreEntity bore;
        private int aimTimer = 0;

        public BoreShootGoal(BoreEntity bore) {
            this.bore = bore;
            this.setControls(EnumSet.of(Control.LOOK));
        }

        @Override
        public boolean canStart() {
            LivingEntity target = bore.getTarget();
            return target != null &&
                    bore.shootCooldown <= 0 &&
                    bore.canShoot() &&
                    bore.distanceTo(target) >= 4.0 &&
                    bore.distanceTo(target) <= 16.0f &&
                    bore.getBoreState() == BoreState.IDLE &&
                    bore.getCombatDuration() >= 40;
        }

        @Override
        public void start() {
            aimTimer = 15;
        }

        @Override
        public void tick() {
            LivingEntity target = bore.getTarget();
            if (target != null && target.isAlive() && !target.isRemoved()) {
                try {
                    bore.getLookControl().lookAt(target.getX(), target.getEyeY(), target.getZ());

                    if (--aimTimer <= 0) {
                        bore.tryShootAtPlayer();
                        aimTimer = 60;
                    }
                } catch (Exception e) {
                    aimTimer = 60;
                }
            }
        }

        @Override
        public boolean shouldContinue() {
            LivingEntity target = bore.getTarget();
            return target != null &&
                    bore.getBoreState() != BoreState.SHOOTING &&
                    bore.distanceTo(target) >= 4.0 &&
                    bore.distanceTo(target) <= 16.0f;
        }

        @Override
        public void stop() {
            aimTimer = 0;
        }
    }

    private static final TrackedData<Integer> DATA_ID_STATE =
            DataTracker.registerData(BoreEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private void syncStateToClients() {
        if (!this.getWorld().isClient()) {
            this.dataTracker.set(DATA_ID_STATE, this.boreState.ordinal());
        }
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (DATA_ID_STATE.equals(data) && this.getWorld().isClient()) {
            BoreState newState = BoreState.values()[this.dataTracker.get(DATA_ID_STATE)];
            if (this.boreState != newState && !isChangingState) {
                isChangingState = true;

                this.previousState = this.boreState;
                this.boreState = newState;
                this.animationTick = 0;
                this.stateTimer = 0;

                startStateAnimation(newState);

                isChangingState = false;
            }
        }
        super.onTrackedDataSet(data);
    }

    private void addBurrowParticles(AnimationState animationState) {
        if (this.getWorld().isClient() && animationState.isRunning()) {
            BlockState blockState = this.getSteppingBlockState();
            if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
                Random random = this.getRandom();
                for (int i = 0; i < 7; ++i) {
                    double d = this.getX() + (double)MathHelper.nextBetween(random, -0.3F, 0.3F);
                    double e = this.getY();
                    double f = this.getZ() + (double)MathHelper.nextBetween(random, -0.3F, 0.3F);
                    this.getWorld().addParticleClient(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), d, e, f, 0.0, 0.0, 0.0);
                }
            }
        }
    }
}