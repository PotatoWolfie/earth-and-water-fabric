package potatowolfie.earth_and_water.structure.ancient_ruins;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.*;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import potatowolfie.earth_and_water.block.custom.ReinforcedSpawnerBlock;
import potatowolfie.earth_and_water.block.entity.custom.ReinforcedSpawnerBlockEntity;
import potatowolfie.earth_and_water.entity.ModEntities;
import potatowolfie.earth_and_water.structure.ModStructurePieceTypes;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AncientRuinsGenerator {
    private static final Identifier INNER_WALLS = Identifier.of("earth-and-water", "ancient_ruins/inner_walls");

    private static final Identifier[] OUTER_WALL_PIECES = new Identifier[] {
            Identifier.of("earth-and-water", "ancient_ruins/walls_1"),
            Identifier.of("earth-and-water", "ancient_ruins/walls_2"),
            Identifier.of("earth-and-water", "ancient_ruins/walls_3")
    };

    private static final Identifier OUTER_WALL_ENTRANCE = Identifier.of("earth-and-water", "ancient_ruins/walls_entrance");

    private static final Identifier[] OUTER_WALL_CORNERS = new Identifier[] {
            Identifier.of("earth-and-water", "ancient_ruins/walls_corner_1"),
            Identifier.of("earth-and-water", "ancient_ruins/walls_corner_2")
    };

    private static final Identifier[] CENTER_PIECES = new Identifier[] {
            Identifier.of("earth-and-water", "ancient_ruins/center_1"),
            Identifier.of("earth-and-water", "ancient_ruins/center_2")
    };

    private static final Identifier[] STATUES = new Identifier[] {
            Identifier.of("earth-and-water", "ancient_ruins/statue_1"),
            Identifier.of("earth-and-water", "ancient_ruins/statue_2"),
            Identifier.of("earth-and-water", "ancient_ruins/statue_3")
    };

    private static final Identifier[] SMALL_RUINS = new Identifier[] {
            Identifier.of("earth-and-water", "ancient_ruins/small_ruins_1"),
            Identifier.of("earth-and-water", "ancient_ruins/small_ruins/small_ruins_2"),
            Identifier.of("earth-and-water", "ancient_ruins/small_ruins_3"),
            Identifier.of("earth-and-water", "ancient_ruins/small_ruins_4"),
            Identifier.of("earth-and-water", "ancient_ruins/small_ruins_5")
    };

    private static final Identifier[] MEDIUM_RUINS = new Identifier[] {
            Identifier.of("earth-and-water", "ancient_ruins/medium_ruins_1"),
            Identifier.of("earth-and-water", "ancient_ruins/medium_ruins_2"),
            Identifier.of("earth-and-water", "ancient_ruins/medium_ruins_3")
    };

    private static final Identifier[] LARGE_RUINS = new Identifier[] {
            Identifier.of("earth-and-water", "ancient_ruins/large_ruins_1"),
            Identifier.of("earth-and-water", "ancient_ruins/large_ruins_2"),
            Identifier.of("earth-and-water", "ancient_ruins/large_ruins_3")
    };

    private static final int WALL_LENGTH = 45;
    private static final int WALL_WIDTH = 3;
    private static final int CORNER_SIZE = 24;
    private static final int TOTAL_PERIMETER = 93;
    private static final int INTERIOR_SIZE = 88;
    private static final int CENTER_EXCLUSION_SIZE = 40;
    private static final int SMALL_RUIN_MAX_SIZE = 9;
    private static final int MEDIUM_RUIN_MAX_SIZE = 15;
    private static final int LARGE_RUIN_MAX_SIZE = 21;

    private static final int SMALL_RUIN_SPACING = 2;
    private static final int MEDIUM_RUIN_SPACING = 2;
    private static final int LARGE_RUIN_SPACING = 3;

    public static void addPieces(
            StructureTemplateManager manager,
            BlockPos pos,
            BlockRotation rotation,
            StructurePiecesHolder holder,
            Random random,
            AncientRuinsStructure structure
    ) {
        int centerIndex = random.nextInt(CENTER_PIECES.length);
        int statueIndex = random.nextInt(STATUES.length);

        BlockRotation noRotation = BlockRotation.NONE;
        addOuterWallPieces(manager, pos, holder, random);

        BlockPos innerGroupOffset = pos.add(-11, 0, -4);

        holder.addPiece(new Piece(manager, INNER_WALLS, innerGroupOffset, noRotation, -1, -1, PieceType.INNER_WALLS));

        BlockPos centerOffset = innerGroupOffset.add(2, 0, 2);
        holder.addPiece(new Piece(manager, CENTER_PIECES[centerIndex], centerOffset, noRotation, centerIndex, statueIndex, PieceType.CENTER));

        BlockPos statueOffset = centerOffset.add(13, 0, 13);
        holder.addPiece(new Piece(manager, STATUES[statueIndex], statueOffset, noRotation, centerIndex, statueIndex, PieceType.STATUE));

        int ruinCount = random.nextBetween(structure.minRuins, structure.maxRuins);
        addSurroundingRuins(manager, random, pos, structure, holder, ruinCount);
    }

    private static void addOuterWallPieces(
            StructureTemplateManager manager,
            BlockPos basePos,
            StructurePiecesHolder holder,
            Random random
    ) {
        Identifier northWall = OUTER_WALL_ENTRANCE;
        Identifier eastWall = OUTER_WALL_PIECES[random.nextInt(OUTER_WALL_PIECES.length)];
        Identifier southWall = OUTER_WALL_PIECES[random.nextInt(OUTER_WALL_PIECES.length)];
        Identifier westWall = OUTER_WALL_PIECES[random.nextInt(OUTER_WALL_PIECES.length)];

        Identifier nwCorner = OUTER_WALL_CORNERS[random.nextInt(OUTER_WALL_CORNERS.length)];
        Identifier neCorner = OUTER_WALL_CORNERS[random.nextInt(OUTER_WALL_CORNERS.length)];
        Identifier seCorner = OUTER_WALL_CORNERS[random.nextInt(OUTER_WALL_CORNERS.length)];
        Identifier swCorner = OUTER_WALL_CORNERS[random.nextInt(OUTER_WALL_CORNERS.length)];

        BlockPos nwCornerPos = basePos.add(-39, 0, 37);
        holder.addPiece(new Piece(manager, nwCorner, nwCornerPos, BlockRotation.NONE, -1, -1, PieceType.OUTER_CORNER));

        BlockPos neCornerPos = basePos.add(-16, 0, -32);
        holder.addPiece(new Piece(manager, neCorner, neCornerPos, BlockRotation.CLOCKWISE_90, -1, -1, PieceType.OUTER_CORNER));

        BlockPos swCornerPos = basePos.add(30, 0, 60);
        holder.addPiece(new Piece(manager, swCorner, swCornerPos, BlockRotation.COUNTERCLOCKWISE_90, -1, -1, PieceType.OUTER_CORNER));

        BlockPos seCornerPos = basePos.add(53, 0, -9);
        holder.addPiece(new Piece(manager, seCorner, seCornerPos, BlockRotation.CLOCKWISE_180, -1, -1, PieceType.OUTER_CORNER));


        BlockPos northWallPos = basePos.add(-15, 0, 58);
        holder.addPiece(new Piece(manager, northWall, northWallPos, BlockRotation.NONE, -1, -1, PieceType.OUTER_WALLS));

        BlockPos southWallPos = basePos.add(29, 0, -30);
        holder.addPiece(new Piece(manager, southWall, southWallPos, BlockRotation.CLOCKWISE_180, -1, -1, PieceType.OUTER_WALLS));

        BlockPos eastWallPos = basePos.add(53, 0, -8);
        holder.addPiece(new Piece(manager, eastWall, eastWallPos, BlockRotation.CLOCKWISE_90, -1, -1, PieceType.OUTER_WALLS));

        BlockPos westWallPos = basePos.add(-39, 0, 36);
        holder.addPiece(new Piece(manager, westWall, westWallPos, BlockRotation.COUNTERCLOCKWISE_90, -1, -1, PieceType.OUTER_WALLS));
    }


    private static void addSurroundingRuins(
            StructureTemplateManager manager,
            Random random,
            BlockPos centerPos,
            AncientRuinsStructure structure,
            StructurePiecesHolder pieces,
            int count
    ) {
        int interiorMinX = centerPos.getX() - 37;
        int interiorMinZ = centerPos.getZ() - 30;
        int interiorMaxX = interiorMinX + INTERIOR_SIZE;
        int interiorMaxZ = interiorMinZ + INTERIOR_SIZE;

        int exclusionStartX = interiorMinX + (INTERIOR_SIZE - CENTER_EXCLUSION_SIZE) / 2;
        int exclusionStartZ = interiorMinZ + (INTERIOR_SIZE - CENTER_EXCLUSION_SIZE) / 2;

        int largeMinX = interiorMinX + 9 + random.nextInt(2);
        int largeMinZ = interiorMinZ + 9 + random.nextInt(2);
        int largeMaxX = interiorMaxX - 9 - random.nextInt(2);
        int largeMaxZ = interiorMaxZ - 9 - random.nextInt(2);

        int mediumMinX = interiorMinX + 6 + random.nextInt(3);
        int mediumMinZ = interiorMinZ + 6 + random.nextInt(3);
        int mediumMaxX = interiorMaxX - 6 - random.nextInt(3);
        int mediumMaxZ = interiorMaxZ - 6 - random.nextInt(3);

        int smallMinX = interiorMinX + 4;
        int smallMinZ = interiorMinZ + 4;
        int smallMaxX = interiorMaxX - 4;
        int smallMaxZ = interiorMaxZ - 4;

        PackingContext largeContext = new PackingContext(largeMinX, largeMinZ, largeMaxX - largeMinX, largeMaxZ - largeMinZ);
        PackingContext mediumContext = new PackingContext(mediumMinX, mediumMinZ, mediumMaxX - mediumMinX, mediumMaxZ - mediumMinZ);
        PackingContext smallContext = new PackingContext(smallMinX, smallMinZ, smallMaxX - smallMinX, smallMaxZ - smallMinZ);

        BlockPos exclusionStart = new BlockPos(exclusionStartX, centerPos.getY(), exclusionStartZ);
        largeContext.addExclusionZone(exclusionStart, CENTER_EXCLUSION_SIZE, CENTER_EXCLUSION_SIZE);
        mediumContext.addExclusionZone(exclusionStart, CENTER_EXCLUSION_SIZE, CENTER_EXCLUSION_SIZE);
        smallContext.addExclusionZone(exclusionStart, CENTER_EXCLUSION_SIZE, CENTER_EXCLUSION_SIZE);

        List<RuinToPlace> ruinsToPlace = Lists.newArrayList();

        int poolSize = 55 + random.nextInt(3);
        int smallCount = (int)(poolSize * 0.5);
        int mediumCount = (int)(poolSize * 0.3);
        int largeCount = (int)(poolSize * 0.2);

        for (int i = 0; i < largeCount; i++) ruinsToPlace.add(new RuinToPlace(RuinSize.LARGE, random));
        for (int i = 0; i < mediumCount; i++) ruinsToPlace.add(new RuinToPlace(RuinSize.MEDIUM, random));
        for (int i = 0; i < smallCount; i++) ruinsToPlace.add(new RuinToPlace(RuinSize.SMALL, random));

        Collections.shuffle(ruinsToPlace, new java.util.Random(random.nextLong()));
        ruinsToPlace.sort(Comparator.comparingInt(r -> -r.size.ordinal()));

        for (RuinToPlace ruin : ruinsToPlace) {
            PackingContext context = switch (ruin.size) {
                case LARGE -> largeContext;
                case MEDIUM -> mediumContext;
                case SMALL -> smallContext;
            };

            int spacing = getSpacingForSize(ruin.size);
            int[] dims = getTemplateDimensions(ruin.template);

            int widthWithSpacing = dims[0] + (spacing * 2);
            int depthWithSpacing = dims[1] + (spacing * 2);

            PlacementResult result = context.findBestPlacement(widthWithSpacing, depthWithSpacing, exclusionStart, CENTER_EXCLUSION_SIZE);
            PlacementResult rotatedResult = context.findBestPlacement(depthWithSpacing, widthWithSpacing, exclusionStart, CENTER_EXCLUSION_SIZE);

            if (result == null || (rotatedResult != null && rotatedResult.wastedSpace < result.wastedSpace)) {
                result = rotatedResult;
                ruin.rotation = (ruin.rotation == BlockRotation.NONE) ? BlockRotation.CLOCKWISE_90 : BlockRotation.NONE;
            }

            if (result != null) {
                int actualX = result.x + spacing;
                int actualZ = result.z + spacing;

                BlockPos ruinPos = new BlockPos(actualX, centerPos.getY(), actualZ);
                pieces.addPiece(new Piece(manager, ruin.template, ruinPos, ruin.rotation, -1, -1, PieceType.RUIN));

                context.placeRectangle(result.x, result.z, result.width, result.depth);
            }
        }
    }

    private static int getSpacingForSize(RuinSize size) {
        return switch (size) {
            case SMALL -> SMALL_RUIN_SPACING;
            case MEDIUM -> MEDIUM_RUIN_SPACING;
            case LARGE -> LARGE_RUIN_SPACING;
        };
    }

    private static class PackingContext {
        private final List<FreeRect> freeRects;
        private final int baseX;
        private final int baseZ;

        public PackingContext(int x, int z, int width, int depth) {
            this.baseX = x;
            this.baseZ = z;
            this.freeRects = Lists.newArrayList();
            this.freeRects.add(new FreeRect(x, z, width, depth));
        }

        public void addExclusionZone(BlockPos pos, int width, int depth) {
            List<FreeRect> newFreeRects = Lists.newArrayList();

            for (FreeRect rect : freeRects) {
                List<FreeRect> splits = splitRectangle(rect, pos.getX(), pos.getZ(), width, depth);
                newFreeRects.addAll(splits);
            }

            freeRects.clear();
            freeRects.addAll(newFreeRects);
            removeDuplicatesAndContained();
        }

        public PlacementResult findBestPlacement(int width, int depth, BlockPos exclusion, int exclusionSize) {
            PlacementResult best = null;

            for (FreeRect rect : freeRects) {
                if (rect.width >= width && rect.depth >= depth) {
                    if (intersectsExclusion(rect.x, rect.z, width, depth, exclusion.getX(), exclusion.getZ(), exclusionSize)) {
                        continue;
                    }

                    int wastedSpace = (rect.width * rect.depth) - (width * depth);

                    if (best == null || wastedSpace < best.wastedSpace) {
                        best = new PlacementResult(rect.x, rect.z, width, depth, wastedSpace);
                    }
                }
            }

            return best;
        }

        public void placeRectangle(int x, int z, int width, int depth) {
            List<FreeRect> newFreeRects = Lists.newArrayList();

            for (FreeRect rect : freeRects) {
                List<FreeRect> splits = splitRectangle(rect, x, z, width, depth);
                newFreeRects.addAll(splits);
            }

            freeRects.clear();
            freeRects.addAll(newFreeRects);
            removeDuplicatesAndContained();
        }

        private List<FreeRect> splitRectangle(FreeRect rect, int x, int z, int width, int depth) {
            List<FreeRect> result = Lists.newArrayList();

            if (!intersects(rect.x, rect.z, rect.width, rect.depth, x, z, width, depth)) {
                result.add(rect);
                return result;
            }

            if (x > rect.x) {
                result.add(new FreeRect(rect.x, rect.z, x - rect.x, rect.depth));
            }

            if (x + width < rect.x + rect.width) {
                result.add(new FreeRect(x + width, rect.z, rect.x + rect.width - (x + width), rect.depth));
            }

            if (z > rect.z) {
                result.add(new FreeRect(rect.x, rect.z, rect.width, z - rect.z));
            }

            if (z + depth < rect.z + rect.depth) {
                result.add(new FreeRect(rect.x, z + depth, rect.width, rect.z + rect.depth - (z + depth)));
            }

            return result;
        }

        private void removeDuplicatesAndContained() {
            for (int i = 0; i < freeRects.size(); i++) {
                for (int j = i + 1; j < freeRects.size(); ) {
                    FreeRect r1 = freeRects.get(i);
                    FreeRect r2 = freeRects.get(j);

                    if (contains(r1, r2)) {
                        freeRects.remove(j);
                    } else if (contains(r2, r1)) {
                        freeRects.remove(i);
                        i--;
                        break;
                    } else {
                        j++;
                    }
                }
            }
        }

        private boolean contains(FreeRect r1, FreeRect r2) {
            return r2.x >= r1.x && r2.z >= r1.z &&
                    r2.x + r2.width <= r1.x + r1.width &&
                    r2.z + r2.depth <= r1.z + r1.depth;
        }

        private boolean intersects(int x1, int z1, int w1, int d1, int x2, int z2, int w2, int d2) {
            return !(x1 + w1 <= x2 || x2 + w2 <= x1 || z1 + d1 <= z2 || z2 + d2 <= z1);
        }

        private boolean intersectsExclusion(int x, int z, int w, int d, int exX, int exZ, int exSize) {
            return intersects(x, z, w, d, exX, exZ, exSize, exSize);
        }
    }

    private static class FreeRect {
        int x, z, width, depth;

        FreeRect(int x, int z, int width, int depth) {
            this.x = x;
            this.z = z;
            this.width = width;
            this.depth = depth;
        }
    }

    private static class PlacementResult {
        int x, z, width, depth, wastedSpace;

        PlacementResult(int x, int z, int width, int depth, int wastedSpace) {
            this.x = x;
            this.z = z;
            this.width = width;
            this.depth = depth;
            this.wastedSpace = wastedSpace;
        }
    }

    private static class RuinToPlace {
        RuinSize size;
        Identifier template;
        BlockRotation rotation;

        RuinToPlace(RuinSize size, Random random) {
            this.size = size;
            this.template = getRandomRuinTemplate(random, size);
            this.rotation = BlockRotation.NONE;
        }
    }

    private static int[] getTemplateDimensions(Identifier template) {
        String path = template.toString();

        if (path.contains("small_ruins_1")) return new int[]{7, 8};
        if (path.contains("small_ruins_2")) return new int[]{7, 7};
        if (path.contains("small_ruins_3")) return new int[]{9, 9};
        if (path.contains("small_ruins_4")) return new int[]{7, 8};
        if (path.contains("small_ruins_5")) return new int[]{9, 7};

        if (path.contains("medium_ruins_1")) return new int[]{13, 12};
        if (path.contains("medium_ruins_2")) return new int[]{11, 15};
        if (path.contains("medium_ruins_3")) return new int[]{13, 14};

        if (path.contains("large_ruins_1")) return new int[]{19, 13};
        if (path.contains("large_ruins_2")) return new int[]{21, 13};
        if (path.contains("large_ruins_3")) return new int[]{17, 19};

        return new int[]{9, 9};
    }

    private static Identifier getRandomRuinTemplate(Random random, RuinSize size) {
        return switch (size) {
            case SMALL -> SMALL_RUINS[random.nextInt(SMALL_RUINS.length)];
            case MEDIUM -> MEDIUM_RUINS[random.nextInt(MEDIUM_RUINS.length)];
            case LARGE -> LARGE_RUINS[random.nextInt(LARGE_RUINS.length)];
        };
    }

    private static int getMaxRuinSize(RuinSize size) {
        return switch (size) {
            case SMALL -> SMALL_RUIN_MAX_SIZE;
            case MEDIUM -> MEDIUM_RUIN_MAX_SIZE;
            case LARGE -> LARGE_RUIN_MAX_SIZE;
        };
    }

    private enum RuinSize {
        SMALL, MEDIUM, LARGE
    }

    private enum PieceType {
        INNER_WALLS, CENTER, STATUE, RUIN, OUTER_WALLS, OUTER_CORNER
    }

    private static class PlacedPiece {
        final BlockPos pos;
        final int width;
        final int depth;

        PlacedPiece(BlockPos pos, int width, int depth) {
            this.pos = pos;
            this.width = width;
            this.depth = depth;
        }
    }

    public static class Piece extends SimpleStructurePiece {
        private final int centerIndex;
        private final int statueIndex;
        private final PieceType pieceType;

        public static Piece load(StructureTemplateManager manager, NbtCompound nbt) {
            return new Piece(manager, nbt);
        }

        public Piece(
                StructureTemplateManager manager,
                Identifier template,
                BlockPos pos,
                BlockRotation rotation,
                int centerIndex,
                int statueIndex,
                PieceType pieceType
        ) {
            super(
                    ModStructurePieceTypes.ANCIENT_RUINS,
                    0,
                    manager,
                    template,
                    template.toString(),
                    createPlacementData(rotation),
                    pos
            );
            this.centerIndex = centerIndex;
            this.statueIndex = statueIndex;
            this.pieceType = pieceType;
        }

        public Piece(StructureTemplateManager manager, NbtCompound nbt) {
            super(
                    ModStructurePieceTypes.ANCIENT_RUINS,
                    nbt,
                    manager,
                    (identifier) -> createPlacementData(
                            BlockRotation.CODEC.parse(NbtOps.INSTANCE, nbt.get("Rot")).result().orElse(BlockRotation.NONE)
                    )
            );
            this.centerIndex = nbt.getInt("CenterIndex");
            this.statueIndex = nbt.getInt("StatueIndex");
            this.pieceType = PieceType.valueOf(nbt.getString("PieceType"));
        }

        private static StructurePlacementData createPlacementData(BlockRotation rotation) {
            return new StructurePlacementData()
                    .setRotation(rotation)
                    .setMirror(BlockMirror.NONE)
                    .addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
        }

        protected void writeNbt(StructureContext context, NbtCompound nbt) {
            super.writeNbt(context, nbt);
            BlockRotation.CODEC.encodeStart(NbtOps.INSTANCE, this.placementData.getRotation())
                    .result()
                    .ifPresent(encoded -> nbt.put("Rot", encoded));
            nbt.putInt("CenterIndex", this.centerIndex);
            nbt.putInt("StatueIndex", this.statueIndex);
            nbt.putString("PieceType", this.pieceType.name());
        }

        protected void handleMetadata(
                String metadata,
                BlockPos pos,
                ServerWorldAccess world,
                Random random,
                BlockBox boundingBox
        ) {
            if (metadata.equals("center_chest")) {
                this.placeChestWithLoot(world, boundingBox, random, pos,
                        Identifier.of("earth-and-water", "chests/ancient_ruins_center"));
            } else if (metadata.equals("statue_chest_1")) {
                this.placeChestWithLoot(world, boundingBox, random, pos,
                        Identifier.of("earth-and-water", "chests/ancient_ruins_statue_1"));
            } else if (metadata.equals("statue_chest_2")) {
                this.placeChestWithLoot(world, boundingBox, random, pos,
                        Identifier.of("earth-and-water", "chests/ancient_ruins_statue_2"));
            } else if (metadata.equals("statue_chest_3")) {
                this.placeChestWithLoot(world, boundingBox, random, pos,
                        Identifier.of("earth-and-water", "chests/ancient_ruins_statue_3"));
            } else if (metadata.equals("medium_chest")) {
                this.placeChestWithLoot(world, boundingBox, random, pos,
                        Identifier.of("earth-and-water", "chests/ancient_ruins_medium"));
            } else if (metadata.equals("large_chest")) {
                this.placeChestWithLoot(world, boundingBox, random, pos,
                        Identifier.of("earth-and-water", "chests/ancient_ruins_large"));
            }
        }

        private void setupSpawner(
                ServerWorldAccess world,
                BlockBox boundingBox,
                BlockPos structureBlockPos,
                String entityName
        ) {
            for (Direction direction : Direction.values()) {
                BlockPos spawnerPos = structureBlockPos.offset(direction);

                if (!boundingBox.contains(spawnerPos)) {
                    continue;
                }

                BlockState state = world.getBlockState(spawnerPos);

                if (state.getBlock() instanceof ReinforcedSpawnerBlock) {
                    BlockEntity blockEntity = world.getBlockEntity(spawnerPos);
                    if (blockEntity instanceof ReinforcedSpawnerBlockEntity spawnerEntity) {
                        EntityType<?> entityType;

                        if (entityName.equals("bore")) {
                            entityType = ModEntities.BORE;
                        } else if (entityName.equals("brine")) {
                            entityType = ModEntities.BRINE;
                        } else {
                            Identifier entityId = Identifier.of("earth-and-water", entityName);
                            entityType = Registries.ENTITY_TYPE.get(entityId);

                            if (entityType == null || !Registries.ENTITY_TYPE.containsId(entityId)) {
                                entityId = Identifier.of("minecraft", entityName);
                                entityType = Registries.ENTITY_TYPE.get(entityId);
                            }
                        }

                        if (entityType != null) {
                            spawnerEntity.setEntityType(entityType);
                            spawnerEntity.activate();

                            BlockState newState = state
                                    .with(ReinforcedSpawnerBlock.ACTIVE, true)
                                    .with(ReinforcedSpawnerBlock.KEYHOLE, false);
                            world.setBlockState(spawnerPos, newState, 3);

                            spawnerEntity.markDirty();
                        }
                        return;
                    }
                }
            }
        }

        private void placeChestWithLoot(
                ServerWorldAccess world,
                BlockBox boundingBox,
                Random random,
                BlockPos structureBlockPos,
                Identifier lootTable
        ) {
            for (Direction direction : Direction.values()) {
                BlockPos chestPos = structureBlockPos.offset(direction);

                if (!boundingBox.contains(chestPos)) {
                    continue;
                }

                BlockState state = world.getBlockState(chestPos);

                if (state.isOf(Blocks.CHEST)) {
                    BlockEntity blockEntity = world.getBlockEntity(chestPos);
                    if (blockEntity instanceof ChestBlockEntity chestEntity) {
                        chestEntity.setLootTable(
                                RegistryKey.of(RegistryKeys.LOOT_TABLE, lootTable),
                                random.nextLong()
                        );
                        return;
                    }
                }
            }
        }

        public void generate(
                StructureWorldAccess world,
                StructureAccessor structureAccessor,
                ChunkGenerator chunkGenerator,
                Random random,
                BlockBox chunkBox,
                ChunkPos chunkPos,
                BlockPos pivot
        ) {
            super.generate(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pivot);

            this.template.getInfosForBlock(this.pos, this.placementData, Blocks.STRUCTURE_BLOCK)
                    .forEach(structureBlockInfo -> {
                        if (structureBlockInfo.nbt() != null) {
                            String metadata = structureBlockInfo.nbt().getString("metadata");
                            if (!metadata.isEmpty()) {
                                this.handleMetadata(
                                        metadata,
                                        structureBlockInfo.pos(),
                                        world,
                                        random,
                                        chunkBox
                                );
                            }
                        }
                    });
        }
    }
}