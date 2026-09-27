package potatowolfie.earth_and_water.block.custom;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SpeleothemBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SpeleothemThickness;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;
import potatowolfie.earth_and_water.accessor.AbstractCauldronBlockAccessor;
import potatowolfie.earth_and_water.block.ModBlocks;

public class PointedDarkDripstoneBlock extends SpeleothemBlock {

    private static final int MAX_SEARCH_LENGTH_WHEN_CHECKING_DRIP_TYPE = 11;
    private static final float DRIP_PROBABILITY_PER_ANIMATE_TICK = 0.02F;
    private static final float DRIP_PROBABILITY_PER_ANIMATE_TICK_IF_UNDER_LIQUID_SOURCE = 0.12F;
    private static final int MAX_SEARCH_LENGTH_BETWEEN_STALACTITE_TIP_AND_CAULDRON = 11;
    private static final float WATER_TRANSFER_PROBABILITY_PER_RANDOM_TICK = 0.17578125F;
    private static final float LAVA_TRANSFER_PROBABILITY_PER_RANDOM_TICK = 0.05859375F;
    private static final float STALAGMITE_FALL_DISTANCE_OFFSET = 2.5F;
    private static final int STALAGMITE_FALL_DAMAGE_MODIFIER = 2;
    private static final double STALACTITE_DRIP_START_PIXEL;
    private static final VoxelShape REQUIRED_SPACE_TO_DRIP_THROUGH_NON_SOLID_BLOCK;
    private static final VoxelShape TIP_MERGE_SHAPE;
    private static final VoxelShape UP_TIP_SHAPE;
    private static final VoxelShape DOWN_TIP_SHAPE;
    private static final VoxelShape BASE_SHAPE;
    private static final VoxelShape FRUSTUM_SHAPE;
    private static final VoxelShape MIDDLE_SHAPE;
    private static final float MAX_HORIZONTAL_MODEL_OFFSET;

    public PointedDarkDripstoneBlock(final BlockState blockToGrowOn, final BlockBehaviour.Properties properties) {
        super(blockToGrowOn, properties);
    }

    @Override
    protected int getStalactiteLandingSound() {
        return 1045;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        LevelAccessor worldAccess = ctx.getLevel();
        BlockPos blockPos = ctx.getClickedPos();
        Direction direction = ctx.getNearestLookingVerticalDirection().getOpposite();
        Direction direction2 = getDirectionToPlaceAt(worldAccess, blockPos, direction);
        if (direction2 == null) {
            return null;
        }
        boolean tryMerge = !ctx.isSecondaryUseActive();
        SpeleothemThickness thickness = getThickness(worldAccess, blockPos, direction2, tryMerge);
        return thickness == null ? null :
                this.defaultBlockState()
                        .setValue(TIP_DIRECTION, direction2)
                        .setValue(THICKNESS, thickness)
                        .setValue(WATERLOGGED, worldAccess.getFluidState(blockPos).getType() == Fluids.WATER);
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state) {
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        VoxelShape voxelShape = switch ((SpeleothemThickness) state.getValue(THICKNESS)) {
            case TIP_MERGE -> TIP_MERGE_SHAPE;
            case TIP -> state.getValue(TIP_DIRECTION) == Direction.DOWN ? DOWN_TIP_SHAPE : UP_TIP_SHAPE;
            case FRUSTUM -> BASE_SHAPE;
            case MIDDLE -> FRUSTUM_SHAPE;
            case BASE -> MIDDLE_SHAPE;
        };
        return voxelShape.move(state.getOffset(pos));
    }

    @Override
    protected boolean isCollisionShapeFullBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return false;
    }

    @Override
    protected float getMaxHorizontalOffset() {
        return MAX_HORIZONTAL_MODEL_OFFSET;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Override
    public void fallOn(final Level level, final BlockState state, final BlockPos pos, final Entity entity, final double fallDistance) {
        if (state.getValue(TIP_DIRECTION) == Direction.UP && state.getValue(THICKNESS) == SpeleothemThickness.TIP) {
            entity.causeFallDamage(fallDistance + STALAGMITE_FALL_DISTANCE_OFFSET, STALAGMITE_FALL_DAMAGE_MODIFIER, level.damageSources().stalagmite());
        } else {
            super.fallOn(level, state, pos, entity, fallDistance);
        }
    }

    @Override
    protected void onProjectileHit(Level world, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (!world.isClientSide()) {
            BlockPos blockPos = hit.getBlockPos();
            if (world instanceof ServerLevel serverWorld) {
                if (projectile.mayInteract(serverWorld, blockPos)
                        && projectile.mayBreak(serverWorld, blockPos)
                        && projectile instanceof ThrownTrident
                        && projectile.getDeltaMovement().length() > 0.6) {
                    world.destroyBlock(blockPos, true);
                }
            }
        }
    }

    @Override
    public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
        if (isFreeHangingStalactite(state)) {
            float randomValue = random.nextFloat();
            if (!(randomValue > DRIP_PROBABILITY_PER_ANIMATE_TICK_IF_UNDER_LIQUID_SOURCE)) {
                getFluidAboveStalactite(level, pos, state).filter((fluidAbove) -> {
                    return randomValue < DRIP_PROBABILITY_PER_ANIMATE_TICK || canFillCauldron(fluidAbove.fluid);
                }).ifPresent((fluidAbove) -> {
                    spawnDripParticle(level, pos, state, fluidAbove.fluid, fluidAbove.pos);
                });
            }
        }
    }

    @Override
    protected void randomTick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
        maybeTransferFluid(state, level, pos, random.nextFloat());
        if (random.nextFloat() < 0.011377778F && isStalactiteStartPos(state, level, pos)) {
            tryGrow(state, level, pos, random);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (isPointingUp(state) && !this.canSurvive(state, world, pos)) {
            world.destroyBlock(pos, true);
        } else {
            spawnFallingBlock(state, world, pos);
        }
    }

    @VisibleForTesting
    public static void maybeTransferFluid(final BlockState state, final ServerLevel level, final BlockPos pos, final float randomValue) {
        if (!(randomValue > WATER_TRANSFER_PROBABILITY_PER_RANDOM_TICK) || !(randomValue > LAVA_TRANSFER_PROBABILITY_PER_RANDOM_TICK)) {
            if (isStalactiteStartPos(state, level, pos)) {
                Optional<FluidInfo> fluidInfo = getFluidAboveStalactite(level, pos, state);
                if (!fluidInfo.isEmpty()) {
                    Fluid fluid = fluidInfo.get().fluid;
                    float transferProbability;
                    if (fluid == Fluids.WATER) {
                        transferProbability = WATER_TRANSFER_PROBABILITY_PER_RANDOM_TICK;
                    } else {
                        if (fluid != Fluids.LAVA) {
                            return;
                        }
                        transferProbability = LAVA_TRANSFER_PROBABILITY_PER_RANDOM_TICK;
                    }

                    if (!(randomValue >= transferProbability)) {
                        BlockPos stalactiteTipPos = findTip(state, level, pos, 11, false);
                        if (stalactiteTipPos != null) {
                            if (fluidInfo.get().sourceState.is(Blocks.MUD) && fluid == Fluids.WATER) {
                                BlockState newState = Blocks.CLAY.defaultBlockState();
                                level.setBlockAndUpdate(fluidInfo.get().pos, newState);
                                Block.pushEntitiesUp(fluidInfo.get().sourceState, newState, level, fluidInfo.get().pos);
                                level.gameEvent(GameEvent.BLOCK_CHANGE, fluidInfo.get().pos, Context.of(newState));
                                level.levelEvent(1504, stalactiteTipPos, 0);
                            } else {
                                BlockPos cauldronPos = findFillableCauldronBelowStalactiteTip(level, stalactiteTipPos, fluid);
                                if (cauldronPos != null) {
                                    level.levelEvent(1504, stalactiteTipPos, 0);
                                    int fallDistance = stalactiteTipPos.getY() - cauldronPos.getY();
                                    int delay = 50 + fallDistance;
                                    BlockState cauldronState = level.getBlockState(cauldronPos);
                                    level.scheduleTick(cauldronPos, cauldronState.getBlock(), delay);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void spawnDripParticle(final Level level, final BlockPos stalactiteTipPos, final BlockState stalactiteTipState) {
        getFluidAboveStalactite(level, stalactiteTipPos, stalactiteTipState).ifPresent((fluidAbove) -> {
            spawnDripParticle(level, stalactiteTipPos, stalactiteTipState, fluidAbove.fluid, fluidAbove.pos);
        });
    }

    private static void spawnDripParticle(final Level level, final BlockPos stalactiteTipPos, final BlockState stalactiteTipState, final Fluid fluidAbove, final BlockPos posAbove) {
        Vec3 offset = stalactiteTipState.getOffset(stalactiteTipPos);
        double PIXEL_SIZE = 0.0625;
        double x = (double) stalactiteTipPos.getX() + 0.5 + offset.x;
        double y = (double) stalactiteTipPos.getY() + STALACTITE_DRIP_START_PIXEL - PIXEL_SIZE;
        double z = (double) stalactiteTipPos.getZ() + 0.5 + offset.z;
        ParticleOptions dripParticle = getDripParticle(level, fluidAbove, posAbove);
        level.addParticle(dripParticle, x, y, z, 0.0, 0.0, 0.0);
    }

    private static ParticleOptions getDripParticle(final Level level, final Fluid fluidAbove, final BlockPos posAbove) {
        if (fluidAbove.isSame(Fluids.EMPTY)) {
            return level.environmentAttributes().getValue(EnvironmentAttributes.DEFAULT_DRIPSTONE_PARTICLE, posAbove);
        } else {
            return fluidAbove.is(FluidTags.LAVA) ? ParticleTypes.DRIPPING_DRIPSTONE_LAVA : ParticleTypes.DRIPPING_DRIPSTONE_WATER;
        }
    }

    @Override
    protected boolean canGrow(final LevelReader level, final BlockPos pos) {
        FluidState fluidState = level.getBlockState(pos.above(2)).getFluidState();
        return super.canGrow(level, pos) && fluidState.is(Fluids.WATER) && fluidState.isSource();
    }

    @VisibleForTesting
    public static void tryGrow(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        BlockState blockState = world.getBlockState(pos.above(1));
        BlockState blockState2 = world.getBlockState(pos.above(2));
        if (canGrow(blockState, blockState2)) {
            BlockPos tipPos = findTip(state, world, pos, 7, false);
            if (tipPos != null) {
                BlockState tipState = world.getBlockState(tipPos);
                if (isFreeHangingStalactite(tipState) && canGrowInto(tipState, world, tipPos)) {
                    if (random.nextBoolean()) {
                        tryGrowStalactite(world, tipPos, Direction.DOWN);
                    } else {
                        tryGrowStalagmite(world, tipPos);
                    }
                }
            }
        }
    }

    private static boolean canGrow(BlockState dripstoneBlockState, BlockState waterState) {
        return dripstoneBlockState.is(ModBlocks.DARK_DRIPSTONE_BLOCK)
                && waterState.is(Blocks.WATER)
                && waterState.getFluidState().isSource();
    }

    private static boolean canGrowInto(BlockState state, ServerLevel world, BlockPos pos) {
        Direction direction = state.getValue(TIP_DIRECTION);
        BlockPos blockPos = pos.relative(direction);
        BlockState blockState = world.getBlockState(blockPos);
        if (!blockState.getFluidState().isEmpty()) {
            return false;
        }
        return blockState.isAir() || isTip(blockState, direction.getOpposite());
    }

    private static void tryGrowStalagmite(ServerLevel world, BlockPos pos) {
        BlockPos.MutableBlockPos mutable = pos.mutable();
        for (int i = 0; i < 10; ++i) {
            mutable.move(Direction.DOWN);
            BlockState blockState = world.getBlockState(mutable);
            if (!blockState.getFluidState().isEmpty()) {
                return;
            }
            if (isTip(blockState, Direction.UP) && canGrowInto(blockState, world, mutable)) {
                tryGrowStalactite(world, mutable, Direction.UP);
                return;
            }
            if (canPlaceAtWithDirection(world, mutable, Direction.UP) && !world.isWaterAt(mutable.below())) {
                tryGrowStalactite(world, mutable.below(), Direction.UP);
                return;
            }
            if (!canDripThrough(world, mutable, blockState)) {
                return;
            }
        }
    }

    private static void tryGrowStalactite(ServerLevel world, BlockPos pos, Direction direction) {
        BlockPos blockPos = pos.relative(direction);
        BlockState blockState = world.getBlockState(blockPos);
        if (isTip(blockState, direction.getOpposite())) {
            growMerged(blockState, world, blockPos);
        } else if (blockState.isAir() || blockState.is(Blocks.WATER)) {
            place(world, blockPos, direction, SpeleothemThickness.TIP);
        }
    }

    private static void growMerged(BlockState state, LevelAccessor world, BlockPos pos) {
        BlockPos upPos;
        BlockPos downPos;
        if (state.getValue(TIP_DIRECTION) == Direction.UP) {
            downPos = pos;
            upPos = pos.above();
        } else {
            upPos = pos;
            downPos = pos.below();
        }
        place(world, upPos, Direction.DOWN, SpeleothemThickness.TIP_MERGE);
        place(world, downPos, Direction.UP, SpeleothemThickness.TIP_MERGE);
    }

    private static void place(LevelAccessor world, BlockPos pos, Direction direction, SpeleothemThickness thickness) {
        BlockState blockState = ModBlocks.POINTED_DARK_DRIPSTONE.defaultBlockState()
                .setValue(TIP_DIRECTION, direction)
                .setValue(THICKNESS, thickness)
                .setValue(WATERLOGGED, world.getFluidState(pos).getType() == Fluids.WATER);
        world.setBlock(pos, blockState, 3);
    }

    private static void spawnFallingBlock(BlockState state, ServerLevel world, BlockPos pos) {
        BlockPos.MutableBlockPos mutable = pos.mutable();
        for (BlockState blockState = state; isPointingDown(blockState); blockState = world.getBlockState(mutable)) {
            FallingBlockEntity fallingBlockEntity = FallingBlockEntity.fall(world, mutable, blockState);
            if (isTip(blockState, true)) {
                int i = Math.max(1 + pos.getY() - mutable.getY(), 6);
                float f = 1.0F * (float) i;
                fallingBlockEntity.setHurtsEntities(f, 40);
                break;
            }
            mutable.move(Direction.DOWN);
        }
    }

    @Nullable
    private static BlockPos findFillableCauldronBelowStalactiteTip(final Level level, final BlockPos stalactiteTipPos, final Fluid fluid) {
        Predicate<BlockState> cauldronPredicate = (state) -> {
            if (!(state.getBlock() instanceof AbstractCauldronBlockAccessor accessor)) return false;
            return accessor.earthAndWater$canReceiveStalactiteDrip(fluid);
        };
        BiPredicate<BlockPos, BlockState> pathPredicate = (pathPos, state) -> canDripThrough(level, pathPos, state);
        return findBlockVertical(level, stalactiteTipPos, Direction.DOWN.getAxisDirection(), pathPredicate, cauldronPredicate, 11).orElse(null);
    }

    @Nullable
    public static BlockPos findStalactiteTipAboveCauldron(final Level level, final BlockPos cauldronPos) {
        BiPredicate<BlockPos, BlockState> pathPredicate = (pos, state) -> canDripThrough(level, pos, state);
        return findBlockVertical(level, cauldronPos, Direction.UP.getAxisDirection(), pathPredicate, SpeleothemBlock::isFreeHangingStalactite, 11).orElse(null);
    }

    public static Fluid getCauldronFillFluidType(final ServerLevel level, final BlockPos stalactitePos) {
        return getFluidAboveStalactite(level, stalactitePos, level.getBlockState(stalactitePos))
                .map((fluidSource) -> fluidSource.fluid)
                .filter(PointedDarkDripstoneBlock::canFillCauldron)
                .orElse(Fluids.EMPTY);
    }

    private static Optional<FluidInfo> getFluidAboveStalactite(final Level level, final BlockPos stalactitePos, final BlockState stalactiteState) {
        return !isStalactite(stalactiteState) ? Optional.empty()
                : findRootBlock(level, stalactitePos, stalactiteState, 11).map((rootPos) -> {
            BlockPos abovePos = rootPos.above();
            BlockState aboveState = level.getBlockState(abovePos);
            Fluid fluid;
            if (aboveState.is(Blocks.MUD) && !(Boolean) level.environmentAttributes().getValue(EnvironmentAttributes.WATER_EVAPORATES, abovePos)) {
                fluid = Fluids.WATER;
            } else {
                fluid = level.getFluidState(abovePos).getType();
            }
            return new FluidInfo(abovePos, fluid, aboveState);
        });
    }

    private static Optional<BlockPos> findRootBlock(final Level level, final BlockPos pos, final BlockState darkDripStoneState, final int maxSearchLength) {
        Direction tipDirection = darkDripStoneState.getValue(TIP_DIRECTION);
        BiPredicate<BlockPos, BlockState> pathPredicate = (pathPos, state) ->
                state.is(darkDripStoneState.getBlock()) && state.getValue(TIP_DIRECTION) == tipDirection;
        return findBlockVertical(level, pos, tipDirection.getOpposite().getAxisDirection(), pathPredicate, (state) ->
                !state.is(darkDripStoneState.getBlock()), maxSearchLength);
    }

    private static boolean canFillCauldron(final Fluid fluidAbove) {
        return fluidAbove == Fluids.LAVA || fluidAbove == Fluids.WATER;
    }

    @Override
    protected boolean blocksStalagmiteScan(final LevelReader level, final BlockPos pos, final BlockState state) {
        return !canDripThrough(level, pos, state);
    }

    private static boolean canDripThrough(final BlockGetter level, final BlockPos pos, final BlockState state) {
        if (state.isAir()) {
            return true;
        } else if (state.isSolidRender()) {
            return false;
        } else if (!state.getFluidState().isEmpty()) {
            return false;
        } else {
            VoxelShape collisionShape = state.getCollisionShape(level, pos);
            return !Shapes.joinIsNotEmpty(REQUIRED_SPACE_TO_DRIP_THROUGH_NON_SOLID_BLOCK, collisionShape, BooleanOp.AND);
        }
    }

    private static boolean isTip(BlockState state, boolean allowMerged) {
        if (!state.is(ModBlocks.POINTED_DARK_DRIPSTONE)) {
            return false;
        }
        SpeleothemThickness thickness = state.getValue(THICKNESS);
        return thickness == SpeleothemThickness.TIP || (allowMerged && thickness == SpeleothemThickness.TIP_MERGE);
    }

    private static boolean isTip(BlockState state, Direction direction) {
        return isTip(state, false) && state.getValue(TIP_DIRECTION) == direction;
    }

    private static boolean isPointingDown(BlockState state) {
        return isPointedDarkDripstoneFacingDirection(state, Direction.DOWN);
    }

    private static boolean isPointingUp(BlockState state) {
        return isPointedDarkDripstoneFacingDirection(state, Direction.UP);
    }

    private static boolean isPointedDarkDripstoneFacingDirection(BlockState state, Direction direction) {
        return state.is(ModBlocks.POINTED_DARK_DRIPSTONE) && state.getValue(TIP_DIRECTION) == direction;
    }

    @Nullable
    private static Direction getDirectionToPlaceAt(LevelReader world, BlockPos pos, Direction direction) {
        if (canPlaceAtWithDirection(world, pos, direction)) {
            return direction;
        } else if (canPlaceAtWithDirection(world, pos, direction.getOpposite())) {
            return direction.getOpposite();
        }
        return null;
    }

    private static boolean canPlaceAtWithDirection(LevelReader world, BlockPos pos, Direction direction) {
        BlockPos blockPos = pos.relative(direction.getOpposite());
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.isFaceSturdy(world, blockPos, direction) || isPointedDarkDripstoneFacingDirection(blockState, direction);
    }

    private static SpeleothemThickness getThickness(LevelReader world, BlockPos pos, Direction direction, boolean tryMerge) {
        Direction opposite = direction.getOpposite();
        BlockState blockState = world.getBlockState(pos.relative(direction));
        if (isPointedDarkDripstoneFacingDirection(blockState, opposite)) {
            return !tryMerge && blockState.getValue(THICKNESS) != SpeleothemThickness.TIP_MERGE
                    ? SpeleothemThickness.TIP : SpeleothemThickness.TIP_MERGE;
        } else if (!isPointedDarkDripstoneFacingDirection(blockState, direction)) {
            return SpeleothemThickness.TIP;
        } else {
            SpeleothemThickness thickness = blockState.getValue(THICKNESS);
            if (thickness != SpeleothemThickness.TIP && thickness != SpeleothemThickness.TIP_MERGE) {
                BlockState blockState2 = world.getBlockState(pos.relative(opposite));
                return !isPointedDarkDripstoneFacingDirection(blockState2, direction)
                        ? SpeleothemThickness.BASE : SpeleothemThickness.MIDDLE;
            } else {
                return SpeleothemThickness.FRUSTUM;
            }
        }
    }

    static {
        TIP_MERGE_SHAPE = Block.column(6.0, 0.0, 16.0);
        UP_TIP_SHAPE = Block.column(6.0, 0.0, 11.0);
        DOWN_TIP_SHAPE = Block.column(6.0, 5.0, 16.0);
        BASE_SHAPE = Block.column(8.0, 0.0, 16.0);
        FRUSTUM_SHAPE = Block.column(10.0, 0.0, 16.0);
        MIDDLE_SHAPE = Block.column(12.0, 0.0, 16.0);
        MAX_HORIZONTAL_MODEL_OFFSET = (float) MIDDLE_SHAPE.min(Axis.X);
        STALACTITE_DRIP_START_PIXEL = DOWN_TIP_SHAPE.min(Axis.Y);
        REQUIRED_SPACE_TO_DRIP_THROUGH_NON_SOLID_BLOCK = Block.column(4.0, 0.0, 16.0);
    }

    private static record FluidInfo(BlockPos pos, Fluid fluid, BlockState sourceState) {
        private FluidInfo(BlockPos pos, Fluid fluid, BlockState sourceState) {
            this.pos = pos;
            this.fluid = fluid;
            this.sourceState = sourceState;
        }

        public BlockPos pos() { return this.pos; }
        public Fluid fluid() { return this.fluid; }
        public BlockState sourceState() { return this.sourceState; }
    }
}