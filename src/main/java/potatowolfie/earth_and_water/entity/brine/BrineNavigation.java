package potatowolfie.earth_and_water.entity.brine;

import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.WaterPathNodeMaker;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import potatowolfie.earth_and_water.entity.custom.HostileWaterCreatureEntity;

public class BrineNavigation extends EntityNavigation {
    private boolean wasInWater;
    private boolean wasFullySubmerged;

    public BrineNavigation(MobEntity entity, World world) {
        super(entity, world);
        this.wasInWater = false;
        this.wasFullySubmerged = false;
    }

    @Override
    protected PathNodeNavigator createPathNodeNavigator(int range) {
        boolean inWater = this.entity.isTouchingWater();

        if (inWater) {
            this.nodeMaker = new WaterPathNodeMaker(false);
        } else {
            this.nodeMaker = new LandPathNodeMaker();
            ((LandPathNodeMaker)this.nodeMaker).setCanEnterOpenDoors(true);
            ((LandPathNodeMaker)this.nodeMaker).setCanOpenDoors(false);
        }

        return new PathNodeNavigator(this.nodeMaker, range);
    }

    @Override
    protected boolean isAtValidPosition() {
        return true;
    }

    @Override
    protected Vec3d getPos() {
        return new Vec3d(this.entity.getX(), this.entity.getBodyY(0.5), this.entity.getZ());
    }

    @Override
    protected double adjustTargetY(Vec3d pos) {
        return pos.y;
    }

    @Override
    protected boolean canPathDirectlyThrough(Vec3d origin, Vec3d target) {
        return doesNotCollide(this.entity, origin, target, false);
    }

    @Override
    public boolean isValidPosition(BlockPos pos) {
        if (this.entity.isTouchingWater()) {
            return !this.world.getBlockState(pos).isOpaqueFullCube(this.world, pos);
        } else {
            return this.world.getBlockState(pos.down()).hasSolidTopSurface(this.world, pos.down(), this.entity);
        }
    }

    public boolean isFullySubmerged() {
        if (this.entity instanceof HostileWaterCreatureEntity) {
            return ((HostileWaterCreatureEntity) this.entity).isFullySubmerged();
        }
        return false;
    }

    @Override
    public void tick() {
        boolean currentlyInWater = this.entity.isTouchingWater();
        boolean currentlyFullySubmerged = isFullySubmerged();

        if (this.wasInWater != currentlyInWater) {
            this.wasInWater = currentlyInWater;

            if (!this.isIdle()) {
                Vec3d targetPos = Vec3d.of(this.getTargetPos());
                if (targetPos != null) {
                    this.stop();
                    this.startMovingTo(targetPos.x, targetPos.y, targetPos.z, this.speed);
                }
            }
        }

        this.wasFullySubmerged = currentlyFullySubmerged;

        super.tick();
    }

    @Override
    public boolean startMovingTo(double x, double y, double z, double speed) {
        this.wasInWater = this.entity.isTouchingWater();
        this.wasFullySubmerged = isFullySubmerged();
        return super.startMovingTo(x, y, z, speed);
    }

    public boolean shouldUseVelocityMovement() {
        return isFullySubmerged();
    }

    @Override
    public void setCanSwim(boolean canSwim) {
    }
}