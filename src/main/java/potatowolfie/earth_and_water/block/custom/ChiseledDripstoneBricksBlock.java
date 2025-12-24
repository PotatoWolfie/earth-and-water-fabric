package potatowolfie.earth_and_water.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import potatowolfie.earth_and_water.EarthWater;

import java.util.List;

public class ChiseledDripstoneBricksBlock extends Block {
    public static final BooleanProperty POWERED = BooleanProperty.of("powered");

    public ChiseledDripstoneBricksBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient() && !state.isOf(oldState.getBlock())) {
            world.scheduleBlockTick(pos, this, 2);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        if (!ctx.getWorld().isClient()) {
            ctx.getWorld().scheduleBlockTick(ctx.getBlockPos(), this, 2);
        }
        return this.getDefaultState().with(POWERED, false);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        if (!world.isClient()) {
            double x = pos.getX();
            double y = pos.getY();
            double z = pos.getZ();
            Box box = new Box(x - 5, y - 5, z - 5, x + 5, y + 5, z + 5);

            List<Entity> entities = world.getOtherEntities(null, box,
                    entity -> !(entity.isSpectator()) && !entity.isInvisible());

            boolean entityNearby = !entities.isEmpty();
            boolean wasPowered = state.get(POWERED);

            if (entityNearby != wasPowered) {
                world.setBlockState(pos, state.with(POWERED, entityNearby));
                world.updateNeighborsAlways(pos, this, WireOrientation.fromOrdinal(0));

                if (entityNearby && !wasPowered) {
                    spawnParticleBurst(world, pos);
                }
            }

            world.scheduleBlockTick(pos, this, 2);
        }
    }

    private void spawnParticleBurst(ServerWorld world, BlockPos pos) {
        Random random = world.getRandom();
        for (int i = 0; i < 20; i++) {
            double offsetX = random.nextDouble() * 0.6 - 0.3;
            double offsetY = random.nextDouble() * 0.6 - 0.3;
            double offsetZ = random.nextDouble() * 0.6 - 0.3;

            double velocityX = offsetX * 0.3;
            double velocityY = random.nextDouble() * 0.2 + 0.1;
            double velocityZ = offsetZ * 0.3;
            world.spawnParticles(
                    EarthWater.LIGHT_UP,
                    pos.getX() + 0.5 + offsetX,
                    pos.getY() + 0.5 + offsetY,
                    pos.getZ() + 0.5 + offsetZ,
                    1,
                    velocityX,
                    velocityY,
                    velocityZ,
                    0.05
            );
        }
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return 0;
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos, Direction direction) {
        return state.get(POWERED) ? 15 : 0;
    }
}