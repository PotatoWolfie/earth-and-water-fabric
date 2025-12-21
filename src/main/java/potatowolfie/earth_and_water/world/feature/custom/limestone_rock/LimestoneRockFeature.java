package potatowolfie.earth_and_water.world.feature.custom.limestone_rock;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import potatowolfie.earth_and_water.block.ModBlocks;

public class LimestoneRockFeature extends Feature<LimestoneRockFeatureConfig> {

    public LimestoneRockFeature(Codec<LimestoneRockFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<LimestoneRockFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos pos = context.getOrigin();
        Random random = context.getRandom();

        BlockPos oceanFloor = findOceanFloorPosition(world, pos);
        if (oceanFloor == null) {
            return false;
        }

        int bigHeight = 3 + random.nextInt(3);
        int bigWidth = 3 + random.nextInt(2);
        int smallHeight = 2 + random.nextInt(2);
        int smallWidth = 2 + random.nextInt(2);

        int corner = random.nextInt(4);

        generateSupport(world, oceanFloor, bigWidth, random);
        generateBigRock(world, oceanFloor, bigWidth, bigHeight, random);
        generateSmallRock(world, oceanFloor, bigWidth, smallWidth, smallHeight, corner, random);

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

                if (!isFlatArea(world, mutablePos)) {
                    return null;
                }

                return mutablePos;
            }
            mutablePos.move(0, -1, 0);
        }

        return null;
    }

    private boolean isFlatArea(StructureWorldAccess world, BlockPos center) {
        int checkRadius = 3;
        int baseY = center.getY();

        for (int x = -checkRadius; x <= checkRadius; x++) {
            for (int z = -checkRadius; z <= checkRadius; z++) {
                BlockPos checkPos = center.add(x, 0, z);
                BlockPos below = checkPos.down();
                BlockState belowState = world.getBlockState(below);

                if (belowState.isOf(Blocks.KELP) || belowState.isOf(Blocks.KELP_PLANT) ||
                        belowState.isOf(Blocks.SEAGRASS) || belowState.isOf(Blocks.TALL_SEAGRASS)) {
                    continue;
                }

                if (belowState.isOf(Blocks.WATER) || belowState.isAir()) {
                    return false;
                }

                int yDiff = Math.abs(checkPos.getY() - baseY);
                if (yDiff > 1) {
                    return false;
                }
            }
        }

        return true;
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

        if (state.isOf(Blocks.WATER) ||
                state.isOf(Blocks.KELP) ||
                state.isOf(Blocks.KELP_PLANT) ||
                state.isOf(Blocks.SEAGRASS) ||
                state.isOf(Blocks.TALL_SEAGRASS)) {
            return true;
        }

        return false;
    }

    private void generateBigRock(StructureWorldAccess world, BlockPos center, int width, int height, Random random) {
        int halfWidth = (width - 1) / 2;

        boolean skipNE = random.nextFloat() < 0.5f;
        boolean skipSE = random.nextFloat() < 0.5f;
        boolean skipSW = random.nextFloat() < 0.5f;
        boolean skipNW = random.nextFloat() < 0.5f;

        for (int y = 0; y < height; y++) {
            int currentSize = halfWidth;
            boolean isTopLayer = (y == height - 1);

            for (int x = -currentSize; x <= currentSize; x++) {
                for (int z = -currentSize; z <= currentSize; z++) {
                    boolean skipColumn = false;

                    if (x == currentSize && z == -currentSize && skipNE) {
                        skipColumn = true;
                    }
                    if (x == currentSize && z == currentSize && skipSE) {
                        skipColumn = true;
                    }
                    if (x == -currentSize && z == currentSize && skipSW) {
                        skipColumn = true;
                    }
                    if (x == -currentSize && z == -currentSize && skipNW) {
                        skipColumn = true;
                    }

                    boolean placeBlock = true;
                    if (isTopLayer) {
                        float distFromCenter = (float) Math.sqrt(x * x + z * z);
                        float randomFactor = 0.7f + random.nextFloat() * 0.3f;
                        float maxDist = halfWidth * randomFactor;
                        if (distFromCenter > maxDist) {
                            placeBlock = false;
                        }
                    }

                    if (!skipColumn && placeBlock) {
                        BlockPos pos = center.add(x, y, z);
                        if (canReplaceBlock(world, pos)) {
                            world.setBlockState(pos, ModBlocks.LIMESTONE.getDefaultState(), 2);
                        }
                    }
                }
            }
        }

        if (skipNE && random.nextFloat() < 0.35f) {
            int extraHeight = 1 + random.nextInt(2);
            for (int y = 0; y < extraHeight; y++) {
                BlockPos pos = center.add(halfWidth, y, -halfWidth);
                if (canReplaceBlock(world, pos)) {
                    world.setBlockState(pos, ModBlocks.LIMESTONE.getDefaultState(), 2);
                }
            }
        }

        if (skipSE && random.nextFloat() < 0.35f) {
            int extraHeight = 1 + random.nextInt(2);
            for (int y = 0; y < extraHeight; y++) {
                BlockPos pos = center.add(halfWidth, y, halfWidth);
                if (canReplaceBlock(world, pos)) {
                    world.setBlockState(pos, ModBlocks.LIMESTONE.getDefaultState(), 2);
                }
            }
        }

        if (skipSW && random.nextFloat() < 0.35f) {
            int extraHeight = 1 + random.nextInt(2);
            for (int y = 0; y < extraHeight; y++) {
                BlockPos pos = center.add(-halfWidth, y, halfWidth);
                if (canReplaceBlock(world, pos)) {
                    world.setBlockState(pos, ModBlocks.LIMESTONE.getDefaultState(), 2);
                }
            }
        }

        if (skipNW && random.nextFloat() < 0.35f) {
            int extraHeight = 1 + random.nextInt(2);
            for (int y = 0; y < extraHeight; y++) {
                BlockPos pos = center.add(-halfWidth, y, -halfWidth);
                if (canReplaceBlock(world, pos)) {
                    world.setBlockState(pos, ModBlocks.LIMESTONE.getDefaultState(), 2);
                }
            }
        }
    }

    private void generateSupport(StructureWorldAccess world, BlockPos center, int width, Random random) {
        int halfWidth = (width - 1) / 2;

        for (int x = -halfWidth; x <= halfWidth; x++) {
            for (int z = -halfWidth; z <= halfWidth; z++) {
                BlockPos checkPos = center.add(x, -1, z);
                BlockState belowState = world.getBlockState(checkPos);

                if (belowState.isOf(Blocks.WATER) || belowState.isAir() ||
                        belowState.isOf(Blocks.KELP) || belowState.isOf(Blocks.KELP_PLANT) ||
                        belowState.isOf(Blocks.SEAGRASS) || belowState.isOf(Blocks.TALL_SEAGRASS)) {
                    for (int y = -1; y >= -10; y--) {
                        BlockPos supportPos = center.add(x, y, z);
                        BlockState state = world.getBlockState(supportPos);

                        if (!state.isOf(Blocks.WATER) && !state.isAir() &&
                                !state.isOf(Blocks.KELP) && !state.isOf(Blocks.KELP_PLANT) &&
                                !state.isOf(Blocks.SEAGRASS) && !state.isOf(Blocks.TALL_SEAGRASS)) {
                            break;
                        }

                        world.setBlockState(supportPos, ModBlocks.LIMESTONE.getDefaultState(), 2);

                        BlockState checkBelow = world.getBlockState(supportPos.down());
                        if (!checkBelow.isOf(Blocks.WATER) && !checkBelow.isAir() &&
                                !checkBelow.isOf(Blocks.KELP) && !checkBelow.isOf(Blocks.KELP_PLANT) &&
                                !checkBelow.isOf(Blocks.SEAGRASS) && !checkBelow.isOf(Blocks.TALL_SEAGRASS)) {
                            break;
                        }
                    }
                }
            }
        }
    }

    private void generateSmallRock(StructureWorldAccess world, BlockPos bigRockCenter, int bigWidth,
                                   int smallWidth, int smallHeight, int corner, Random random) {
        int bigHalf = (bigWidth - 1) / 2;
        int smallHalf = smallWidth / 2;

        int xOffset = 0;
        int zOffset = 0;

        switch (corner) {
            case 0:
                xOffset = bigHalf + smallHalf;
                zOffset = -(bigHalf + smallHalf);
                break;
            case 1:
                xOffset = bigHalf + smallHalf;
                zOffset = bigHalf + smallHalf;
                break;
            case 2:
                xOffset = -(bigHalf + smallHalf);
                zOffset = bigHalf + smallHalf;
                break;
            case 3:
                xOffset = -(bigHalf + smallHalf);
                zOffset = -(bigHalf + smallHalf);
                break;
        }

        BlockPos smallRockCenter = bigRockCenter.add(xOffset, 0, zOffset);

        generateSupport(world, smallRockCenter, smallWidth, random);

        boolean skipNE = random.nextFloat() < 0.15f;
        boolean skipSE = random.nextFloat() < 0.15f;
        boolean skipSW = random.nextFloat() < 0.15f;
        boolean skipNW = random.nextFloat() < 0.15f;

        for (int y = 0; y < smallHeight; y++) {
            int currentSize = smallHalf;
            boolean isTopLayer = (y == smallHeight - 1);

            for (int x = -currentSize; x <= currentSize; x++) {
                for (int z = -currentSize; z <= currentSize; z++) {
                    BlockPos pos = smallRockCenter.add(x, y, z);

                    boolean isConnectingCorner = false;
                    if (isTopLayer) {
                        switch (corner) {
                            case 0:
                                isConnectingCorner = (x <= 0 && z >= 0);
                                break;
                            case 1:
                                isConnectingCorner = (x <= 0 && z <= 0);
                                break;
                            case 2:
                                isConnectingCorner = (x >= 0 && z <= 0);
                                break;
                            case 3:
                                isConnectingCorner = (x >= 0 && z >= 0);
                                break;
                        }
                    }

                    boolean skipColumn = false;

                    if (x == currentSize && z == -currentSize && skipNE) {
                        skipColumn = true;
                    }
                    if (x == currentSize && z == currentSize && skipSE) {
                        skipColumn = true;
                    }
                    if (x == -currentSize && z == currentSize && skipSW) {
                        skipColumn = true;
                    }
                    if (x == -currentSize && z == -currentSize && skipNW) {
                        skipColumn = true;
                    }

                    boolean placeBlock = true;
                    if (isTopLayer && !isConnectingCorner) {
                        float distFromCenter = (float) Math.sqrt(x * x + z * z);
                        float randomFactor = 0.7f + random.nextFloat() * 0.5f;
                        float maxDist = smallHalf * randomFactor;
                        if (distFromCenter > maxDist) {
                            placeBlock = false;
                        }
                    }

                    if (!skipColumn && placeBlock) {
                        if (canReplaceBlock(world, pos)) {
                            world.setBlockState(pos, ModBlocks.LIMESTONE.getDefaultState(), 2);
                        }
                    }
                }
            }
        }
    }
}