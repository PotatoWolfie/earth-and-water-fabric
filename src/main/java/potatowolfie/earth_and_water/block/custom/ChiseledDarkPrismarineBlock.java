package potatowolfie.earth_and_water.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;

public class ChiseledDarkPrismarineBlock extends Block {
    public static final IntProperty Y_LEVEL_STATE = IntProperty.of("y_level", 0, 3);

    public ChiseledDarkPrismarineBlock(Settings settings) {
        super(settings.luminance(state -> getLuminanceForState(state)));
        this.setDefaultState(this.getStateManager().getDefaultState().with(Y_LEVEL_STATE, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Y_LEVEL_STATE);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock())) {
            int newState = getStateForYLevel(pos.getY());
            if (newState != state.get(Y_LEVEL_STATE)) {
                world.setBlockState(pos, state.with(Y_LEVEL_STATE, newState), Block.NOTIFY_ALL);
            }
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        int newState = getStateForYLevel(ctx.getBlockPos().getY());
        return this.getDefaultState().with(Y_LEVEL_STATE, newState);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int newState = getStateForYLevel(pos.getY());
        if (newState != state.get(Y_LEVEL_STATE)) {
            world.setBlockState(pos, state.with(Y_LEVEL_STATE, newState), Block.NOTIFY_ALL);
        }
    }

    private int getStateForYLevel(int yLevel) {
        if (yLevel >= 32 && yLevel <= 320000000) {
            return 3;
        } else if (yLevel >= 0 && yLevel <= 31) {
            return 2;
        } else if (yLevel >= -32 && yLevel <= -1) {
            return 1;
        } else {
            return 0;
        }
    }

    private static int getLuminanceForState(BlockState state) {
        if (state.contains(Y_LEVEL_STATE)) {
            int yLevelState = state.get(Y_LEVEL_STATE);
            switch (yLevelState) {
                case 1: return 4;
                case 2: return 8;
                case 3: return 12;
                default: return 0;
            }
        }
        return 0;
    }
}