package potatowolfie.earth_and_water.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import potatowolfie.earth_and_water.block.ModBlocks;

public class OxygenBlock extends Block {
    private static final int SCHEDULED_TICK_DELAY = 2;
    private static final int PARTICLE_HEIGHT = 2;
    private static final int BUBBLE_HEIGHT = 3;
    private static final float BUBBLE_BASE_SPEED = 0.2f;
    private static final float BUBBLE_RANDOM_SPEED = 0.1f;

    public OxygenBlock(Properties settings) {
        super(settings);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        for (int y = 1; y <= PARTICLE_HEIGHT; y++) {
            BlockPos checkPos = pos.above(y);
            if (world.getFluidState(checkPos).is(Fluids.WATER)) {
                double xPos = pos.getX() + 0.5f;
                double yPos = pos.getY() + y;
                double zPos = pos.getZ() + 0.5f;

                float heightFactor = 1.0f - ((float)y / PARTICLE_HEIGHT);
                float upwardSpeed = BUBBLE_BASE_SPEED + (random.nextFloat() * BUBBLE_RANDOM_SPEED);
                upwardSpeed *= heightFactor;

                for (int i = 0; i < 2; i++) {
                    double offsetX = random.nextDouble() * 0.6 - 0.3;
                    double offsetZ = random.nextDouble() * 0.6 - 0.3;
                    world.addParticle(ParticleTypes.BUBBLE_COLUMN_UP,
                            xPos + offsetX, yPos, zPos + offsetZ,
                            0.0, upwardSpeed, 0.0);
                }

                if (y == PARTICLE_HEIGHT && random.nextInt(5) == 0) {
                    world.addParticle(ParticleTypes.BUBBLE_POP,
                            xPos + (random.nextDouble() - 0.5) * 0.6,
                            yPos + 0.5,
                            zPos + (random.nextDouble() - 0.5) * 0.6,
                            0.0, 0.0, 0.0);
                }
            }
        }
    }

    private void replenishAir(LivingEntity entity) {
        if (entity.isUnderWater()) {
            int currentAir = entity.getAirSupply();
            int maxAir = entity.getMaxAirSupply();
            if (currentAir < maxAir) {
                entity.setAirSupply(Math.min(currentAir + 4, maxAir));
            }
        }
    }

    @Override
    public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
        if (!world.isClientSide() && entity instanceof LivingEntity living) {
            replenishAir(living);
        }
        super.stepOn(world, pos, state, entity);
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        for (int y = 1; y <= BUBBLE_HEIGHT; y++) {
            BlockPos bubblePos = pos.above(y);
            if (world.getFluidState(bubblePos).is(Fluids.WATER)) {
                BlockState currentState = world.getBlockState(bubblePos);
                if (!(currentState.getBlock() instanceof OxygenBubbleBlock)) {
                    world.setBlockAndUpdate(bubblePos, ModBlocks.OXYGEN_BUBBLE.defaultBlockState());
                }
            }
        }

        AABB blockBox = new AABB(pos).inflate(0.3, 0, 0.3);
        world.getEntitiesOfClass(LivingEntity.class, blockBox, Entity::isUnderWater)
                .forEach(this::replenishAir);

        world.scheduleTick(pos, this, SCHEDULED_TICK_DELAY);
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess tickView,
                                  BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (direction == Direction.UP) {
            tickView.scheduleTick(pos, this, SCHEDULED_TICK_DELAY);
        }
        return super.updateShape(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        world.scheduleTick(pos, this, SCHEDULED_TICK_DELAY);
    }
}