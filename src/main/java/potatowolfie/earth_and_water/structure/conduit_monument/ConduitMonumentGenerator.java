package potatowolfie.earth_and_water.structure.conduit_monument;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.*;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.*;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import potatowolfie.earth_and_water.block.custom.ReinforcedSpawnerBlock;
import potatowolfie.earth_and_water.block.entity.custom.ReinforcedSpawnerBlockEntity;
import potatowolfie.earth_and_water.entity.ModEntities;
import potatowolfie.earth_and_water.entity.brine.BrineEntity;
import potatowolfie.earth_and_water.structure.ModStructurePieceTypes;

import java.util.List;

public class ConduitMonumentGenerator {
    private static final Identifier[] CENTER_PIECES = new Identifier[] {
            Identifier.of("earth-and-water", "conduit_monument/center_1"),
            Identifier.of("earth-and-water", "conduit_monument/center_2"),
            Identifier.of("earth-and-water", "conduit_monument/center_3")
    };

    private static final Identifier[] MEDIUM_RUINS = new Identifier[] {
            Identifier.of("earth-and-water", "conduit_monument/medium_ruins_1"),
            Identifier.of("earth-and-water", "conduit_monument/medium_ruins_2"),
            Identifier.of("earth-and-water", "conduit_monument/medium_ruins_3")
    };

    private static final Identifier[] SMALL_RUINS = new Identifier[] {
            Identifier.of("earth-and-water", "conduit_monument/small_ruins_1"),
            Identifier.of("earth-and-water", "conduit_monument/small_ruins_2"),
            Identifier.of("earth-and-water", "conduit_monument/small_ruins_3")
    };

    private static Identifier getRandomCenterPiece(Random random) {
        return Util.getRandom(CENTER_PIECES, random);
    }

    private static Identifier getRandomMediumRuin(Random random) {
        return Util.getRandom(MEDIUM_RUINS, random);
    }

    private static Identifier getRandomSmallRuin(Random random) {
        return Util.getRandom(SMALL_RUINS, random);
    }

    public static void addPieces(
            StructureTemplateManager manager,
            BlockPos pos,
            BlockRotation rotation,
            StructurePiecesHolder holder,
            Random random,
            ConduitMonumentStructure structure
    ) {
        int centerIndex = random.nextInt(CENTER_PIECES.length);
        Identifier centerPiece = CENTER_PIECES[centerIndex];
        holder.addPiece(new Piece(manager, centerPiece, pos, rotation, centerIndex));

        int ruinCount = MathHelper.nextInt(random, structure.minRuins, structure.maxRuins);

        addSurroundingRuins(manager, random, rotation, pos, structure, holder, ruinCount);
    }

    private static void addSurroundingRuins(
            StructureTemplateManager manager,
            Random random,
            BlockRotation centerRotation,
            BlockPos centerPos,
            ConduitMonumentStructure structure,
            StructurePiecesHolder pieces,
            int count
    ) {
        BlockPos blockPos = new BlockPos(centerPos.getX(), 90, centerPos.getZ());
        BlockPos blockPos2 = StructureTemplate.transformAround(
                new BlockPos(15, 0, 15),
                BlockMirror.NONE,
                centerRotation,
                BlockPos.ORIGIN
        ).add(blockPos);
        BlockBox centerBox = BlockBox.create(blockPos, blockPos2);
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
            BlockRotation ruinRotation = BlockRotation.random(random);

            boolean isMedium = mediumCount < 3 && random.nextFloat() <= structure.mediumProbability;
            if (isMedium) {
                mediumCount++;
            }

            Identifier ruinTemplate = isMedium ?
                    getRandomMediumRuin(random) :
                    getRandomSmallRuin(random);

            BlockPos ruinPos2 = StructureTemplate.transformAround(
                    new BlockPos(10, 0, 10),
                    BlockMirror.NONE,
                    ruinRotation,
                    BlockPos.ORIGIN
            ).add(ruinPos);
            BlockBox ruinBox = BlockBox.create(ruinPos, ruinPos2);

            if (!ruinBox.intersects(centerBox)) {
                pieces.addPiece(new Piece(manager, ruinTemplate, ruinPos, ruinRotation, -1));
            }
        }
    }

    private static List<BlockPos> getRuinPositions(Random random, BlockPos centerPos) {
        List<BlockPos> list = Lists.newArrayList();

        list.add(centerPos.add(-12 + MathHelper.nextInt(random, 1, 5), 0, 12 + MathHelper.nextInt(random, 1, 5)));
        list.add(centerPos.add(MathHelper.nextInt(random, 1, 5), 0, 12 + MathHelper.nextInt(random, 1, 5)));
        list.add(centerPos.add(12 + MathHelper.nextInt(random, 1, 5), 0, 12 + MathHelper.nextInt(random, 1, 5)));

        list.add(centerPos.add(-12 + MathHelper.nextInt(random, 1, 5), 0, -12 - MathHelper.nextInt(random, 1, 5)));
        list.add(centerPos.add(MathHelper.nextInt(random, 1, 5), 0, -12 - MathHelper.nextInt(random, 1, 5)));
        list.add(centerPos.add(12 + MathHelper.nextInt(random, 1, 5), 0, -12 - MathHelper.nextInt(random, 1, 5)));

        list.add(centerPos.add(12 + MathHelper.nextInt(random, 1, 5), 0, -12 + MathHelper.nextInt(random, 1, 5)));
        list.add(centerPos.add(12 + MathHelper.nextInt(random, 1, 5), 0, MathHelper.nextInt(random, 1, 5)));
        list.add(centerPos.add(12 + MathHelper.nextInt(random, 1, 5), 0, 12 + MathHelper.nextInt(random, 1, 5)));

        list.add(centerPos.add(-12 - MathHelper.nextInt(random, 1, 5), 0, -12 + MathHelper.nextInt(random, 1, 5)));
        list.add(centerPos.add(-12 - MathHelper.nextInt(random, 1, 5), 0, MathHelper.nextInt(random, 1, 5)));
        list.add(centerPos.add(-12 - MathHelper.nextInt(random, 1, 5), 0, 12 + MathHelper.nextInt(random, 1, 5)));

        return list;
    }

    public static class Piece extends SimpleStructurePiece {
        private final int centerIndex;

        public Piece(
                StructureTemplateManager manager,
                Identifier template,
                BlockPos pos,
                BlockRotation rotation,
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

        public Piece(StructureTemplateManager manager, NbtCompound nbt) {
            super(
                    ModStructurePieceTypes.CONDUIT_MONUMENT,
                    nbt,
                    manager,
                    (identifier) -> createPlacementData(
                            nbt.get("Rot", BlockRotation.CODEC).orElse(BlockRotation.NONE)
                    )
            );
            this.centerIndex = nbt.getInt("CenterIndex").orElse(-1);
        }

        private static StructurePlacementData createPlacementData(BlockRotation rotation) {
            return new StructurePlacementData()
                    .setRotation(rotation)
                    .setMirror(BlockMirror.NONE)
                    .addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
        }

        protected void writeNbt(StructureContext context, NbtCompound nbt) {
            super.writeNbt(context, nbt);
            nbt.put("Rot", BlockRotation.CODEC, this.placementData.getRotation());
            nbt.putInt("CenterIndex", this.centerIndex);
        }

        protected void handleMetadata(
                String metadata,
                BlockPos pos,
                ServerWorldAccess world,
                Random random,
                BlockBox boundingBox
        ) {
            if (this.centerIndex == -1) {
                return;
            }

            if (metadata.equals("chest_1")) {
                switch (this.centerIndex) {
                    case 0:
                        this.placeChestWithLoot(world, boundingBox, random, pos,
                                Identifier.of("earth-and-water", "chests/center_1_chest_1"));
                        break;
                    case 1:
                        this.placeChestWithLoot(world, boundingBox, random, pos,
                                Identifier.of("earth-and-water", "chests/center_2_chest_1"));
                        break;
                    case 2:
                        this.placeChestWithLoot(world, boundingBox, random, pos,
                                Identifier.of("earth-and-water", "chests/center_3"));
                        break;
                }
            } else if (metadata.equals("chest_2")) {
                this.placeChestWithLoot(world, boundingBox, random, pos,
                        Identifier.of("earth-and-water", "chests/center_shared_chest_2"));
            } else if (metadata.startsWith("spawner_")) {
                String entityName = metadata.substring(8);
                this.setupSpawner(world, boundingBox, pos, entityName);
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

                        if (entityName.equals("brine")) {
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
                            spawnerEntity.markDirty();
                            ServerWorld serverWorld = world.toServerWorld();
                            BlockState newState = state
                                    .with(ReinforcedSpawnerBlock.ACTIVE, true)
                                    .with(ReinforcedSpawnerBlock.KEYHOLE, false);
                            serverWorld.setBlockState(spawnerPos, newState, 3);
                            serverWorld.updateListeners(spawnerPos, state, newState, 3);
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

        private void fillUnderStructure(StructureWorldAccess world, BlockBox chunkBox, Random random) {
            BlockPos structureStart = this.pos;
            BlockPos structureEnd = StructureTemplate.transformAround(
                    new BlockPos(this.template.getSize().getX() - 1, this.template.getSize().getY() - 1, this.template.getSize().getZ() - 1),
                    BlockMirror.NONE,
                    this.placementData.getRotation(),
                    BlockPos.ORIGIN
            ).add(this.pos);

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
                            !bottomState.isOf(Blocks.STRUCTURE_VOID);

                    if (hasBottomBlock && bottomState.isOf(Blocks.WATER)) {
                        hasBottomBlock = false;
                    }

                    if (hasBottomBlock) {
                        BlockPos belowPos = new BlockPos(x, minY - 1, z);
                        BlockState belowState = world.getBlockState(belowPos);

                        if (belowState.isAir() || belowState.getFluidState().isIn(FluidTags.WATER)) {
                            int terrainY = findTerrainHeight(world, x, z, minY - 1);

                            if (terrainY < minY - 1) {
                                createTaperedPillar(world, x, z, terrainY, minY - 1, random);
                            }
                        }
                    }
                }
            }
        }

        private int findTerrainHeight(StructureWorldAccess world, int x, int z, int startY) {
            for (int y = startY; y >= world.getBottomY(); y--) {
                BlockPos pos = new BlockPos(x, y, z);
                BlockState state = world.getBlockState(pos);

                if (!state.isAir() && !state.getFluidState().isIn(FluidTags.WATER)) {
                    return y;
                }
            }
            return world.getBottomY();
        }

        private void createTaperedPillar(StructureWorldAccess world, int centerX, int centerZ, int terrainY, int topY, Random random) {
            int pillarHeight = topY - terrainY;

            SimplexNoiseSampler noiseSampler = new SimplexNoiseSampler(random);
            float baseRadiusMultiplier = 2.0f + random.nextFloat() * 1.5f;
            float taperingCurve = 0.7f + random.nextFloat() * 0.6f;
            if (pillarHeight > 10) {
                baseRadiusMultiplier += (pillarHeight - 10) * 0.1f;
            }

            for (int y = terrainY + 1; y <= topY; y++) {
                float progress = (float)(y - terrainY) / (float)pillarHeight;

                double noiseScale = 0.1;
                double noise = noiseSampler.sample(centerX * noiseScale, y * noiseScale * 0.5, centerZ * noiseScale);
                float noiseOffset = (float)noise * 0.5f;
                float radiusFloat = (1.0f - (float)Math.pow(progress, taperingCurve)) * baseRadiusMultiplier + noiseOffset;
                int radius = Math.max(0, (int)Math.ceil(radiusFloat));

                for (int dx = -radius - 1; dx <= radius + 1; dx++) {
                    for (int dz = -radius - 1; dz <= radius + 1; dz++) {
                        int actualX = centerX + dx;
                        int actualZ = centerZ + dz;

                        double distance = Math.sqrt(dx * dx + dz * dz);
                        double edgeNoise = noiseSampler.sample(actualX * 0.3, y * 0.2, actualZ * 0.3);
                        float edgeVariation = (float)edgeNoise * 0.8f;

                        float threshold = radius + 0.5f + edgeVariation;

                        if (distance <= threshold) {
                            BlockPos fillPos = new BlockPos(actualX, y, actualZ);
                            BlockState currentState = world.getBlockState(fillPos);

                            if (currentState.isAir() || currentState.getFluidState().isIn(FluidTags.WATER)) {
                                BlockState blockToPlace = getTerrainMatchingBlock(world, centerX, centerZ, terrainY, random, y, terrainY);
                                world.setBlockState(fillPos, blockToPlace, 3);
                            }
                        }
                    }
                }
            }
        }

        private BlockState getTerrainMatchingBlock(StructureWorldAccess world, int x, int z, int terrainY, Random random, int currentY, int baseY) {
            BlockPos terrainPos = new BlockPos(x, terrainY, z);
            BlockState terrainBlock = world.getBlockState(terrainPos);

            int depthFromBase = currentY - baseY;
            float depthRatio = (float)depthFromBase / (float)(terrainY - baseY + 1);

            if (terrainBlock.isOf(Blocks.SAND) || terrainBlock.isOf(Blocks.SANDSTONE)) {
                if (depthRatio < 0.3f) {
                    return Blocks.SANDSTONE.getDefaultState();
                } else if (depthRatio < 0.6f) {
                    return random.nextFloat() < 0.5f ? Blocks.SANDSTONE.getDefaultState() : Blocks.SAND.getDefaultState();
                } else {
                    return Blocks.SAND.getDefaultState();
                }
            }

            if (terrainBlock.isOf(Blocks.STONE) || terrainBlock.isOf(Blocks.COBBLESTONE) ||
                    terrainBlock.isOf(Blocks.ANDESITE) || terrainBlock.isOf(Blocks.DIORITE) ||
                    terrainBlock.isOf(Blocks.GRANITE)) {

                if (depthRatio < 0.4f) {
                    return Blocks.STONE.getDefaultState();
                } else {
                    float r = random.nextFloat();
                    if (r < 0.4f) return Blocks.STONE.getDefaultState();
                    else if (r < 0.6f) return Blocks.ANDESITE.getDefaultState();
                    else if (r < 0.8f) return Blocks.COBBLESTONE.getDefaultState();
                    else return terrainBlock;
                }
            }

            if (terrainBlock.isOf(Blocks.DIRT) || terrainBlock.isOf(Blocks.GRASS_BLOCK) ||
                    terrainBlock.isOf(Blocks.COARSE_DIRT)) {

                if (depthRatio < 0.3f) {
                    return Blocks.STONE.getDefaultState();
                } else if (depthRatio < 0.6f) {
                    return random.nextFloat() < 0.5f ? Blocks.COARSE_DIRT.getDefaultState() : Blocks.DIRT.getDefaultState();
                } else {
                    return Blocks.DIRT.getDefaultState();
                }
            }

            if (terrainBlock.isOf(Blocks.GRAVEL)) {
                if (depthRatio < 0.4f) {
                    return random.nextFloat() < 0.6f ? Blocks.STONE.getDefaultState() : Blocks.GRAVEL.getDefaultState();
                } else {
                    return Blocks.GRAVEL.getDefaultState();
                }
            }

            if (depthRatio < 0.4f) {
                return Blocks.SANDSTONE.getDefaultState();
            } else {
                return Blocks.SAND.getDefaultState();
            }
        }

        private void spawnBrines(StructureWorldAccess world, Random random) {
            int brineCount = 2 + random.nextInt(1);

            BlockPos structureCenter = this.pos.add(
                    this.template.getSize().getX() / 2,
                    this.template.getSize().getY() / 2,
                    this.template.getSize().getZ() / 2
            );

            for (int i = 0; i < brineCount; i++) {
                int offsetX = random.nextInt(20) - 10;
                int offsetY = random.nextInt(10) - 5;
                int offsetZ = random.nextInt(20) - 10;

                BlockPos spawnPos = structureCenter.add(offsetX, offsetY, offsetZ);

                if (world.getFluidState(spawnPos).isIn(FluidTags.WATER)) {
                    BrineEntity brine = new BrineEntity(ModEntities.BRINE, world.toServerWorld());
                    if (brine != null) {
                        brine.refreshPositionAndAngles(spawnPos, 0.0F, 0.0F);
                        brine.initialize(world, world.getLocalDifficulty(spawnPos), SpawnReason.STRUCTURE, null);
                        brine.setHomePosition(structureCenter);
                        world.spawnEntity(brine);
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
            int i = world.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, this.pos.getX(), this.pos.getZ());
            this.pos = new BlockPos(this.pos.getX(), i, this.pos.getZ());
            super.generate(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pivot);
            fillUnderStructure(world, chunkBox, random);
            if (this.centerIndex != -1) {
                spawnBrines(world, random);
            }
            this.template.getInfosForBlock(this.pos, this.placementData, Blocks.STRUCTURE_BLOCK, false)
                    .forEach(structureBlockInfo -> {
                        if (structureBlockInfo.nbt() != null) {
                            String metadata = structureBlockInfo.nbt().getString("metadata", "");
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