package potatowolfie.earth_and_water.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import potatowolfie.earth_and_water.advancement.MobLockHandler;
import potatowolfie.earth_and_water.block.entity.ModBlockEntities;
import potatowolfie.earth_and_water.block.entity.custom.ReinforcedSpawnerBlockEntity;
import potatowolfie.earth_and_water.item.custom.ReinforcedKeyItem;

public class ReinforcedSpawnerBlock extends BlockWithEntity implements Waterloggable {
    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");
    public static final BooleanProperty KEYHOLE = BooleanProperty.of("keyhole");
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public ReinforcedSpawnerBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(ACTIVE, false)
                .with(KEYHOLE, false)
                .with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE, KEYHOLE, WATERLOGGED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return this.getDefaultState()
                .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(
            BlockState state,
            WorldView world,
            ScheduledTickView tickView,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            Random random
    ) {
        if (state.get(WATERLOGGED)) {
            tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ReinforcedSpawnerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.REINFORCED_SPAWNER_BLOCK_ENTITY,
                world.isClient() ? ReinforcedSpawnerBlockEntity::clientTick : ReinforcedSpawnerBlockEntity::serverTick);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
                                 BlockHitResult hit) {
        if (world.isClient()) return ActionResult.SUCCESS;
        ItemStack stack = player.getMainHandStack();

        if (world.getBlockEntity(pos) instanceof ReinforcedSpawnerBlockEntity spawner) {
            if (stack.getItem() instanceof SpawnEggItem spawnEggItem) {
                EntityType<?> entityType = spawnEggItem.getEntityType(stack);

                spawner.setEntityType(entityType);

                if (!player.getAbilities().creativeMode) {
                    stack.decrement(1);
                }

                world.playSound(null, pos, SoundEvents.BLOCK_TRIAL_SPAWNER_SPAWN_MOB,
                        SoundCategory.BLOCKS, 1.0f, 1.0f);

                return ActionResult.SUCCESS;
            }

            if (stack.getItem() instanceof ReinforcedKeyItem) {
                if (!spawner.canUseKey(world)) {
                    return ActionResult.PASS;
                }

                if (!state.get(ACTIVE) && state.get(KEYHOLE)) {
                    if (spawner.getEntityType() != null) {
                        world.setBlockState(pos, state
                                .with(ACTIVE, true)
                                .with(KEYHOLE, false), 3);
                        spawner.activate();
                        spawner.onKeyUsed(world);

                        if (!player.getAbilities().creativeMode) {
                            stack.decrement(1);
                        }

                        world.playSound(null, pos, SoundEvents.BLOCK_VAULT_DEACTIVATE,
                                SoundCategory.BLOCKS, 1.0f, 1.0f);

                        return ActionResult.SUCCESS;
                    }
                } else if (state.get(ACTIVE)) {
                    world.setBlockState(pos, state
                            .with(ACTIVE, false)
                            .with(KEYHOLE, false), 3);
                    spawner.deactivate();
                    spawner.onKeyUsed(world);

                    if (!player.getAbilities().creativeMode) {
                        stack.decrement(1);
                    }

                    world.playSound(null, pos, SoundEvents.BLOCK_VAULT_DEACTIVATE,
                            SoundCategory.BLOCKS, 1.0f, 1.0f);

                    if (player instanceof ServerPlayerEntity serverPlayer) {
                        MobLockHandler.grantDeactivateSpawnerAdvancement(serverPlayer);
                    }

                    return ActionResult.SUCCESS;
                }
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }
}