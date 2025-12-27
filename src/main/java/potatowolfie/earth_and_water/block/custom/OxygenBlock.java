package potatowolfie.earth_and_water.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import potatowolfie.earth_and_water.block.ModBlocks;

public class OxygenBlock extends Block {
    public static final MapCodec<OxygenBlock> CODEC = createCodec(OxygenBlock::new);
    private static final int SCHEDULED_TICK_DELAY = 2;
    private static final int PARTICLE_HEIGHT = 2; 
    private static final int BUBBLE_HEIGHT = 3; 
    private static final float BUBBLE_BASE_SPEED = 0.2f;
    private static final float BUBBLE_RANDOM_SPEED = 0.1f;

    public OxygenBlock(Settings settings) {
        super(settings);
    }

    @Override
    public MapCodec<OxygenBlock> getCodec() {
        return CODEC;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        for (int y = 1; y <= PARTICLE_HEIGHT; y++) {
            BlockPos checkPos = pos.up(y);
            if (world.getFluidState(checkPos).isOf(Fluids.WATER)) {
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
        if (entity.isSubmergedInWater()) {
            int currentAir = entity.getAir();
            int maxAir = entity.getMaxAir();
            if (currentAir < maxAir) {
                entity.setAir(Math.min(currentAir + 4, maxAir));
            }
        }
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (!world.isClient() && entity instanceof LivingEntity living) {
            replenishAir(living);
        }
        super.onSteppedOn(world, pos, state, entity);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        for (int y = 1; y <= BUBBLE_HEIGHT; y++) {
            BlockPos bubblePos = pos.up(y);
            if (world.getFluidState(bubblePos).isOf(Fluids.WATER)) {
                BlockState currentState = world.getBlockState(bubblePos);
                if (!(currentState.getBlock() instanceof OxygenBubbleBlock)) {
                    world.setBlockState(bubblePos, ModBlocks.OXYGEN_BUBBLE.getDefaultState());
                }
            }
        }

        Box blockBox = new Box(pos).expand(0.3, 0, 0.3);
        world.getEntitiesByClass(LivingEntity.class, blockBox, Entity::isSubmergedInWater)
            .forEach(this::replenishAir);

        world.scheduleBlockTick(pos, this, SCHEDULED_TICK_DELAY);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
                                                WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.UP) {
            world.scheduleBlockTick(pos, this, SCHEDULED_TICK_DELAY);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        world.scheduleBlockTick(pos, this, SCHEDULED_TICK_DELAY);
    }
}
