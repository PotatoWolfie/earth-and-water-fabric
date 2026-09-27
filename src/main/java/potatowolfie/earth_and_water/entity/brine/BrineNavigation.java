package potatowolfie.earth_and_water.entity.brine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import potatowolfie.earth_and_water.entity.goals.HostileWaterCreatureEntity;

public class BrineNavigation extends PathNavigation {
    private boolean wasInWater;
    private boolean wasFullySubmerged;

    public BrineNavigation(Mob entity, Level world) {
        super(entity, world);
        this.wasInWater = false;
        this.wasFullySubmerged = false;
    }

    @Override
    protected PathFinder createPathFinder(int range) {
        boolean inWater = this.mob.isInWater();

        if (inWater) {
            this.nodeEvaluator = new SwimNodeEvaluator(false);
        } else {
            this.nodeEvaluator = new WalkNodeEvaluator();
            ((WalkNodeEvaluator)this.nodeEvaluator).setCanPassDoors(true);
            ((WalkNodeEvaluator)this.nodeEvaluator).setCanOpenDoors(false);
        }

        return new PathFinder(this.nodeEvaluator, range);
    }

    @Override
    protected boolean canUpdatePath() {
        return true;
    }

    @Override
    protected Vec3 getTempMobPos() {
        return new Vec3(this.mob.getX(), this.mob.getY(0.5), this.mob.getZ());
    }

    @Override
    protected double getGroundY(Vec3 pos) {
        return pos.y;
    }

    @Override
    protected boolean canMoveDirectly(Vec3 origin, Vec3 target) {
        return isClearForMovementBetween(this.mob, origin, target, false);
    }

    @Override
    public boolean isStableDestination(BlockPos pos) {
        if (this.mob.isInWater()) {
            return !this.level.getBlockState(pos).isSolidRender();
        } else {
            return this.level.getBlockState(pos.below()).entityCanStandOn(this.level, pos.below(), this.mob);
        }
    }

    public boolean isFullySubmerged() {
        if (this.mob instanceof HostileWaterCreatureEntity) {
            return ((HostileWaterCreatureEntity) this.mob).isFullySubmerged();
        }
        return false;
    }

    @Override
    public void tick() {
        boolean currentlyInWater = this.mob.isInWater();
        boolean currentlyFullySubmerged = isFullySubmerged();

        if (this.wasInWater != currentlyInWater) {
            this.wasInWater = currentlyInWater;

            if (!this.isDone()) {
                Vec3 targetPos = Vec3.atLowerCornerOf(this.getTargetPos());
                if (targetPos != null) {
                    this.stop();
                    this.moveTo(targetPos.x, targetPos.y, targetPos.z, this.speedModifier);
                }
            }
        }

        this.wasFullySubmerged = currentlyFullySubmerged;

        super.tick();
    }

    @Override
    public boolean moveTo(double x, double y, double z, double speed) {
        this.wasInWater = this.mob.isInWater();
        this.wasFullySubmerged = isFullySubmerged();
        return super.moveTo(x, y, z, speed);
    }

    public boolean shouldUseVelocityMovement() {
        return isFullySubmerged();
    }

    @Override
    public void setCanFloat(boolean canSwim) {
    }

    @Override
    public boolean canNavigateGround() {
        return false;
    }
}