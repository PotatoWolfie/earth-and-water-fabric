package potatowolfie.earth_and_water.world.feature.custom.limestone_rock;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import potatowolfie.earth_and_water.block.ModBlocks;

public record LimestoneRockFeature() implements Feature {
    public static final MapCodec<LimestoneRockFeature> CODEC = MapCodec.unit(LimestoneRockFeature::new);

    public MapCodec<LimestoneRockFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel world, ChunkGenerator chunkGenerator, RandomSource random, BlockPos pos) {
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

                if (!isFlatArea(world, mutablePos)) {
                    return null;
                }

                return mutablePos;
            }
            mutablePos.move(0, -1, 0);
        }

        return null;
    }

    private boolean isFlatArea(WorldGenLevel world, BlockPos center) {
        int checkRadius = 3;
        int baseY = center.getY();

        for (int x = -checkRadius; x <= checkRadius; x++) {
            for (int z = -checkRadius; z <= checkRadius; z++) {
                BlockPos checkPos = center.offset(x, 0, z);
                BlockPos below = checkPos.below();
                BlockState belowState = world.getBlockState(below);

                if (belowState.is(Blocks.KELP) || belowState.is(Blocks.KELP_PLANT) ||
                        belowState.is(Blocks.SEAGRASS) || belowState.is(Blocks.TALL_SEAGRASS)) {
                    continue;
                }

                if (belowState.is(Blocks.WATER) || belowState.isAir()) {
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

        if (state.is(Blocks.WATER) ||
                state.is(Blocks.KELP) ||
                state.is(Blocks.KELP_PLANT) ||
                state.is(Blocks.SEAGRASS) ||
                state.is(Blocks.TALL_SEAGRASS)) {
            return true;
        }

        return false;
    }

    private void generateBigRock(WorldGenLevel world, BlockPos center, int width, int height, RandomSource random) {
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
                        BlockPos pos = center.offset(x, y, z);
                        if (canReplaceBlock(world, pos)) {
                            world.setBlock(pos, ModBlocks.LIMESTONE.defaultBlockState(), 2);
                        }
                    }
                }
            }
        }

        if (skipNE && random.nextFloat() < 0.35f) {
            int extraHeight = 1 + random.nextInt(2);
            for (int y = 0; y < extraHeight; y++) {
                BlockPos pos = center.offset(halfWidth, y, -halfWidth);
                if (canReplaceBlock(world, pos)) {
                    world.setBlock(pos, ModBlocks.LIMESTONE.defaultBlockState(), 2);
                }
            }
        }

        if (skipSE && random.nextFloat() < 0.35f) {
            int extraHeight = 1 + random.nextInt(2);
            for (int y = 0; y < extraHeight; y++) {
                BlockPos pos = center.offset(halfWidth, y, halfWidth);
                if (canReplaceBlock(world, pos)) {
                    world.setBlock(pos, ModBlocks.LIMESTONE.defaultBlockState(), 2);
                }
            }
        }

        if (skipSW && random.nextFloat() < 0.35f) {
            int extraHeight = 1 + random.nextInt(2);
            for (int y = 0; y < extraHeight; y++) {
                BlockPos pos = center.offset(-halfWidth, y, halfWidth);
                if (canReplaceBlock(world, pos)) {
                    world.setBlock(pos, ModBlocks.LIMESTONE.defaultBlockState(), 2);
                }
            }
        }

        if (skipNW && random.nextFloat() < 0.35f) {
            int extraHeight = 1 + random.nextInt(2);
            for (int y = 0; y < extraHeight; y++) {
                BlockPos pos = center.offset(-halfWidth, y, -halfWidth);
                if (canReplaceBlock(world, pos)) {
                    world.setBlock(pos, ModBlocks.LIMESTONE.defaultBlockState(), 2);
                }
            }
        }
    }

    private void generateSupport(WorldGenLevel world, BlockPos center, int width, RandomSource random) {
        int halfWidth = (width - 1) / 2;

        for (int x = -halfWidth; x <= halfWidth; x++) {
            for (int z = -halfWidth; z <= halfWidth; z++) {
                BlockPos checkPos = center.offset(x, -1, z);
                BlockState belowState = world.getBlockState(checkPos);

                if (belowState.is(Blocks.WATER) || belowState.isAir() ||
                        belowState.is(Blocks.KELP) || belowState.is(Blocks.KELP_PLANT) ||
                        belowState.is(Blocks.SEAGRASS) || belowState.is(Blocks.TALL_SEAGRASS)) {
                    for (int y = -1; y >= -10; y--) {
                        BlockPos supportPos = center.offset(x, y, z);
                        BlockState state = world.getBlockState(supportPos);

                        if (!state.is(Blocks.WATER) && !state.isAir() &&
                                !state.is(Blocks.KELP) && !state.is(Blocks.KELP_PLANT) &&
                                !state.is(Blocks.SEAGRASS) && !state.is(Blocks.TALL_SEAGRASS)) {
                            break;
                        }

                        world.setBlock(supportPos, ModBlocks.LIMESTONE.defaultBlockState(), 2);

                        BlockState checkBelow = world.getBlockState(supportPos.below());
                        if (!checkBelow.is(Blocks.WATER) && !checkBelow.isAir() &&
                                !checkBelow.is(Blocks.KELP) && !checkBelow.is(Blocks.KELP_PLANT) &&
                                !checkBelow.is(Blocks.SEAGRASS) && !checkBelow.is(Blocks.TALL_SEAGRASS)) {
                            break;
                        }
                    }
                }
            }
        }
    }

    private void generateSmallRock(WorldGenLevel world, BlockPos bigRockCenter, int bigWidth,
                                   int smallWidth, int smallHeight, int corner, RandomSource random) {
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

        BlockPos smallRockCenter = bigRockCenter.offset(xOffset, 0, zOffset);

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
                    BlockPos pos = smallRockCenter.offset(x, y, z);

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
                            world.setBlock(pos, ModBlocks.LIMESTONE.defaultBlockState(), 2);
                        }
                    }
                }
            }
        }
    }
}