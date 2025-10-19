package potatowolfie.earth_and_water.world.feature.custom;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import potatowolfie.earth_and_water.block.ModBlocks;

public class OxygenFeature extends Feature<OxygenFeatureConfig> {

    public OxygenFeature(Codec<OxygenFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<OxygenFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos pos = context.getOrigin();
        Random random = context.getRandom();

        BlockPos oceanFloor = findOceanFloorPosition(world, pos);
        if (oceanFloor == null) {
            return false;
        }

        generateCrossPattern(world, oceanFloor);

        return true;
    }

    private BlockPos findOceanFloorPosition(StructureWorldAccess world, BlockPos startPos) {
        BlockPos.Mutable mutablePos = startPos.mutableCopy();

        while (mutablePos.getY() < 63 && !world.getBlockState(mutablePos).isOf(Blocks.WATER)) {
            mutablePos.move(0, 1, 0);
        }

        boolean foundWater = false;
        for (int i = 0; i < 64; i++) {
            if (world.getBlockState(mutablePos).isOf(Blocks.WATER)) {
                foundWater = true;
                break;
            }
            mutablePos.move(0, -1, 0);
        }

        if (!foundWater) {
            return null;
        }

        while (mutablePos.getY() > world.getBottomY()) {
            BlockPos below = mutablePos.down();
            BlockState belowState = world.getBlockState(below);

            if (!belowState.isOf(Blocks.WATER) && !belowState.isAir() &&
                    world.getBlockState(mutablePos).isOf(Blocks.WATER)) {

                if (hasKelpOrPlantsAbove(world, mutablePos)) {
                    return null;
                }

                return below;
            }
            mutablePos.move(0, -1, 0);
        }

        return null;
    }

    private boolean canReplaceBlock(StructureWorldAccess world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);

        if (state.isOf(Blocks.SAND) ||
                state.isOf(Blocks.GRAVEL) ||
                state.isOf(Blocks.CLAY) ||
                state.isOf(Blocks.DIRT) ||
                state.isOf(Blocks.COARSE_DIRT)) {
            return true;
        }

        if (state.isOf(Blocks.STONE) ||
                state.isOf(Blocks.DEEPSLATE) ||
                state.isOf(Blocks.GRANITE) ||
                state.isOf(Blocks.ANDESITE) ||
                state.isOf(Blocks.DIORITE)) {
            return true;
        }

        return false;
    }

    private boolean hasKelpOrPlantsAbove(StructureWorldAccess world, BlockPos oceanFloorWater) {
        for (int i = 0; i < 10; i++) {
            BlockPos checkPos = oceanFloorWater.up(i);
            BlockState state = world.getBlockState(checkPos);

            if (state.isOf(Blocks.KELP) ||
                    state.isOf(Blocks.KELP_PLANT) ||
                    state.isOf(Blocks.SEAGRASS) ||
                    state.isOf(Blocks.TALL_SEAGRASS)) {
                return true;
            }

            if (state.isAir()) {
                break;
            }
        }
        return false;
    }

    private void generateCrossPattern(StructureWorldAccess world, BlockPos center) {
        if (canReplaceBlock(world, center)) {
            world.setBlockState(center, ModBlocks.OXYGEN_BLOCK.getDefaultState(), 3);
        }

        BlockPos[] positions = {
                center.north(),
                center.south(),
                center.east(),
                center.west()
        };

        for (BlockPos pos : positions) {
            if (canReplaceBlock(world, pos)) {
                world.setBlockState(pos, ModBlocks.OXYGEN_BLOCK.getDefaultState(), 3);
            }
        }
    }
}