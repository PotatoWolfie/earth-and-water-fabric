package potatowolfie.earth_and_water.block;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.block.custom.*;

import static net.minecraft.block.Blocks.*;

public class ModBlocks {
    public static final Block STEEL_BLOCK = registerBlock("steel_block",
            new Block(
                    AbstractBlock.Settings.create()
                            .mapColor(MapColor.LIGHT_GRAY)
                            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                            .requiresTool()
                            .strength(5.0F, 6.0F)
                            .sounds(BlockSoundGroup.METAL)
            ));

    public static final Block DRIPSTONE_PILLAR = registerBlock("dripstone_pillar",
            new PillarBlock(AbstractBlock.Settings.copy(DRIPSTONE_BLOCK)));
    public static final Block DARK_DRIPSTONE_PILLAR = registerBlock("dark_dripstone_pillar",
            new PillarBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.DRIPSTONE_BLOCK)
                    .strength(4f).requiresTool()));
    public static final Block DARK_PRISMARINE_PILLAR = registerBlock("dark_prismarine_pillar",
            new PillarBlock(AbstractBlock.Settings.copy(DARK_PRISMARINE)));
    public static final Block PRISMARINE_PILLAR = registerBlock("prismarine_pillar",
            new PillarBlock(AbstractBlock.Settings.copy(PRISMARINE)));

    public static final Block DRIPSTONE_BRICKS = registerBlock("dripstone_bricks",
            new Block(AbstractBlock.Settings.copy(DRIPSTONE_BLOCK)));
    public static final Block DRIPSTONE_BRICK_STAIRS = registerBlock("dripstone_brick_stairs",
            new StairsBlock(ModBlocks.DRIPSTONE_BRICKS.getDefaultState(),
                    AbstractBlock.Settings.copy(DRIPSTONE_BLOCK)));
    public static final Block DRIPSTONE_BRICK_SLAB = registerBlock("dripstone_brick_slab",
            new SlabBlock(AbstractBlock.Settings.copy(DRIPSTONE_BLOCK)));
    public static final Block DRIPSTONE_BRICK_WALL = registerBlock("dripstone_brick_wall",
            new WallBlock(AbstractBlock.Settings.copy(DRIPSTONE_BLOCK)));

    public static final Block POLISHED_DRIPSTONE = registerBlock("polished_dripstone",
            new Block(AbstractBlock.Settings.copy(DRIPSTONE_BLOCK)));
    public static final Block POLISHED_DRIPSTONE_STAIRS = registerBlock("polished_dripstone_stairs",
            new StairsBlock(ModBlocks.POLISHED_DRIPSTONE.getDefaultState(),
                    AbstractBlock.Settings.copy(DRIPSTONE_BLOCK)));
    public static final Block POLISHED_DRIPSTONE_SLAB = registerBlock("polished_dripstone_slab",
            new SlabBlock(AbstractBlock.Settings.copy(DRIPSTONE_BLOCK)));
    public static final Block POLISHED_DRIPSTONE_WALL = registerBlock("polished_dripstone_wall",
            new WallBlock(AbstractBlock.Settings.copy(DRIPSTONE_BLOCK)));

    public static final Block DRIPSTONE_STAIRS = registerBlock("dripstone_stairs",
            new StairsBlock(Blocks.DRIPSTONE_BLOCK.getDefaultState(),
                    AbstractBlock.Settings.copy(DRIPSTONE_BLOCK)));
    public static final Block DRIPSTONE_SLAB = registerBlock("dripstone_slab",
            new SlabBlock(AbstractBlock.Settings.copy(DRIPSTONE_BLOCK)));
    public static final Block DRIPSTONE_WALL = registerBlock("dripstone_wall",
            new WallBlock(AbstractBlock.Settings.copy(DRIPSTONE_BLOCK)));

    public static final Block CHISELED_DRIPSTONE_BRICKS = registerBlock("chiseled_dripstone_bricks",
            new ChiseledDripstoneBricksBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.TERRACOTTA_BROWN)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sounds(BlockSoundGroup.DRIPSTONE_BLOCK)
                    .requiresTool()
                    .strength(1.5F, 1.0F)
                    .luminance(state -> state.get(ChiseledDripstoneBricksBlock.POWERED) ? 12 : 0)));
    public static final Block CHISELED_DARK_DRIPSTONE_BRICKS = registerBlock("chiseled_dark_dripstone_bricks",
            new ChiseledDarkDripstoneBricksBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.DIAMOND_BLUE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresTool()
                    .strength(1.5F, 6.0F)
                    .luminance(state -> state.get(ChiseledDarkDripstoneBricksBlock.POWERED) ? 12 : 0)));

    public static final Block DARK_DRIPSTONE_BLOCK = registerBlock("dark_dripstone_block",
            new Block(AbstractBlock.Settings.copy(DRIPSTONE_BLOCK)));
    public static final Block DARK_DRIPSTONE_STAIRS = registerBlock("dark_dripstone_stairs",
            new StairsBlock(ModBlocks.DARK_DRIPSTONE_BLOCK.getDefaultState(),
                    AbstractBlock.Settings.copy(DRIPSTONE_BLOCK)));
    public static final Block DARK_DRIPSTONE_SLAB = registerBlock("dark_dripstone_slab",
            new SlabBlock(AbstractBlock.Settings.copy(DRIPSTONE_BLOCK)));
    public static final Block DARK_DRIPSTONE_WALL = registerBlock("dark_dripstone_wall",
            new WallBlock(AbstractBlock.Settings.copy(DRIPSTONE_BLOCK)));

    public static final Block POLISHED_DARK_DRIPSTONE = registerBlock("polished_dark_dripstone",
            new Block(AbstractBlock.Settings.copy(POLISHED_DRIPSTONE)));
    public static final Block POLISHED_DARK_DRIPSTONE_STAIRS = registerBlock("polished_dark_dripstone_stairs",
            new StairsBlock(ModBlocks.POLISHED_DARK_DRIPSTONE.getDefaultState(),
                    AbstractBlock.Settings.copy(POLISHED_DRIPSTONE)));
    public static final Block POLISHED_DARK_DRIPSTONE_SLAB = registerBlock("polished_dark_dripstone_slab",
            new SlabBlock(AbstractBlock.Settings.copy(POLISHED_DRIPSTONE)));
    public static final Block POLISHED_DARK_DRIPSTONE_WALL = registerBlock("polished_dark_dripstone_wall",
            new WallBlock(AbstractBlock.Settings.copy(POLISHED_DRIPSTONE)));

    public static final Block DARK_DRIPSTONE_BRICKS = registerBlock("dark_dripstone_bricks",
            new Block(AbstractBlock.Settings.copy(DRIPSTONE_BRICKS)));
    public static final Block DARK_DRIPSTONE_BRICK_STAIRS = registerBlock("dark_dripstone_brick_stairs",
            new StairsBlock(ModBlocks.DARK_DRIPSTONE_BRICKS.getDefaultState(),
                    AbstractBlock.Settings.copy(DRIPSTONE_BRICKS)));
    public static final Block DARK_DRIPSTONE_BRICK_SLAB = registerBlock("dark_dripstone_brick_slab",
            new SlabBlock(AbstractBlock.Settings.copy(DRIPSTONE_BRICKS)));
    public static final Block DARK_DRIPSTONE_BRICK_WALL = registerBlock("dark_dripstone_brick_wall",
            new WallBlock(AbstractBlock.Settings.copy(DRIPSTONE_BRICKS)));

    public static final Block POINTED_DARK_DRIPSTONE = registerBlock("pointed_dark_dripstone",
            new PointedDarkDripstoneBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.TERRACOTTA_BROWN)
                    .solid()
                    .requiresTool()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .nonOpaque()
                    .sounds(BlockSoundGroup.POINTED_DRIPSTONE)
                    .ticksRandomly()
                    .strength(1.5F, 3.0F)
                    .dynamicBounds()
                    .offset(AbstractBlock.OffsetType.XZ)
                    .pistonBehavior(PistonBehavior.DESTROY)
                    .solidBlock(Blocks::never)));

    public static final Block CHISELED_PRISMARINE_BRICKS = registerBlock("chiseled_prismarine_bricks",
            new ChiseledPrismarineBricksBlock(AbstractBlock.Settings.create().mapColor(MapColor.CYAN)
                    .instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)
                    .luminance(state -> state.get(ChiseledPrismarineBricksBlock.ACTIVE) ? 12 : 0)));
    public static final Block PRISMARINE_TILES = registerBlock("prismarine_tiles",
            new Block(AbstractBlock.Settings.copy(PRISMARINE)));
    public static final Block PRISMARINE_TILE_STAIRS = registerBlock("prismarine_tile_stairs",
            new StairsBlock(ModBlocks.PRISMARINE_TILES.getDefaultState(),
                    AbstractBlock.Settings.copy(PRISMARINE)));
    public static final Block PRISMARINE_TILE_SLAB = registerBlock("prismarine_tile_slab",
            new SlabBlock(AbstractBlock.Settings.copy(PRISMARINE)));
    public static final Block PRISMARINE_TILE_WALL = registerBlock("prismarine_tile_wall",
            new WallBlock(AbstractBlock.Settings.copy(PRISMARINE)));

    public static final Block MIXED_PRISMARINE_TILES = registerBlock("mixed_prismarine_tiles",
            new Block(AbstractBlock.Settings.copy(PRISMARINE)));
    public static final Block POLISHED_DRIPSTONE_TILES = registerBlock("polished_dripstone_tiles",
            new Block(AbstractBlock.Settings.copy(POLISHED_DARK_DRIPSTONE)));
    public static final Block CHISELED_DARK_PRISMARINE = registerBlock("chiseled_dark_prismarine",
            new ChiseledDarkPrismarineBlock(AbstractBlock.Settings.create().mapColor(MapColor.DIAMOND_BLUE)
                    .instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F)));
    public static final Block DARK_PRISMARINE_WALL = registerBlock("dark_prismarine_wall",
            new WallBlock(AbstractBlock.Settings.copy(DARK_PRISMARINE)));

    public static final Block LIMESTONE = registerBlock("limestone",
            new Block(AbstractBlock.Settings.copy(TUFF)));
    public static final Block LIMESTONE_STAIRS = registerBlock("limestone_stairs",
            new StairsBlock(ModBlocks.LIMESTONE.getDefaultState(),
                    AbstractBlock.Settings.copy(TUFF)));
    public static final Block LIMESTONE_SLAB = registerBlock("limestone_slab",
            new SlabBlock(AbstractBlock.Settings.copy(TUFF)));
    public static final Block LIMESTONE_WALL = registerBlock("limestone_wall",
            new WallBlock(AbstractBlock.Settings.copy(TUFF)));
    public static final Block POLISHED_LIMESTONE = registerBlock("polished_limestone",
            new Block(AbstractBlock.Settings.copy(POLISHED_TUFF)));
    public static final Block POLISHED_LIMESTONE_STAIRS = registerBlock("polished_limestone_stairs",
            new StairsBlock(ModBlocks.POLISHED_LIMESTONE.getDefaultState(),
                    AbstractBlock.Settings.copy(POLISHED_TUFF)));
    public static final Block POLISHED_LIMESTONE_SLAB = registerBlock("polished_limestone_slab",
            new SlabBlock(AbstractBlock.Settings.copy(POLISHED_TUFF)));
    public static final Block POLISHED_LIMESTONE_WALL = registerBlock("polished_limestone_wall",
            new WallBlock(AbstractBlock.Settings.copy(POLISHED_TUFF)));
    public static final Block LIMESTONE_BRICKS = registerBlock("limestone_bricks",
            new Block(AbstractBlock.Settings.copy(TUFF_BRICKS)));
    public static final Block LIMESTONE_BRICK_STAIRS = registerBlock("limestone_brick_stairs",
            new StairsBlock(ModBlocks.LIMESTONE_BRICKS.getDefaultState(),
                    AbstractBlock.Settings.copy(TUFF_BRICKS)));
    public static final Block LIMESTONE_BRICK_SLAB = registerBlock("limestone_brick_slab",
            new SlabBlock(AbstractBlock.Settings.copy(TUFF_BRICKS)));
    public static final Block LIMESTONE_BRICK_WALL = registerBlock("limestone_brick_wall",
            new WallBlock(AbstractBlock.Settings.copy(TUFF_BRICKS)));
    public static final Block LIMESTONE_PILLAR = registerBlock("limestone_pillar",
            new PillarBlock(AbstractBlock.Settings.copy(LIMESTONE)));
    public static final Block CHISELED_LIMESTONE_BRICKS = registerBlock("chiseled_limestone_bricks",
            new Block(AbstractBlock.Settings.copy(LIMESTONE_BRICKS)));

    public static final Block OXYGEN_BLOCK = registerBlock("oxygen_block",
            new OxygenBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.DARK_RED)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresTool()
                    .luminance(state -> 3)
                    .strength(0.5F)
                    .allowsSpawning((state, world, pos, entityType) -> entityType.isFireImmune())
                    .postProcess(Blocks::always)
                    .emissiveLighting(Blocks::always)));

    public static final Block OXYGEN_BUBBLE = registerBlock("oxygen_bubble",
            new OxygenBubbleBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.WATER_BLUE)
                    .replaceable()
                    .noCollision()
                    .dropsNothing()
                    .pistonBehavior(PistonBehavior.DESTROY)
                    .liquid()
                    .sounds(BlockSoundGroup.INTENTIONALLY_EMPTY)));

    public static final Block REINFORCED_SPAWNER = registerBlock("reinforced_spawner",
            new ReinforcedSpawnerBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.STONE_GRAY)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .strength(50.0F)
                    .sounds(BlockSoundGroup.TRIAL_SPAWNER)
                    .blockVision(Blocks::never)
                    .nonOpaque()
                    .pistonBehavior(PistonBehavior.BLOCK)
                    .luminance(state -> state.get(ReinforcedSpawnerBlock.ACTIVE) ? 4 : 0)
            )
    );

    private static RegistryKey<Block> createBlockRegistryKey(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(EarthWater.MOD_ID, name));
    }

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, createBlockRegistryKey(name), block);
    }

    private static void customBuildingBlocks(FabricItemGroupEntries entries) {
        entries.addBefore(CHAIN, STEEL_BLOCK);
        entries.addAfter(CHISELED_TUFF_BRICKS, DRIPSTONE_BLOCK);
        entries.addAfter(DRIPSTONE_BLOCK, DRIPSTONE_STAIRS);
        entries.addAfter(DRIPSTONE_STAIRS, DRIPSTONE_SLAB);
        entries.addAfter(DRIPSTONE_SLAB, DRIPSTONE_WALL);
        entries.addAfter(DRIPSTONE_WALL, POLISHED_DRIPSTONE);
        entries.addAfter(POLISHED_DRIPSTONE, POLISHED_DRIPSTONE_STAIRS);
        entries.addAfter(POLISHED_DRIPSTONE_STAIRS, POLISHED_DRIPSTONE_SLAB);
        entries.addAfter(POLISHED_DRIPSTONE_SLAB, POLISHED_DRIPSTONE_WALL);
        entries.addAfter(POLISHED_DRIPSTONE_WALL, DRIPSTONE_BRICKS);
        entries.addAfter(DRIPSTONE_BRICKS, DRIPSTONE_BRICK_STAIRS);
        entries.addAfter(DRIPSTONE_BRICK_STAIRS, DRIPSTONE_BRICK_SLAB);
        entries.addAfter(DRIPSTONE_BRICK_SLAB, DRIPSTONE_BRICK_WALL);
        entries.addAfter(DRIPSTONE_BRICK_WALL, CHISELED_DRIPSTONE_BRICKS);
        entries.addAfter(CHISELED_DRIPSTONE_BRICKS, DRIPSTONE_PILLAR);
        entries.addAfter(DRIPSTONE_PILLAR, POLISHED_DRIPSTONE_TILES);
        entries.addAfter(POLISHED_DRIPSTONE_TILES, DARK_DRIPSTONE_BLOCK);
        entries.addAfter(DARK_DRIPSTONE_BLOCK, DARK_DRIPSTONE_STAIRS);
        entries.addAfter(DARK_DRIPSTONE_STAIRS, DARK_DRIPSTONE_SLAB);
        entries.addAfter(DARK_DRIPSTONE_SLAB, DARK_DRIPSTONE_WALL);
        entries.addAfter(DARK_DRIPSTONE_WALL, POLISHED_DARK_DRIPSTONE);
        entries.addAfter(POLISHED_DARK_DRIPSTONE, POLISHED_DARK_DRIPSTONE_STAIRS);
        entries.addAfter(POLISHED_DARK_DRIPSTONE_STAIRS, POLISHED_DARK_DRIPSTONE_SLAB);
        entries.addAfter(POLISHED_DARK_DRIPSTONE_SLAB, POLISHED_DARK_DRIPSTONE_WALL);
        entries.addAfter(POLISHED_DARK_DRIPSTONE_WALL, DARK_DRIPSTONE_BRICKS);
        entries.addAfter(DARK_DRIPSTONE_BRICKS, DARK_DRIPSTONE_BRICK_STAIRS);
        entries.addAfter(DARK_DRIPSTONE_BRICK_STAIRS, DARK_DRIPSTONE_BRICK_SLAB);
        entries.addAfter(DARK_DRIPSTONE_BRICK_SLAB, DARK_DRIPSTONE_BRICK_WALL);
        entries.addAfter(DARK_DRIPSTONE_BRICK_WALL, CHISELED_DARK_DRIPSTONE_BRICKS);
        entries.addAfter(CHISELED_DARK_DRIPSTONE_BRICKS, DARK_DRIPSTONE_PILLAR);
        entries.addAfter(DARK_DRIPSTONE_PILLAR, LIMESTONE);
        entries.addAfter(LIMESTONE, LIMESTONE_STAIRS);
        entries.addAfter(LIMESTONE_STAIRS, LIMESTONE_SLAB);
        entries.addAfter(LIMESTONE_SLAB, LIMESTONE_WALL);
        entries.addAfter(LIMESTONE_WALL, POLISHED_LIMESTONE);
        entries.addAfter(POLISHED_LIMESTONE, POLISHED_LIMESTONE_STAIRS);
        entries.addAfter(POLISHED_LIMESTONE_STAIRS, POLISHED_LIMESTONE_SLAB);
        entries.addAfter(POLISHED_LIMESTONE_SLAB, POLISHED_LIMESTONE_WALL);
        entries.addAfter(POLISHED_LIMESTONE_WALL, LIMESTONE_BRICKS);
        entries.addAfter(LIMESTONE_BRICKS, LIMESTONE_BRICK_STAIRS);
        entries.addAfter(LIMESTONE_BRICK_STAIRS, LIMESTONE_BRICK_SLAB);
        entries.addAfter(LIMESTONE_BRICK_SLAB, LIMESTONE_BRICK_WALL);
        entries.addAfter(LIMESTONE_BRICK_WALL, CHISELED_LIMESTONE_BRICKS);
        entries.addAfter(CHISELED_LIMESTONE_BRICKS, LIMESTONE_PILLAR);
        entries.addAfter(PRISMARINE_BRICK_SLAB, CHISELED_PRISMARINE_BRICKS);
        entries.addAfter(CHISELED_PRISMARINE_BRICKS, PRISMARINE_TILES);
        entries.addAfter(PRISMARINE_TILES, PRISMARINE_TILE_STAIRS);
        entries.addAfter(PRISMARINE_TILE_STAIRS, PRISMARINE_TILE_SLAB);
        entries.addAfter(PRISMARINE_TILE_SLAB, PRISMARINE_TILE_WALL);
        entries.addAfter(PRISMARINE_TILE_WALL, MIXED_PRISMARINE_TILES);
        entries.addAfter(PRISMARINE, PRISMARINE_PILLAR);
        entries.addAfter(DARK_PRISMARINE_SLAB, DARK_PRISMARINE_WALL);
        entries.addAfter(DARK_PRISMARINE_WALL, DARK_PRISMARINE_PILLAR);
        entries.addAfter(DARK_PRISMARINE_PILLAR, CHISELED_DARK_PRISMARINE);
    }

    private static void customNaturalBlocks(FabricItemGroupEntries entries) {
        entries.addAfter(MAGMA_BLOCK, OXYGEN_BLOCK);
        entries.addAfter(POINTED_DRIPSTONE, DARK_DRIPSTONE_BLOCK);
        entries.addAfter(DARK_DRIPSTONE_BLOCK, POINTED_DARK_DRIPSTONE);
        entries.addAfter(POINTED_DARK_DRIPSTONE, LIMESTONE);
    }

    private static void customSpawnEggs(FabricItemGroupEntries entries) {
        entries.addAfter(TRIAL_SPAWNER, REINFORCED_SPAWNER);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(EarthWater.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks () {
        EarthWater.LOGGER.info("Registering Mod Blocks for " + EarthWater.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(ModBlocks::customBuildingBlocks);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(ModBlocks::customNaturalBlocks);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(ModBlocks::customSpawnEggs);
    }
}