package potatowolfie.earth_and_water.entity.custom;

import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.registry.tag.FluidTags;

import java.util.EnumSet;

public class BrineEntity extends HostileWaterCreatureEntity {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState underwaterAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();

    private int idleAnimationTimeout = 0;
    private boolean isIdleAnimationRunning = false;
    private boolean isUnderwaterAnimationRunning = false;
    private boolean isAttackAnimationRunning = false;
    private boolean animationStartedThisTick = false;

    public enum BrineState {
        IDLE,
        UNDERWATER_IDLE,
        SHOOTING
    }

    private static final TrackedData<Integer> DATA_ID_STATE =
            DataTracker.registerData(BrineEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private BrineState brineState = BrineState.UNDERWATER_IDLE;
    private BrineState previousState = BrineState.UNDERWATER_IDLE;
    private boolean isChangingState = false;

    public BrineEntity(EntityType<? extends BrineEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createBrineAttributes() {
        return WaterCreatureEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 30.0D)
                .add(EntityAttributes.ATTACK_DAMAGE, 6.0D)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.4D);
    }

    @Override
    protected void initGoals() {
        // Hostile goals
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0D, true));

        // Swimming movement goals
        this.goalSelector.add(2, new BrineRandomSwimGoal(this, 1.0D, 40));
        this.goalSelector.add(3, new BrineLookAroundGoal(this));
        this.goalSelector.add(4, new SwimAroundGoal(this, 1.0D, 10));

        // Target goals
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    // Custom swimming goal for random movement
    public static class BrineRandomSwimGoal extends Goal {
        private final BrineEntity brine;
        private final double speed;
        private final int chance;
        private double targetX;
        private double targetY;
        private double targetZ;

        public BrineRandomSwimGoal(BrineEntity brine, double speed, int chance) {
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

            // Only move when in water
            return this.brine.isTouchingWater();
        }

        @Override
        public void start() {
            this.chooseRandomTarget();
        }

        @Override
        public boolean shouldContinue() {
            return !this.brine.getNavigation().isIdle() && this.brine.isTouchingWater();
        }

        @Override
        public void stop() {
            this.brine.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (this.brine.getRandom().nextInt(50) == 0 || this.brine.squaredDistanceTo(this.targetX, this.targetY, this.targetZ) < 4.0D) {
                this.chooseRandomTarget();
            }
        }

        private void chooseRandomTarget() {
            Vec3d currentPos = this.brine.getPos();

            // Generate random target within swimming range - less restrictive
            double range = 20.0D;
            double offsetX = (this.brine.getRandom().nextDouble() - 0.5D) * range;
            double offsetY = (this.brine.getRandom().nextDouble() - 0.5D) * 12.0D; // Increased Y range
            double offsetZ = (this.brine.getRandom().nextDouble() - 0.5D) * range;

            this.targetX = currentPos.x + offsetX;
            // Fixed: Use getSeaLevel() instead of getTopY()
            this.targetY = MathHelper.clamp(currentPos.y + offsetY, this.brine.getWorld().getBottomY() + 5, this.brine.getWorld().getSeaLevel() + 50);
            this.targetZ = currentPos.z + offsetZ;

            // Just go to the target - no water restrictions
            this.brine.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
        }
    }

    // Custom look around goal for water creatures
    public static class BrineLookAroundGoal extends Goal {
        private final BrineEntity brine;
        private double lookX;
        private double lookZ;
        private int lookTime;

        public BrineLookAroundGoal(BrineEntity brine) {
            this.brine = brine;
            this.setControls(EnumSet.of(Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            return this.brine.getRandom().nextFloat() < 0.02F;
        }

        @Override
        public void start() {
            double d = (Math.PI * 2) * this.brine.getRandom().nextDouble();
            this.lookX = Math.cos(d);
            this.lookZ = Math.sin(d);
            this.lookTime = 20 + this.brine.getRandom().nextInt(20);
        }

        @Override
        public boolean shouldContinue() {
            return this.lookTime > 0;
        }

        @Override
        public void tick() {
            --this.lookTime;
            this.brine.getLookControl().lookAt(this.brine.getX() + this.lookX, this.brine.getEyeY(), this.brine.getZ() + this.lookZ);
        }
    }

    private void updateAnimations() {
        if (this.getWorld().isClient()) {
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

            if (!this.getWorld().isClient()) {
                this.dataTracker.set(DATA_ID_STATE, newState.ordinal());
            } else {
                startStateAnimation(newState);
            }

            isChangingState = false;
        }
    }

    private void startStateAnimation(BrineState state) {
        if (!this.getWorld().isClient() || animationStartedThisTick) return;

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
        if (this.getWorld().isClient()) {
            idleAnimationState.stop();
            underwaterAnimationState.stop();
            attackAnimationState.stop();
        }
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (DATA_ID_STATE.equals(data) && this.getWorld().isClient()) {
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

    private boolean isFullySubmerged() {
        double entityHeight = this.getHeight();
        Vec3d pos = this.getPos();

        boolean bottomSubmerged = this.getWorld().getFluidState(new BlockPos((int)pos.x, (int)pos.y, (int)pos.z)).isIn(FluidTags.WATER);
        boolean middleSubmerged = this.getWorld().getFluidState(new BlockPos((int)pos.x, (int)(pos.y + entityHeight * 0.5), (int)pos.z)).isIn(FluidTags.WATER);
        boolean topSubmerged = this.getWorld().getFluidState(new BlockPos((int)pos.x, (int)(pos.y + entityHeight), (int)pos.z)).isIn(FluidTags.WATER);

        return bottomSubmerged && middleSubmerged && topSubmerged;
    }

    @Override
    public void tick() {
        if (this.isRemoved() || this.getWorld() == null) {
            return;
        }

        animationStartedThisTick = false;
        super.tick();

        try {
            updateAnimations();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void tickMovement() {
        super.tickMovement();

        // Fixed: Use isSubmergedIn instead of isInsideWaterOrBubbleColumn
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

    public boolean isKelpCovered() {
        return true;
    }

    public float getLeftVisionAngle() {
        return this.getYaw() - 90.0F;
    }


    @Override
    public void writeCustomData(WriteView nbt) {
        super.writeCustomData(nbt);
        nbt.putString("BrineState", brineState.name());
    }

    @Override
    public void readCustomData(ReadView nbt) {
        super.readCustomData(nbt);
        String stateString = nbt.getString("BrineState", "UNDERWATER_IDLE");
        if (!stateString.equals("UNDERWATER_IDLE")) {
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
    }
}