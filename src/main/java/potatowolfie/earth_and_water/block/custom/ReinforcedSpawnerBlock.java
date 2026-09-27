package potatowolfie.earth_and_water.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import potatowolfie.earth_and_water.advancement.MobLockHandler;
import potatowolfie.earth_and_water.block.entity.ModBlockEntities;
import potatowolfie.earth_and_water.block.entity.custom.ReinforcedSpawnerBlockEntity;
import potatowolfie.earth_and_water.item.custom.ReinforcedKeyItem;

public class ReinforcedSpawnerBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final BooleanProperty KEYHOLE = BooleanProperty.create("keyhole");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public ReinforcedSpawnerBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState()
                .setValue(ACTIVE, false)
                .setValue(KEYHOLE, false)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE, KEYHOLE, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        return this.defaultBlockState()
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            LevelReader world,
            ScheduledTickAccess tickView,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource random
    ) {
        if (state.getValue(WATERLOGGED)) {
            tickView.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return super.updateShape(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ReinforcedSpawnerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.REINFORCED_SPAWNER_BLOCK_ENTITY,
                world.isClientSide() ? ReinforcedSpawnerBlockEntity::clientTick : ReinforcedSpawnerBlockEntity::serverTick);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (world.isClientSide()) return InteractionResult.SUCCESS;
        ItemStack stack = player.getMainHandItem();

        if (world.getBlockEntity(pos) instanceof ReinforcedSpawnerBlockEntity spawner) {
            if (stack.getItem() instanceof SpawnEggItem spawnEggItem) {
                EntityType<?> entityType = spawnEggItem.getType(stack);

                spawner.setEntityType(entityType);

                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }

                world.playSound(null, pos, SoundEvents.TRIAL_SPAWNER_SPAWN_MOB,
                        SoundSource.BLOCKS, 1.0f, 1.0f);

                return InteractionResult.SUCCESS;
            }

            if (stack.getItem() instanceof ReinforcedKeyItem) {
                if (!spawner.canUseKey(world)) {
                    return InteractionResult.PASS;
                }

                if (!state.getValue(ACTIVE) && state.getValue(KEYHOLE)) {
                    if (spawner.getEntityType() != null) {
                        world.setBlock(pos, state
                                .setValue(ACTIVE, true)
                                .setValue(KEYHOLE, false), 3);
                        spawner.activate();
                        spawner.onKeyUsed(world);

                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                        }

                        world.playSound(null, pos, SoundEvents.VAULT_DEACTIVATE,
                                SoundSource.BLOCKS, 1.0f, 1.0f);

                        return InteractionResult.SUCCESS;
                    }
                } else if (state.getValue(ACTIVE)) {
                    world.setBlock(pos, state
                            .setValue(ACTIVE, false)
                            .setValue(KEYHOLE, false), 3);
                    spawner.deactivate();
                    spawner.onKeyUsed(world);

                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }

                    world.playSound(null, pos, SoundEvents.VAULT_DEACTIVATE,
                            SoundSource.BLOCKS, 1.0f, 1.0f);

                    if (player instanceof ServerPlayer serverPlayer) {
                        MobLockHandler.grantDeactivateSpawnerAdvancement(serverPlayer);
                    }

                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}