package potatowolfie.earth_and_water.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.EnumSet;

public abstract class HostileWaterCreatureEntity extends HostileEntity {

    protected HostileWaterCreatureEntity(EntityType<? extends HostileWaterCreatureEntity> entityType, World world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
        this.moveControl = new MoveControl(this);
    }

    public boolean canSpawn(WorldView world) {
        return world.doesNotIntersectEntities(this);
    }

    public int getMinAmbientSoundDelay() {
        return 120;
    }

    protected int getXpToDrop() {
        return 1 + this.getEntityWorld().random.nextInt(3);
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

    protected void tickWaterBreathingAir(int air) {
        this.setAir(this.getMaxAir());
    }

    @Override
    public void baseTick() {
        int i = this.getAir();
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
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (this.entity.isTouchingWater()) {
                return false;
            }

            this.targetWaterPos = findNearbyWater();
            return this.targetWaterPos != null;
        }

        @Override
        public boolean shouldContinue() {
            return !this.entity.isTouchingWater() &&
                    this.targetWaterPos != null &&
                    !this.entity.getNavigation().isIdle();
        }

        @Override
        public void start() {
            if (this.targetWaterPos != null) {
                this.entity.getNavigation().startMovingTo(
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
            BlockPos entityPos = this.entity.getBlockPos();
            int searchRange = 16;

            for (int range = 4; range <= searchRange; range += 4) {
                for (int x = -range; x <= range; x += 2) {
                    for (int y = -8; y <= 8; y += 2) {
                        for (int z = -range; z <= range; z += 2) {
                            BlockPos checkPos = entityPos.add(x, y, z);
                            if (this.entity.getEntityWorld().getFluidState(checkPos).isIn(FluidTags.WATER)) {
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
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (this.entity.hasPassengers() || this.entity.getTarget() != null) {
                return false;
            }

            if (this.entity.getRandom().nextInt(this.chance) != 0) {
                return false;
            }

            return this.entity.isTouchingWater();
        }

        @Override
        public void start() {
            this.chooseWaterTarget();
        }

        @Override
        public boolean shouldContinue() {
            return !this.entity.getNavigation().isIdle() && this.entity.isTouchingWater();
        }

        @Override
        public void stop() {
            this.entity.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (this.entity.getRandom().nextInt(150) == 0 ||
                    this.entity.squaredDistanceTo(this.targetX, this.targetY, this.targetZ) < 1.0D) {
                this.chooseWaterTarget();
            }
        }

        private void chooseWaterTarget() {
            Vec3d currentPos = this.entity.getPos();
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

                if (this.entity.getEntityWorld().getFluidState(checkPos).isIn(FluidTags.WATER)) {
                    this.targetX = potentialX;
                    this.targetY = potentialY;
                    this.targetZ = potentialZ;
                    this.entity.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
                    return;
                }
            }

            this.targetX = currentPos.x;
            this.targetY = currentPos.y;
            this.targetZ = currentPos.z;
        }
    }

    @Override
    public boolean isPushedByFluids() {
        return false;
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    private boolean hasAI() {
        return !this.isAiDisabled() && this.getEntityWorld().getDifficulty() != Difficulty.PEACEFUL;
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (this.hasAI() && this.isTouchingWater()) {
            this.updateVelocity(this.getMovementSpeed(), movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.9));

            if (this.getTarget() == null && this.getNavigation().isIdle()) {
                this.setVelocity(this.getVelocity().add(0.0, -0.005, 0.0));
            }
        } else {
            super.travel(movementInput);
        }
    }

    public boolean shouldSwim() {
        return this.isTouchingWater() && this.getVelocity().lengthSquared() > 0.0001;
    }

    public abstract boolean shouldDropXp();

    public boolean isFullySubmerged() {
        double entityHeight = this.getHeight();
        Vec3d pos = this.getPos();

        boolean bottomSubmerged = this.getEntityWorld().getFluidState(new BlockPos((int)pos.x, (int)pos.y, (int)pos.z)).isIn(FluidTags.WATER);
        boolean middleSubmerged = this.getEntityWorld().getFluidState(new BlockPos((int)pos.x, (int)(pos.y + entityHeight * 0.5), (int)pos.z)).isIn(FluidTags.WATER);
        boolean topSubmerged = this.getEntityWorld().getFluidState(new BlockPos((int)pos.x, (int)(pos.y + entityHeight), (int)pos.z)).isIn(FluidTags.WATER);

        return bottomSubmerged && middleSubmerged && topSubmerged;
    }

    public static class BrineSwimGoal extends Goal {
        private final HostileWaterCreatureEntity entity;

        public BrineSwimGoal(HostileWaterCreatureEntity entity) {
            this.entity = entity;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return true;
        }

        @Override
        public boolean shouldContinue() {
            return true;
        }

        @Override
        public void tick() {
            if (this.entity.getTarget() != null) {
                return;
            }

            if (this.entity.age % 80 == 0) {
                BlockPos targetPos = findRandomWaterBlock();
                if (targetPos != null) {
                    this.entity.getNavigation().startMovingTo(
                            targetPos.getX() + 0.5,
                            targetPos.getY() + 0.5,
                            targetPos.getZ() + 0.5,
                            1.0
                    );
                }
            }

            if (!this.entity.isTouchingWater() && this.entity.age % 20 == 0) {
                BlockPos waterPos = findNearbyWater();
                if (waterPos != null) {
                    this.entity.getNavigation().startMovingTo(
                            waterPos.getX() + 0.5,
                            waterPos.getY() + 0.5,
                            waterPos.getZ() + 0.5,
                            1.5
                    );
                }
            }
        }

        private BlockPos findRandomWaterBlock() {
            BlockPos entityPos = this.entity.getBlockPos();

            for (int attempt = 0; attempt < 30; attempt++) {
                int offsetX = this.entity.getRandom().nextInt(15) - 7;
                int offsetY = this.entity.getRandom().nextInt(15) - 7;
                int offsetZ = this.entity.getRandom().nextInt(15) - 7;

                BlockPos testPos = entityPos.add(offsetX, offsetY, offsetZ);

                if (isDeepWater(testPos)) {
                    return testPos;
                }
            }

            return null;
        }

        private boolean isDeepWater(BlockPos pos) {
            return this.entity.getEntityWorld().getFluidState(pos).isIn(FluidTags.WATER) &&
                    this.entity.getEntityWorld().getFluidState(pos.up()).isIn(FluidTags.WATER) &&
                    this.entity.getEntityWorld().getFluidState(pos.up(2)).isIn(FluidTags.WATER);
        }

        private BlockPos findNearbyWater() {
            BlockPos entityPos = this.entity.getBlockPos();

            for (int range = 4; range <= 16; range += 4) {
                for (int x = -range; x <= range; x += 2) {
                    for (int y = -8; y <= 8; y += 2) {
                        for (int z = -range; z <= range; z += 2) {
                            BlockPos testPos = entityPos.add(x, y, z);
                            if (this.entity.getEntityWorld().getFluidState(testPos).isIn(FluidTags.WATER)) {
                                return testPos;
                            }
                        }
                    }
                }
            }
            return null;
        }
    }

    public static boolean canSpawn(EntityType<? extends HostileWaterCreatureEntity> type, WorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
        return world.getFluidState(pos).isIn(FluidTags.WATER)
                && world.getFluidState(pos.down()).isIn(FluidTags.WATER)
                && world.getFluidState(pos.up()).isIn(FluidTags.WATER)
                && world.getDifficulty() != Difficulty.PEACEFUL;
    }

    @Override
    public boolean canWalkOnFluid(FluidState state) {
        return false;
    }

    public boolean isSwimming() {
        return this.shouldSwim();
    }
}