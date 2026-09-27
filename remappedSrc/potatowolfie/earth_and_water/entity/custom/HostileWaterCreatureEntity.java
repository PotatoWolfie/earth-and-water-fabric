package potatowolfie.earth_and_water.entity.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import java.util.EnumSet;

public abstract class HostileWaterCreatureEntity extends Monster {

    protected HostileWaterCreatureEntity(EntityType<? extends HostileWaterCreatureEntity> entityType, Level world) {
        super(entityType, world);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.moveControl = new MoveControl(this);
    }

    public boolean checkSpawnObstruction(LevelReader world) {
        return world.isUnobstructed(this);
    }

    public int getAmbientSoundInterval() {
        return 120;
    }

    protected int getXpToDrop() {
        return 1 + this.level().random.nextInt(3);
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

    protected void tickWaterBreathingAir(int air) {
        this.setAirSupply(this.getMaxAirSupply());
    }

    @Override
    public void baseTick() {
        int i = this.getAirSupply();
        super.baseTick();
        this.tickWaterBreathingAir(i);
    }

    public static class SeekWaterGoal extends Goal {
        private final HostileWaterCreatureEntity entity;
        private final double speed;
        private BlockPos targetWaterPos;

        public SeekWaterGoal(HostileWaterCreatureEntity entity, double speed) {
            this.entity = entity;
            this.speed = speed;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.entity.isInWater()) {
                return false;
            }

            this.targetWaterPos = findNearbyWater();
            return this.targetWaterPos != null;
        }

        @Override
        public boolean canContinueToUse() {
            return !this.entity.isInWater() &&
                    this.targetWaterPos != null &&
                    !this.entity.getNavigation().isDone();
        }

        @Override
        public void start() {
            if (this.targetWaterPos != null) {
                this.entity.getNavigation().moveTo(
                        this.targetWaterPos.getX(),
                        this.targetWaterPos.getY(),
                        this.targetWaterPos.getZ(),
                        this.speed
                );
            }
        }

        @Override
        public void stop() {
            this.targetWaterPos = null;
        }

        private BlockPos findNearbyWater() {
            BlockPos entityPos = this.entity.blockPosition();
            int searchRange = 16;

            for (int range = 4; range <= searchRange; range += 4) {
                for (int x = -range; x <= range; x += 2) {
                    for (int y = -8; y <= 8; y += 2) {
                        for (int z = -range; z <= range; z += 2) {
                            BlockPos checkPos = entityPos.offset(x, y, z);
                            if (this.entity.level().getFluidState(checkPos).is(FluidTags.WATER)) {
                                return checkPos;
                            }
                        }
                    }
                }
            }
            return null;
        }
    }

    public static class SwimInWaterGoal extends Goal {
        private final HostileWaterCreatureEntity entity;
        private final double speed;
        private final int chance;
        private double targetX;
        private double targetY;
        private double targetZ;

        public SwimInWaterGoal(HostileWaterCreatureEntity entity, double speed, int chance) {
            this.entity = entity;
            this.speed = speed;
            this.chance = chance;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.entity.isVehicle() || this.entity.getTarget() != null) {
                return false;
            }

            if (this.entity.getRandom().nextInt(this.chance) != 0) {
                return false;
            }

            return this.entity.isInWater();
        }

        @Override
        public void start() {
            this.chooseWaterTarget();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.entity.getNavigation().isDone() && this.entity.isInWater();
        }

        @Override
        public void stop() {
            this.entity.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (this.entity.getRandom().nextInt(150) == 0 ||
                    this.entity.distanceToSqr(this.targetX, this.targetY, this.targetZ) < 1.0D) {
                this.chooseWaterTarget();
            }
        }

        private void chooseWaterTarget() {
            Vec3 currentPos = this.entity.position();
            int range = 15;
            int minDistance = 8;

            for (int attempts = 0; attempts < 20; attempts++) {
                double offsetX = (this.entity.getRandom().nextDouble() - 0.5D) * range * 2;
                double offsetY = (this.entity.getRandom().nextDouble() - 0.5D) * range * 2;
                double offsetZ = (this.entity.getRandom().nextDouble() - 0.5D) * range * 2;

                double potentialX = currentPos.x + offsetX;
                double potentialY = currentPos.y + offsetY;
                double potentialZ = currentPos.z + offsetZ;

                double distanceSquared = (potentialX - currentPos.x) * (potentialX - currentPos.x) +
                        (potentialY - currentPos.y) * (potentialY - currentPos.y) +
                        (potentialZ - currentPos.z) * (potentialZ - currentPos.z);

                if (distanceSquared < minDistance * minDistance) {
                    continue;
                }

                BlockPos checkPos = new BlockPos((int)potentialX, (int)potentialY, (int)potentialZ);

                if (this.entity.level().getFluidState(checkPos).is(FluidTags.WATER)) {
                    this.targetX = potentialX;
                    this.targetY = potentialY;
                    this.targetZ = potentialZ;
                    this.entity.getNavigation().moveTo(this.targetX, this.targetY, this.targetZ, this.speed);
                    return;
                }
            }

            this.targetX = currentPos.x;
            this.targetY = currentPos.y;
            this.targetZ = currentPos.z;
        }
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    private boolean hasAI() {
        return !this.isNoAi() && this.level().getDifficulty() != Difficulty.PEACEFUL;
    }

    @Override
    public void travel(Vec3 movementInput) {
        if (this.hasAI() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), movementInput);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));

            if (this.getTarget() == null && this.getNavigation().isDone()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.005, 0.0));
            }
        } else {
            super.travel(movementInput);
        }
    }

    public boolean shouldSwim() {
        return this.isInWater() && this.getDeltaMovement().lengthSqr() > 0.0001;
    }

    public abstract boolean shouldDropXp();

    public boolean isFullySubmerged() {
        double entityHeight = this.getBbHeight();
        Vec3 pos = this.position();

        boolean bottomSubmerged = this.level().getFluidState(new BlockPos((int)pos.x, (int)pos.y, (int)pos.z)).is(FluidTags.WATER);
        boolean middleSubmerged = this.level().getFluidState(new BlockPos((int)pos.x, (int)(pos.y + entityHeight * 0.5), (int)pos.z)).is(FluidTags.WATER);
        boolean topSubmerged = this.level().getFluidState(new BlockPos((int)pos.x, (int)(pos.y + entityHeight), (int)pos.z)).is(FluidTags.WATER);

        return bottomSubmerged && middleSubmerged && topSubmerged;
    }

    @Override
    public void addAdditionalSaveData(ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
    }

    @Override
    public void readAdditionalSaveData(ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
    }

    public static class BrineSwimGoal extends Goal {
        private final HostileWaterCreatureEntity entity;

        public BrineSwimGoal(HostileWaterCreatureEntity entity) {
            this.entity = entity;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return true;
        }

        @Override
        public void tick() {
            if (this.entity.getTarget() != null) {
                return;
            }

            if (this.entity.tickCount % 80 == 0) {
                BlockPos targetPos = findRandomWaterBlock();
                if (targetPos != null) {
                    this.entity.getNavigation().moveTo(
                            targetPos.getX() + 0.5,
                            targetPos.getY() + 0.5,
                            targetPos.getZ() + 0.5,
                            1.0
                    );
                }
            }

            if (!this.entity.isInWater() && this.entity.tickCount % 20 == 0) {
                BlockPos waterPos = findNearbyWater();
                if (waterPos != null) {
                    this.entity.getNavigation().moveTo(
                            waterPos.getX() + 0.5,
                            waterPos.getY() + 0.5,
                            waterPos.getZ() + 0.5,
                            1.5
                    );
                }
            }
        }

        private BlockPos findRandomWaterBlock() {
            BlockPos entityPos = this.entity.blockPosition();

            for (int attempt = 0; attempt < 30; attempt++) {
                int offsetX = this.entity.getRandom().nextInt(15) - 7;
                int offsetY = this.entity.getRandom().nextInt(15) - 7;
                int offsetZ = this.entity.getRandom().nextInt(15) - 7;

                BlockPos testPos = entityPos.offset(offsetX, offsetY, offsetZ);

                if (isDeepWater(testPos)) {
                    return testPos;
                }
            }

            return null;
        }

        private boolean isDeepWater(BlockPos pos) {
            return this.entity.level().getFluidState(pos).is(FluidTags.WATER) &&
                    this.entity.level().getFluidState(pos.above()).is(FluidTags.WATER) &&
                    this.entity.level().getFluidState(pos.above(2)).is(FluidTags.WATER);
        }

        private BlockPos findNearbyWater() {
            BlockPos entityPos = this.entity.blockPosition();

            for (int range = 4; range <= 16; range += 4) {
                for (int x = -range; x <= range; x += 2) {
                    for (int y = -8; y <= 8; y += 2) {
                        for (int z = -range; z <= range; z += 2) {
                            BlockPos testPos = entityPos.offset(x, y, z);
                            if (this.entity.level().getFluidState(testPos).is(FluidTags.WATER)) {
                                return testPos;
                            }
                        }
                    }
                }
            }
            return null;
        }
    }

    public static boolean canSpawn(EntityType<? extends HostileWaterCreatureEntity> type, LevelAccessor world, EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return world.getFluidState(pos).is(FluidTags.WATER)
                && world.getFluidState(pos.below()).is(FluidTags.WATER)
                && world.getFluidState(pos.above()).is(FluidTags.WATER)
                && world.getDifficulty() != Difficulty.PEACEFUL;
    }

    @Override
    public boolean canStandOnFluid(FluidState state) {
        return false;
    }

    public boolean isSwimming() {
        return this.shouldSwim();
    }
}