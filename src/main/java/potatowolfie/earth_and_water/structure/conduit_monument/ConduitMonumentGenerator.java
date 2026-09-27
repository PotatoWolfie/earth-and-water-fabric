package potatowolfie.earth_and_water.structure.conduit_monument;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import potatowolfie.earth_and_water.block.custom.ReinforcedSpawnerBlock;
import potatowolfie.earth_and_water.block.entity.custom.ReinforcedSpawnerBlockEntity;
import potatowolfie.earth_and_water.entity.ModEntities;
import potatowolfie.earth_and_water.entity.brine.BrineEntity;
import potatowolfie.earth_and_water.structure.ModStructurePieceTypes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConduitMonumentGenerator {
    private static final Identifier[] CENTER_PIECES = new Identifier[] {
            Identifier.fromNamespaceAndPath("earth-and-water", "conduit_monument/center_1"),
            Identifier.fromNamespaceAndPath("earth-and-water", "conduit_monument/center_2"),
            Identifier.fromNamespaceAndPath("earth-and-water", "conduit_monument/center_3")
    };

    private static final Identifier[] MEDIUM_RUINS = new Identifier[] {
            Identifier.fromNamespaceAndPath("earth-and-water", "conduit_monument/medium_ruins_1"),
            Identifier.fromNamespaceAndPath("earth-and-water", "conduit_monument/medium_ruins_2"),
            Identifier.fromNamespaceAndPath("earth-and-water", "conduit_monument/medium_ruins_3")
    };

    private static final Identifier[] SMALL_RUINS = new Identifier[] {
            Identifier.fromNamespaceAndPath("earth-and-water", "conduit_monument/small_ruins_1"),
            Identifier.fromNamespaceAndPath("earth-and-water", "conduit_monument/small_ruins_2"),
            Identifier.fromNamespaceAndPath("earth-and-water", "conduit_monument/small_ruins_3")
    };

    private static Identifier getRandomCenterPiece(RandomSource random) {
        return Util.getRandom(CENTER_PIECES, random);
    }

    private static Identifier getRandomMediumRuin(RandomSource random) {
        return Util.getRandom(MEDIUM_RUINS, random);
    }

    private static Identifier getRandomSmallRuin(RandomSource random) {
        return Util.getRandom(SMALL_RUINS, random);
    }

    private static final Set<Long> BRINE_SPAWNED_STRUCTURES = new HashSet<>();

    private static long getStructureKey(BoundingBox box) {
        return ((long) box.minX() & 0xFFFFF)
                ^ (((long) box.minY() & 0xFFFFF) << 20)
                ^ (((long) box.minZ() & 0xFFFFF) << 40);
    }

    public static void addPieces(
            StructureTemplateManager manager,
            BlockPos pos,
            Rotation rotation,
            StructurePiecesBuilder holder,
            RandomSource random,
            ConduitMonumentStructure structure
    ) {
        int centerIndex = random.nextInt(CENTER_PIECES.length);
        Identifier centerPiece = CENTER_PIECES[centerIndex];
        holder.addPiece(new Piece(manager, centerPiece, pos, rotation, centerIndex));

        int ruinCount = Mth.nextInt(random, structure.minRuins, structure.maxRuins);

        addSurroundingRuins(manager, random, rotation, pos, structure, holder, ruinCount);
    }

    private static void addSurroundingRuins(
            StructureTemplateManager manager,
            RandomSource random,
            Rotation centerRotation,
            BlockPos centerPos,
            ConduitMonumentStructure structure,
            StructurePiecesBuilder pieces,
            int count
    ) {
        BlockPos blockPos = new BlockPos(centerPos.getX(), 90, centerPos.getZ());
        BlockPos blockPos2 = StructureTemplate.transform(
                new BlockPos(15, 0, 15),
                Mirror.NONE,
                centerRotation,
                BlockPos.ZERO
        ).offset(blockPos);
        BoundingBox centerBox = BoundingBox.fromCorners(blockPos, blockPos2);
        BlockPos blockPos3 = new BlockPos(
                Math.min(blockPos.getX(), blockPos2.getX()),
                blockPos.getY(),
                Math.min(blockPos.getZ(), blockPos2.getZ())
        );

        List<BlockPos> positions = getRuinPositions(random, blockPos3);
        int mediumCount = 0;

        for (int i = 0; i < count && !positions.isEmpty(); i++) {
            int index = random.nextInt(positions.size());
            BlockPos ruinPos = positions.remove(index);
            Rotation ruinRotation = Rotation.getRandom(random);

            boolean isMedium = mediumCount < 3 && random.nextFloat() <= structure.mediumProbability;
            if (isMedium) {
                mediumCount++;
            }

            Identifier ruinTemplate = isMedium ?
                    getRandomMediumRuin(random) :
                    getRandomSmallRuin(random);

            BlockPos ruinPos2 = StructureTemplate.transform(
                    new BlockPos(10, 0, 10),
                    Mirror.NONE,
                    ruinRotation,
                    BlockPos.ZERO
            ).offset(ruinPos);
            BoundingBox ruinBox = BoundingBox.fromCorners(ruinPos, ruinPos2);

            if (!ruinBox.intersects(centerBox)) {
                pieces.addPiece(new Piece(manager, ruinTemplate, ruinPos, ruinRotation, -1));
            }
        }
    }

    private static List<BlockPos> getRuinPositions(RandomSource random, BlockPos centerPos) {
        List<BlockPos> list = Lists.newArrayList();

        list.add(centerPos.offset(-12 + Mth.nextInt(random, 1, 5), 0, 12 + Mth.nextInt(random, 1, 5)));
        list.add(centerPos.offset(Mth.nextInt(random, 1, 5), 0, 12 + Mth.nextInt(random, 1, 5)));
        list.add(centerPos.offset(12 + Mth.nextInt(random, 1, 5), 0, 12 + Mth.nextInt(random, 1, 5)));

        list.add(centerPos.offset(-12 + Mth.nextInt(random, 1, 5), 0, -12 - Mth.nextInt(random, 1, 5)));
        list.add(centerPos.offset(Mth.nextInt(random, 1, 5), 0, -12 - Mth.nextInt(random, 1, 5)));
        list.add(centerPos.offset(12 + Mth.nextInt(random, 1, 5), 0, -12 - Mth.nextInt(random, 1, 5)));

        list.add(centerPos.offset(12 + Mth.nextInt(random, 1, 5), 0, -12 + Mth.nextInt(random, 1, 5)));
        list.add(centerPos.offset(12 + Mth.nextInt(random, 1, 5), 0, Mth.nextInt(random, 1, 5)));
        list.add(centerPos.offset(12 + Mth.nextInt(random, 1, 5), 0, 12 + Mth.nextInt(random, 1, 5)));

        list.add(centerPos.offset(-12 - Mth.nextInt(random, 1, 5), 0, -12 + Mth.nextInt(random, 1, 5)));
        list.add(centerPos.offset(-12 - Mth.nextInt(random, 1, 5), 0, Mth.nextInt(random, 1, 5)));
        list.add(centerPos.offset(-12 - Mth.nextInt(random, 1, 5), 0, 12 + Mth.nextInt(random, 1, 5)));

        return list;
    }

    public static class Piece extends TemplateStructurePiece {
        private final int centerIndex;

        public Piece(
                StructureTemplateManager manager,
                Identifier template,
                BlockPos pos,
                Rotation rotation,
                int centerIndex
        ) {
            super(
                    ModStructurePieceTypes.CONDUIT_MONUMENT,
                    0,
                    manager,
                    template,
                    template.toString(),
                    createPlacementData(rotation),
                    pos
            );
            this.centerIndex = centerIndex;
        }

        public Piece(StructureTemplateManager manager, CompoundTag nbt) {
            super(
                    ModStructurePieceTypes.CONDUIT_MONUMENT,
                    nbt,
                    manager,
                    (identifier) -> createPlacementData(
                            nbt.read("Rot", Rotation.CODEC).orElse(Rotation.NONE)
                    )
            );
            this.centerIndex = nbt.getInt("CenterIndex").orElse(-1);
        }

        private static StructurePlaceSettings createPlacementData(Rotation rotation) {
            return new StructurePlaceSettings()
                    .setRotation(rotation)
                    .setMirror(Mirror.NONE)
                    .addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
        }

        protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {
            super.addAdditionalSaveData(context, nbt);
            nbt.store("Rot", Rotation.CODEC, this.placeSettings.getRotation());
            nbt.putInt("CenterIndex", this.centerIndex);
        }

        protected void handleDataMarker(
                String metadata,
                BlockPos pos,
                ServerLevelAccessor world,
                RandomSource random,
                BoundingBox boundingBox
        ) {
            if (this.centerIndex == -1) {
                return;
            }

            if (metadata.equals("chest_1")) {
                switch (this.centerIndex) {
                    case 0:
                        this.placeChestWithLoot(world, boundingBox, random, pos,
                                Identifier.fromNamespaceAndPath("earth-and-water", "chests/center_1_chest_1"));
                        break;
                    case 1:
                        this.placeChestWithLoot(world, boundingBox, random, pos,
                                Identifier.fromNamespaceAndPath("earth-and-water", "chests/center_2_chest_1"));
                        break;
                    case 2:
                        this.placeChestWithLoot(world, boundingBox, random, pos,
                                Identifier.fromNamespaceAndPath("earth-and-water", "chests/center_3"));
                        break;
                }
            } else if (metadata.equals("chest_2")) {
                this.placeChestWithLoot(world, boundingBox, random, pos,
                        Identifier.fromNamespaceAndPath("earth-and-water", "chests/center_shared_chest_2"));
            } else if (metadata.startsWith("spawner_")) {
                String entityName = metadata.substring(8);
                this.setupSpawner(world, boundingBox, pos, entityName);
            }
        }

        private void setupSpawner(
                ServerLevelAccessor world,
                BoundingBox boundingBox,
                BlockPos structureBlockPos,
                String entityName
        ) {
            for (Direction direction : Direction.values()) {
                BlockPos spawnerPos = structureBlockPos.relative(direction);

                if (!boundingBox.isInside(spawnerPos)) {
                    continue;
                }

                BlockState state = world.getBlockState(spawnerPos);

                if (state.getBlock() instanceof ReinforcedSpawnerBlock) {
                    BlockEntity blockEntity = world.getBlockEntity(spawnerPos);
                    if (blockEntity instanceof ReinforcedSpawnerBlockEntity spawnerEntity) {
                        EntityType<?> entityType;

                        if (entityName.equals("brine")) {
                            entityType = ModEntities.BRINE;
                        } else {
                            Identifier entityId = Identifier.fromNamespaceAndPath("earth-and-water", entityName);
                            entityType = BuiltInRegistries.ENTITY_TYPE.getValue(entityId);

                            if (entityType == null || !BuiltInRegistries.ENTITY_TYPE.containsKey(entityId)) {
                                entityId = Identifier.fromNamespaceAndPath("minecraft", entityName);
                                entityType = BuiltInRegistries.ENTITY_TYPE.getValue(entityId);
                            }
                        }

                        if (entityType != null) {
                            spawnerEntity.setEntityType(entityType);
                            spawnerEntity.activate();
                            spawnerEntity.setChanged();
                            ServerLevel serverWorld = world.getLevel();
                            BlockState newState = state
                                    .setValue(ReinforcedSpawnerBlock.ACTIVE, true)
                                    .setValue(ReinforcedSpawnerBlock.KEYHOLE, false);
                            serverWorld.setBlock(spawnerPos, newState, 3);
                            serverWorld.sendBlockUpdated(spawnerPos, state, newState, 3);
                        }
                        return;
                    }
                }
            }
        }

        private void placeChestWithLoot(
                ServerLevelAccessor world,
                BoundingBox boundingBox,
                RandomSource random,
                BlockPos structureBlockPos,
                Identifier lootTable
        ) {
            for (Direction direction : Direction.values()) {
                BlockPos chestPos = structureBlockPos.relative(direction);

                if (!boundingBox.isInside(chestPos)) {
                    continue;
                }

                BlockState state = world.getBlockState(chestPos);

                if (state.is(Blocks.CHEST)) {
                    BlockEntity blockEntity = world.getBlockEntity(chestPos);
                    if (blockEntity instanceof ChestBlockEntity chestEntity) {
                        chestEntity.setLootTable(
                                ResourceKey.create(Registries.LOOT_TABLE, lootTable),
                                random.nextLong()
                        );
                        return;
                    }
                }
            }
        }

        private void fillUnderStructure(WorldGenLevel world, BoundingBox chunkBox, RandomSource random) {
            BlockPos structureStart = this.templatePosition;
            BlockPos structureEnd = StructureTemplate.transform(
                    new BlockPos(this.template.getSize().getX() - 1, this.template.getSize().getY() - 1, this.template.getSize().getZ() - 1),
                    Mirror.NONE,
                    this.placeSettings.getRotation(),
                    BlockPos.ZERO
            ).offset(this.templatePosition);

            int minX = Math.min(structureStart.getX(), structureEnd.getX());
            int maxX = Math.max(structureStart.getX(), structureEnd.getX());
            int minY = Math.min(structureStart.getY(), structureEnd.getY());
            int maxY = Math.max(structureStart.getY(), structureEnd.getY());
            int minZ = Math.min(structureStart.getZ(), structureEnd.getZ());
            int maxZ = Math.max(structureStart.getZ(), structureEnd.getZ());

            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos bottomPos = new BlockPos(x, minY, z);
                    BlockState bottomState = world.getBlockState(bottomPos);

                    boolean hasBottomBlock = !bottomState.isAir() &&
                            !bottomState.is(Blocks.STRUCTURE_VOID);

                    if (hasBottomBlock && bottomState.is(Blocks.WATER)) {
                        hasBottomBlock = false;
                    }

                    if (hasBottomBlock) {
                        BlockPos belowPos = new BlockPos(x, minY - 1, z);
                        BlockState belowState = world.getBlockState(belowPos);

                        if (belowState.isAir() || belowState.getFluidState().is(FluidTags.WATER)) {
                            int terrainY = findTerrainHeight(world, x, z, minY - 1);

                            if (terrainY < minY - 1) {
                                createTaperedPillar(world, x, z, terrainY, minY - 1, random);
                            }
                        }
                    }
                }
            }
        }

        private int findTerrainHeight(WorldGenLevel world, int x, int z, int startY) {
            for (int y = startY; y >= world.getMinY(); y--) {
                BlockPos pos = new BlockPos(x, y, z);
                BlockState state = world.getBlockState(pos);

                if (!state.isAir() && !state.getFluidState().is(FluidTags.WATER)) {
                    return y;
                }
            }
            return world.getMinY();
        }

        private void createTaperedPillar(WorldGenLevel world, int centerX, int centerZ, int terrainY, int topY, RandomSource random) {
            int pillarHeight = topY - terrainY;

            SimplexNoise noiseSampler = new SimplexNoise(random);
            float baseRadiusMultiplier = 2.0f + random.nextFloat() * 1.5f;
            float taperingCurve = 0.7f + random.nextFloat() * 0.6f;
            if (pillarHeight > 10) {
                baseRadiusMultiplier += (pillarHeight - 10) * 0.1f;
            }

            for (int y = terrainY + 1; y <= topY; y++) {
                float progress = (float)(y - terrainY) / (float)pillarHeight;

                double noiseScale = 0.1;
                double noise = noiseSampler.get(centerX * noiseScale, y * noiseScale * 0.5, centerZ * noiseScale);
                float noiseOffset = (float)noise * 0.5f;
                float radiusFloat = (1.0f - (float)Math.pow(progress, taperingCurve)) * baseRadiusMultiplier + noiseOffset;
                int radius = Math.max(0, (int)Math.ceil(radiusFloat));

                for (int dx = -radius - 1; dx <= radius + 1; dx++) {
                    for (int dz = -radius - 1; dz <= radius + 1; dz++) {
                        int actualX = centerX + dx;
                        int actualZ = centerZ + dz;

                        double distance = Math.sqrt(dx * dx + dz * dz);
                        double edgeNoise = noiseSampler.get(actualX * 0.3, y * 0.2, actualZ * 0.3);
                        float edgeVariation = (float)edgeNoise * 0.8f;

                        float threshold = radius + 0.5f + edgeVariation;

                        if (distance <= threshold) {
                            BlockPos fillPos = new BlockPos(actualX, y, actualZ);
                            BlockState currentState = world.getBlockState(fillPos);

                            if (currentState.isAir() || currentState.getFluidState().is(FluidTags.WATER)) {
                                BlockState blockToPlace = getTerrainMatchingBlock(world, centerX, centerZ, terrainY, random, y, terrainY);
                                world.setBlock(fillPos, blockToPlace, 3);
                            }
                        }
                    }
                }
            }
        }

        private BlockState getTerrainMatchingBlock(WorldGenLevel world, int x, int z, int terrainY, RandomSource random, int currentY, int baseY) {
            BlockPos terrainPos = new BlockPos(x, terrainY, z);
            BlockState terrainBlock = world.getBlockState(terrainPos);

            int depthFromBase = currentY - baseY;
            float depthRatio = (float)depthFromBase / (float)(terrainY - baseY + 1);

            if (terrainBlock.is(Blocks.SAND) || terrainBlock.is(Blocks.SANDSTONE)) {
                if (depthRatio < 0.3f) {
                    return Blocks.SANDSTONE.defaultBlockState();
                } else if (depthRatio < 0.6f) {
                    return random.nextFloat() < 0.5f ? Blocks.SANDSTONE.defaultBlockState() : Blocks.SAND.defaultBlockState();
                } else {
                    return Blocks.SAND.defaultBlockState();
                }
            }

            if (terrainBlock.is(Blocks.STONE) || terrainBlock.is(Blocks.COBBLESTONE) ||
                    terrainBlock.is(Blocks.ANDESITE) || terrainBlock.is(Blocks.DIORITE) ||
                    terrainBlock.is(Blocks.GRANITE)) {

                if (depthRatio < 0.4f) {
                    return Blocks.STONE.defaultBlockState();
                } else {
                    float r = random.nextFloat();
                    if (r < 0.4f) return Blocks.STONE.defaultBlockState();
                    else if (r < 0.6f) return Blocks.ANDESITE.defaultBlockState();
                    else if (r < 0.8f) return Blocks.COBBLESTONE.defaultBlockState();
                    else return terrainBlock;
                }
            }

            if (terrainBlock.is(Blocks.DIRT) || terrainBlock.is(Blocks.GRASS_BLOCK) ||
                    terrainBlock.is(Blocks.COARSE_DIRT)) {

                if (depthRatio < 0.3f) {
                    return Blocks.STONE.defaultBlockState();
                } else if (depthRatio < 0.6f) {
                    return random.nextFloat() < 0.5f ? Blocks.COARSE_DIRT.defaultBlockState() : Blocks.DIRT.defaultBlockState();
                } else {
                    return Blocks.DIRT.defaultBlockState();
                }
            }

            if (terrainBlock.is(Blocks.GRAVEL)) {
                if (depthRatio < 0.4f) {
                    return random.nextFloat() < 0.6f ? Blocks.STONE.defaultBlockState() : Blocks.GRAVEL.defaultBlockState();
                } else {
                    return Blocks.GRAVEL.defaultBlockState();
                }
            }

            if (depthRatio < 0.4f) {
                return Blocks.SANDSTONE.defaultBlockState();
            } else {
                return Blocks.SAND.defaultBlockState();
            }
        }

        private void spawnBrines(WorldGenLevel world, RandomSource random) {
            int brineCount = 2 + random.nextInt(2);

            BlockPos structureCenter = this.templatePosition.offset(
                    this.template.getSize().getX() / 2,
                    this.template.getSize().getY() / 2,
                    this.template.getSize().getZ() / 2
            );

            for (int i = 0; i < brineCount; i++) {
                int offsetX = random.nextInt(20) - 10;
                int offsetY = random.nextInt(10) - 5;
                int offsetZ = random.nextInt(20) - 10;

                BlockPos spawnPos = structureCenter.offset(offsetX, offsetY, offsetZ);

                if (world.getFluidState(spawnPos).is(FluidTags.WATER)) {
                    BrineEntity brine = new BrineEntity(ModEntities.BRINE, world.getLevel());
                    if (brine != null) {
                        brine.snapTo(spawnPos, 0.0F, 0.0F);
                        brine.finalizeSpawn(world, world.getCurrentDifficultyAt(spawnPos), EntitySpawnReason.STRUCTURE, null);
                        brine.setHomePosition(structureCenter);
                        world.addFreshEntity(brine);
                    }
                }
            }
        }

        public void postProcess(
                WorldGenLevel world,
                StructureManager structureAccessor,
                ChunkGenerator chunkGenerator,
                RandomSource random,
                BoundingBox chunkBox,
                ChunkPos chunkPos,
                BlockPos pivot
        ) {
            BoundingBox structureBox = new BoundingBox(
                    this.templatePosition.getX(),
                    this.templatePosition.getY(),
                    this.templatePosition.getZ(),
                    this.templatePosition.getX() + this.template.getSize().getX(),
                    this.templatePosition.getY() + this.template.getSize().getY(),
                    this.templatePosition.getZ() + this.template.getSize().getZ()
            );

            long key = getStructureKey(structureBox);

            if (this.centerIndex != -1 && BRINE_SPAWNED_STRUCTURES.add(key)) {
                spawnBrines(world, random);
            }
            int i = world.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, this.templatePosition.getX(), this.templatePosition.getZ());
            this.templatePosition = new BlockPos(this.templatePosition.getX(), i, this.templatePosition.getZ());
            super.postProcess(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pivot);
            fillUnderStructure(world, chunkBox, random);
            this.template.filterBlocks(this.templatePosition, this.placeSettings, Blocks.STRUCTURE_BLOCK, false)
                    .forEach(structureBlockInfo -> {
                        if (structureBlockInfo.nbt() != null) {
                            String metadata = structureBlockInfo.nbt().getStringOr("metadata", "");
                            if (!metadata.isEmpty()) {
                                this.handleDataMarker(
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