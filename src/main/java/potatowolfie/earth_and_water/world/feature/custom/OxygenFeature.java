package potatowolfie.earth_and_water.world.feature.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import potatowolfie.earth_and_water.block.ModBlocks;

public record OxygenFeature() implements Feature {
    public static final MapCodec<OxygenFeature> CODEC = MapCodec.unit(OxygenFeature::new);

    public MapCodec<OxygenFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel world, ChunkGenerator chunkGenerator, RandomSource random, BlockPos pos) {
        BlockPos oceanFloor = findOceanFloorPosition(world, pos);
        if (oceanFloor == null) {
            return false;
        }

        generateCrossPattern(world, oceanFloor);
        return true;
    }

    private BlockPos findOceanFloorPosition(WorldGenLevel world, BlockPos startPos) {
        BlockPos.MutableBlockPos mutablePos = startPos.mutable();

        while (mutablePos.getY() < 63 && !world.getBlockState(mutablePos).is(Blocks.WATER)) {
            mutablePos.move(0, 1, 0);
        }

        boolean foundWater = false;
        for (int i = 0; i < 64; i++) {
            if (world.getBlockState(mutablePos).is(Blocks.WATER)) {
                foundWater = true;
                break;
            }
            mutablePos.move(0, -1, 0);
        }

        if (!foundWater) {
            return null;
        }

        while (mutablePos.getY() > world.getMinY()) {
            BlockPos below = mutablePos.below();
            BlockState belowState = world.getBlockState(below);

            if (!belowState.is(Blocks.WATER) && !belowState.isAir() &&
                    world.getBlockState(mutablePos).is(Blocks.WATER)) {

                if (hasKelpOrPlantsAbove(world, mutablePos)) {
                    return null;
                }

                return below;
            }
            mutablePos.move(0, -1, 0);
        }

        return null;
    }

    private boolean canReplaceBlock(WorldGenLevel world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);

        if (state.is(Blocks.SAND) ||
                state.is(Blocks.GRAVEL) ||
                state.is(Blocks.CLAY) ||
                state.is(Blocks.DIRT) ||
                state.is(Blocks.COARSE_DIRT)) {
            return true;
        }

        if (state.is(Blocks.STONE) ||
                state.is(Blocks.DEEPSLATE) ||
                state.is(Blocks.GRANITE) ||
                state.is(Blocks.ANDESITE) ||
                state.is(Blocks.DIORITE)) {
            return true;
        }

        return false;
    }

    private boolean hasKelpOrPlantsAbove(WorldGenLevel world, BlockPos oceanFloorWater) {
        for (int i = 0; i < 10; i++) {
            BlockPos checkPos = oceanFloorWater.above(i);
            BlockState state = world.getBlockState(checkPos);

            if (state.is(Blocks.KELP) ||
                    state.is(Blocks.KELP_PLANT) ||
                    state.is(Blocks.SEAGRASS) ||
                    state.is(Blocks.TALL_SEAGRASS)) {
                return true;
            }

            if (state.isAir()) {
                break;
            }
        }
        return false;
    }

    private void generateCrossPattern(WorldGenLevel world, BlockPos center) {
        if (canReplaceBlock(world, center)) {
            world.setBlock(center, ModBlocks.OXYGEN_BLOCK.defaultBlockState(), 3);
        }

        BlockPos[] positions = {
                center.north(),
                center.south(),
                center.east(),
                center.west()
        };

        for (BlockPos pos : positions) {
            if (canReplaceBlock(world, pos)) {
                world.setBlock(pos, ModBlocks.OXYGEN_BLOCK.defaultBlockState(), 3);
            }
        }
    }
}