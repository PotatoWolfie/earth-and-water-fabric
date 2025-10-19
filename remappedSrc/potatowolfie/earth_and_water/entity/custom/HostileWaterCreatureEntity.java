package potatowolfie.earth_and_water.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class HostileWaterCreatureEntity extends HostileEntity {

    protected HostileWaterCreatureEntity(EntityType<? extends HostileWaterCreatureEntity> entityType, World world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
        this.moveControl = new HostileWaterCreatureEntity.WaterMoveControl(this);
    }

    public boolean canSpawn(WorldView world) {
        return world.doesNotIntersectEntities(this);
    }

    public int getMinAmbientSoundDelay() {
        return 120;
    }

    protected int getXpToDrop() {
        return 1 + this.getWorld().random.nextInt(3);
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

    @Override
    public boolean isPushedByFluids() {
        return false;
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    private boolean hasAI() {
        return !this.isAiDisabled() && this.getWorld().getDifficulty() != Difficulty.PEACEFUL;
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

    @Override
    public void writeCustomData(WriteView nbt) {
        super.writeCustomData(nbt);
    }

    @Override
    public void readCustomData(ReadView nbt) {
        super.readCustomData(nbt);
    }

    static class WaterMoveControl extends MoveControl {
        private final HostileWaterCreatureEntity entity;

        public WaterMoveControl(HostileWaterCreatureEntity entity) {
            super(entity);
            this.entity = entity;
        }

        @Override
        public void tick() {
            if (this.entity.isTouchingWater()) {
                if (this.state == MoveControl.State.MOVE_TO) {
                    double d = this.targetX - this.entity.getX();
                    double e = this.targetY - this.entity.getY();
                    double f = this.targetZ - this.entity.getZ();
                    double g = Math.sqrt(d * d + e * e + f * f);

                    if (g < 2.5000003E-7) {
                        this.entity.setForwardSpeed(0.0F);
                    } else {
                        float h = (float)(MathHelper.atan2(f, d) * 180.0 / Math.PI) - 90.0F;
                        this.entity.setYaw(this.wrapDegrees(this.entity.getYaw(), h, 10.0F));
                        this.entity.bodyYaw = this.entity.getYaw();
                        this.entity.headYaw = this.entity.getYaw();
                        float i = (float)(this.speed * this.entity.getAttributeValue(EntityAttributes.MOVEMENT_SPEED));

                        if (this.entity.isTouchingWater()) {
                            this.entity.setMovementSpeed(i * 0.02F);
                            float j = -((float)(MathHelper.atan2(e, Math.sqrt(d * d + f * f)) * 180.0 / Math.PI));
                            j = MathHelper.clamp(MathHelper.wrapDegrees(j), -85.0F, 85.0F);
                            this.entity.setPitch(this.wrapDegrees(this.entity.getPitch(), j, 5.0F));
                            float k = MathHelper.cos(this.entity.getPitch() * (float) (Math.PI / 180.0));
                            float l = MathHelper.sin(this.entity.getPitch() * (float) (Math.PI / 180.0));
                            this.entity.forwardSpeed = k * i;
                            this.entity.upwardSpeed = -l * i;
                        } else {
                            this.entity.setMovementSpeed(i * 0.1F);
                        }
                    }
                } else {
                    this.entity.setMovementSpeed(0.0F);
                    this.entity.setSidewaysSpeed(0.0F);
                    this.entity.setUpwardSpeed(0.0F);
                    this.entity.setForwardSpeed(0.0F);
                }
            } else {
                super.tick();
            }
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