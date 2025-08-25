package potatowolfie.earth_and_water.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;

public class ChiseledPrismarineBricksBlock extends Block {
    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");

    public ChiseledPrismarineBricksBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(ACTIVE, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock())) {
            boolean shouldBeActive = checkForWater(world, pos);
            if (shouldBeActive != state.get(ACTIVE)) {
                world.setBlockState(pos, state.with(ACTIVE, shouldBeActive), Block.NOTIFY_ALL);
            }

            world.scheduleBlockTick(pos, this, 2);
        }
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
                                                WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (world instanceof World) {
            ((World) world).scheduleBlockTick(pos, this, 2);
        }

        boolean shouldBeActive = checkForWater(world, pos);
        return shouldBeActive != state.get(ACTIVE) ? state.with(ACTIVE, shouldBeActive) : state;
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        boolean shouldBeActive = checkForWater(world, pos);
        if (shouldBeActive != state.get(ACTIVE)) {
            world.setBlockState(pos, state.with(ACTIVE, shouldBeActive), Block.NOTIFY_ALL);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        boolean shouldBeActive = checkForWater(world, pos);
        if (shouldBeActive != state.get(ACTIVE)) {
            world.setBlockState(pos, state.with(ACTIVE, shouldBeActive), Block.NOTIFY_ALL);
        }

        world.scheduleBlockTick(pos, this, 2);
    }

    private boolean checkForWater(WorldAccess world, BlockPos pos) {
        FluidState blockFluidState = world.getFluidState(pos);
        if (blockFluidState.getFluid() == Fluids.WATER || blockFluidState.getFluid() == Fluids.FLOWING_WATER) {
            return true;
        }

        for (Direction direction : Direction.values()) {
            BlockPos adjacentPos = pos.offset(direction);

            BlockState adjacentState = world.getBlockState(adjacentPos);
            FluidState fluidState = world.getFluidState(adjacentPos);

            if (fluidState.getFluid() == Fluids.WATER ||
                    fluidState.getFluid() == Fluids.FLOWING_WATER ||
                    adjacentState.isOf(Blocks.WATER)) {
                return true;
            }
        }

        BlockPos belowPos = pos.down();
        FluidState belowFluidState = world.getFluidState(belowPos);
        if (belowFluidState.getFluid() == Fluids.WATER || belowFluidState.getFluid() == Fluids.FLOWING_WATER) {
            return true;
        }

        return false;
    }
}