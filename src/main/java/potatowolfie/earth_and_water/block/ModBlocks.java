package potatowolfie.earth_and_water.block;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.block.custom.*;

import java.util.List;

import static net.minecraft.world.level.block.Blocks.*;

public class ModBlocks {
    public static final Block STEEL_BLOCK = registerBlock("steel_block",
            new Block(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_LIGHT_GRAY)
                            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                            .requiresCorrectToolForDrops()
                            .strength(5.0F, 6.0F)
                            .sound(SoundType.METAL)
                            .setId(createBlockRegistryKey("steel_block"))
            ));

    public static final Block DRIPSTONE_PILLAR = registerBlock("dripstone_pillar",
            new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(DRIPSTONE_BLOCK)
                    .setId(createBlockRegistryKey("dripstone_pillar"))));
    public static final Block DARK_DRIPSTONE_PILLAR = registerBlock("dark_dripstone_pillar",
            new RotatedPillarBlock(BlockBehaviour.Properties.of().sound(SoundType.DRIPSTONE_BLOCK)
                    .strength(4f).requiresCorrectToolForDrops()
                    .setId(createBlockRegistryKey("dark_dripstone_pillar"))));
    public static final Block DARK_PRISMARINE_PILLAR = registerBlock("dark_prismarine_pillar",
            new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(DARK_PRISMARINE)
                    .setId(createBlockRegistryKey("dark_prismarine_pillar"))));
    public static final Block PRISMARINE_PILLAR = registerBlock("prismarine_pillar",
            new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(PRISMARINE)
                    .setId(createBlockRegistryKey("prismarine_pillar"))));

    public static final Block DRIPSTONE_BRICKS = registerBlock("dripstone_bricks",
            new Block(BlockBehaviour.Properties.ofFullCopy(DRIPSTONE_BLOCK)
                    .setId(createBlockRegistryKey("dripstone_bricks"))));
    public static final Block DRIPSTONE_BRICK_STAIRS = registerBlock("dripstone_brick_stairs",
            new StairBlock(ModBlocks.DRIPSTONE_BRICKS.defaultBlockState(),
                    BlockBehaviour.Properties.ofFullCopy(DRIPSTONE_BLOCK)
                            .setId(createBlockRegistryKey("dripstone_brick_stairs"))));
    public static final Block DRIPSTONE_BRICK_SLAB = registerBlock("dripstone_brick_slab",
            new SlabBlock(BlockBehaviour.Properties.ofFullCopy(DRIPSTONE_BLOCK)
                    .setId(createBlockRegistryKey("dripstone_brick_slab"))));
    public static final Block DRIPSTONE_BRICK_WALL = registerBlock("dripstone_brick_wall",
            new WallBlock(BlockBehaviour.Properties.ofFullCopy(DRIPSTONE_BLOCK)
                    .setId(createBlockRegistryKey("dripstone_brick_wall"))));

    public static final Block POLISHED_DRIPSTONE = registerBlock("polished_dripstone",
            new Block(BlockBehaviour.Properties.ofFullCopy(DRIPSTONE_BLOCK)
                    .setId(createBlockRegistryKey("polished_dripstone"))));
    public static final Block POLISHED_DRIPSTONE_STAIRS = registerBlock("polished_dripstone_stairs",
            new StairBlock(ModBlocks.POLISHED_DRIPSTONE.defaultBlockState(),
                    BlockBehaviour.Properties.ofFullCopy(DRIPSTONE_BLOCK)
                            .setId(createBlockRegistryKey("polished_dripstone_stairs"))));
    public static final Block POLISHED_DRIPSTONE_SLAB = registerBlock("polished_dripstone_slab",
            new SlabBlock(BlockBehaviour.Properties.ofFullCopy(DRIPSTONE_BLOCK)
                    .setId(createBlockRegistryKey("polished_dripstone_slab"))));
    public static final Block POLISHED_DRIPSTONE_WALL = registerBlock("polished_dripstone_wall",
            new WallBlock(BlockBehaviour.Properties.ofFullCopy(DRIPSTONE_BLOCK)
                    .setId(createBlockRegistryKey("polished_dripstone_wall"))));

    public static final Block DRIPSTONE_STAIRS = registerBlock("dripstone_stairs",
            new StairBlock(Blocks.DRIPSTONE_BLOCK.defaultBlockState(),
                    BlockBehaviour.Properties.ofFullCopy(DRIPSTONE_BLOCK)
                            .setId(createBlockRegistryKey("dripstone_stairs"))));
    public static final Block DRIPSTONE_SLAB = registerBlock("dripstone_slab",
            new SlabBlock(BlockBehaviour.Properties.ofFullCopy(DRIPSTONE_BLOCK)
                    .setId(createBlockRegistryKey("dripstone_slab"))));
    public static final Block DRIPSTONE_WALL = registerBlock("dripstone_wall",
            new WallBlock(BlockBehaviour.Properties.ofFullCopy(DRIPSTONE_BLOCK)
                    .setId(createBlockRegistryKey("dripstone_wall"))));

    public static final Block CHISELED_DRIPSTONE_BRICKS = registerBlock("chiseled_dripstone_bricks",
            new ChiseledDripstoneBricksBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_BROWN)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.DRIPSTONE_BLOCK)
                    .requiresCorrectToolForDrops()
                    .strength(1.5F, 1.0F)
                    .lightLevel(state -> state.getValue(ChiseledDripstoneBricksBlock.POWERED) ? 12 : 0)
                    .setId(createBlockRegistryKey("chiseled_dripstone_bricks"))));
    public static final Block CHISELED_DARK_DRIPSTONE_BRICKS = registerBlock("chiseled_dark_dripstone_bricks",
            new ChiseledDarkDripstoneBricksBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIAMOND)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(1.5F, 6.0F)
                    .lightLevel(state -> state.getValue(ChiseledDarkDripstoneBricksBlock.POWERED) ? 12 : 0)
                    .setId(createBlockRegistryKey("chiseled_dark_dripstone_bricks"))));

    public static final Block DARK_DRIPSTONE_BLOCK = registerBlock("dark_dripstone_block",
            new Block(BlockBehaviour.Properties.ofFullCopy(DRIPSTONE_BLOCK)
                    .setId(createBlockRegistryKey("dark_dripstone_block"))));
    public static final Block DARK_DRIPSTONE_STAIRS = registerBlock("dark_dripstone_stairs",
            new StairBlock(ModBlocks.DARK_DRIPSTONE_BLOCK.defaultBlockState(),
                    BlockBehaviour.Properties.ofFullCopy(DRIPSTONE_BLOCK)
                            .setId(createBlockRegistryKey("dark_dripstone_stairs"))));
    public static final Block DARK_DRIPSTONE_SLAB = registerBlock("dark_dripstone_slab",
            new SlabBlock(BlockBehaviour.Properties.ofFullCopy(DRIPSTONE_BLOCK)
                    .setId(createBlockRegistryKey("dark_dripstone_slab"))));
    public static final Block DARK_DRIPSTONE_WALL = registerBlock("dark_dripstone_wall",
            new WallBlock(BlockBehaviour.Properties.ofFullCopy(DRIPSTONE_BLOCK)
                    .setId(createBlockRegistryKey("dark_dripstone_wall"))));

    public static final Block POLISHED_DARK_DRIPSTONE = registerBlock("polished_dark_dripstone",
            new Block(BlockBehaviour.Properties.ofFullCopy(POLISHED_DRIPSTONE)
                    .setId(createBlockRegistryKey("polished_dark_dripstone"))));
    public static final Block POLISHED_DARK_DRIPSTONE_STAIRS = registerBlock("polished_dark_dripstone_stairs",
            new StairBlock(ModBlocks.POLISHED_DARK_DRIPSTONE.defaultBlockState(),
                    BlockBehaviour.Properties.ofFullCopy(POLISHED_DRIPSTONE)
                            .setId(createBlockRegistryKey("polished_dark_dripstone_stairs"))));
    public static final Block POLISHED_DARK_DRIPSTONE_SLAB = registerBlock("polished_dark_dripstone_slab",
            new SlabBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_DRIPSTONE)
                    .setId(createBlockRegistryKey("polished_dark_dripstone_slab"))));
    public static final Block POLISHED_DARK_DRIPSTONE_WALL = registerBlock("polished_dark_dripstone_wall",
            new WallBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_DRIPSTONE)
                    .setId(createBlockRegistryKey("polished_dark_dripstone_wall"))));

    public static final Block DARK_DRIPSTONE_BRICKS = registerBlock("dark_dripstone_bricks",
            new Block(BlockBehaviour.Properties.ofFullCopy(DRIPSTONE_BRICKS)
                    .setId(createBlockRegistryKey("dark_dripstone_bricks"))));
    public static final Block DARK_DRIPSTONE_BRICK_STAIRS = registerBlock("dark_dripstone_brick_stairs",
            new StairBlock(ModBlocks.DARK_DRIPSTONE_BRICKS.defaultBlockState(),
                    BlockBehaviour.Properties.ofFullCopy(DRIPSTONE_BRICKS)
                            .setId(createBlockRegistryKey("dark_dripstone_brick_stairs"))));
    public static final Block DARK_DRIPSTONE_BRICK_SLAB = registerBlock("dark_dripstone_brick_slab",
            new SlabBlock(BlockBehaviour.Properties.ofFullCopy(DRIPSTONE_BRICKS)
                    .setId(createBlockRegistryKey("dark_dripstone_brick_slab"))));
    public static final Block DARK_DRIPSTONE_BRICK_WALL = registerBlock("dark_dripstone_brick_wall",
            new WallBlock(BlockBehaviour.Properties.ofFullCopy(DRIPSTONE_BRICKS)
                    .setId(createBlockRegistryKey("dark_dripstone_brick_wall"))));

    public static final Block POINTED_DARK_DRIPSTONE = registerBlock(
            "pointed_dark_dripstone",
            new PointedDarkDripstoneBlock(DRIPSTONE_BRICKS.defaultBlockState(),
                    BlockBehaviour.Properties.ofFullCopy(POINTED_DRIPSTONE)
                            .setId(createBlockRegistryKey("pointed_dark_dripstone"))
            )
    );

    public static final Block CHISELED_PRISMARINE_BRICKS = registerBlock("chiseled_prismarine_bricks",
            new ChiseledPrismarineBricksBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN)
                    .instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F)
                    .lightLevel(state -> state.getValue(ChiseledPrismarineBricksBlock.ACTIVE) ? 12 : 0)
                    .setId(createBlockRegistryKey("chiseled_prismarine_bricks"))));
    public static final Block PRISMARINE_TILES = registerBlock("prismarine_tiles",
            new Block(BlockBehaviour.Properties.ofFullCopy(PRISMARINE)
                    .setId(createBlockRegistryKey("prismarine_tiles"))));
    public static final Block PRISMARINE_TILE_STAIRS = registerBlock("prismarine_tile_stairs",
            new StairBlock(ModBlocks.PRISMARINE_TILES.defaultBlockState(),
                    BlockBehaviour.Properties.ofFullCopy(PRISMARINE)
                            .setId(createBlockRegistryKey("prismarine_tile_stairs"))));
    public static final Block PRISMARINE_TILE_SLAB = registerBlock("prismarine_tile_slab",
            new SlabBlock(BlockBehaviour.Properties.ofFullCopy(PRISMARINE)
                    .setId(createBlockRegistryKey("prismarine_tile_slab"))));
    public static final Block PRISMARINE_TILE_WALL = registerBlock("prismarine_tile_wall",
            new WallBlock(BlockBehaviour.Properties.ofFullCopy(PRISMARINE)
                    .setId(createBlockRegistryKey("prismarine_tile_wall"))));

    public static final Block MIXED_PRISMARINE_TILES = registerBlock("mixed_prismarine_tiles",
            new Block(BlockBehaviour.Properties.ofFullCopy(PRISMARINE)
                    .setId(createBlockRegistryKey("mixed_prismarine_tiles"))));
    public static final Block POLISHED_DRIPSTONE_TILES = registerBlock("polished_dripstone_tiles",
            new Block(BlockBehaviour.Properties.ofFullCopy(POLISHED_DARK_DRIPSTONE)
                    .setId(createBlockRegistryKey("polished_dripstone_tiles"))));
    public static final Block CHISELED_DARK_PRISMARINE = registerBlock("chiseled_dark_prismarine",
            new ChiseledDarkPrismarineBlock(BlockBehaviour.Properties.of().mapColor(MapColor.DIAMOND)
                    .instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F)
                    .setId(createBlockRegistryKey("chiseled_dark_prismarine"))));
    public static final Block DARK_PRISMARINE_WALL = registerBlock("dark_prismarine_wall",
            new WallBlock(BlockBehaviour.Properties.ofFullCopy(DARK_PRISMARINE)
                    .setId(createBlockRegistryKey("dark_prismarine_wall"))));

    public static final Block LIMESTONE = registerBlock("limestone",
            new Block(BlockBehaviour.Properties.ofFullCopy(TUFF)
                    .setId(createBlockRegistryKey("limestone"))));
    public static final Block LIMESTONE_STAIRS = registerBlock("limestone_stairs",
            new StairBlock(ModBlocks.LIMESTONE.defaultBlockState(),
                    BlockBehaviour.Properties.ofFullCopy(TUFF)
                            .setId(createBlockRegistryKey("limestone_stairs"))));
    public static final Block LIMESTONE_SLAB = registerBlock("limestone_slab",
            new SlabBlock(BlockBehaviour.Properties.ofFullCopy(TUFF)
                    .setId(createBlockRegistryKey("limestone_slab"))));
    public static final Block LIMESTONE_WALL = registerBlock("limestone_wall",
            new WallBlock(BlockBehaviour.Properties.ofFullCopy(TUFF)
                    .setId(createBlockRegistryKey("limestone_wall"))));
    public static final Block POLISHED_LIMESTONE = registerBlock("polished_limestone",
            new Block(BlockBehaviour.Properties.ofFullCopy(POLISHED_TUFF)
                    .setId(createBlockRegistryKey("polished_limestone"))));
    public static final Block POLISHED_LIMESTONE_STAIRS = registerBlock("polished_limestone_stairs",
            new StairBlock(ModBlocks.POLISHED_LIMESTONE.defaultBlockState(),
                    BlockBehaviour.Properties.ofFullCopy(POLISHED_TUFF)
                            .setId(createBlockRegistryKey("polished_limestone_stairs"))));
    public static final Block POLISHED_LIMESTONE_SLAB = registerBlock("polished_limestone_slab",
            new SlabBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_TUFF)
                    .setId(createBlockRegistryKey("polished_limestone_slab"))));
    public static final Block POLISHED_LIMESTONE_WALL = registerBlock("polished_limestone_wall",
            new WallBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_TUFF)
                    .setId(createBlockRegistryKey("polished_limestone_wall"))));
    public static final Block LIMESTONE_BRICKS = registerBlock("limestone_bricks",
            new Block(BlockBehaviour.Properties.ofFullCopy(TUFF_BRICKS)
                    .setId(createBlockRegistryKey("limestone_bricks"))));
    public static final Block LIMESTONE_BRICK_STAIRS = registerBlock("limestone_brick_stairs",
            new StairBlock(ModBlocks.LIMESTONE_BRICKS.defaultBlockState(),
                    BlockBehaviour.Properties.ofFullCopy(TUFF_BRICKS)
                            .setId(createBlockRegistryKey("limestone_brick_stairs"))));
    public static final Block LIMESTONE_BRICK_SLAB = registerBlock("limestone_brick_slab",
            new SlabBlock(BlockBehaviour.Properties.ofFullCopy(TUFF_BRICKS)
                    .setId(createBlockRegistryKey("limestone_brick_slab"))));
    public static final Block LIMESTONE_BRICK_WALL = registerBlock("limestone_brick_wall",
            new WallBlock(BlockBehaviour.Properties.ofFullCopy(TUFF_BRICKS)
                    .setId(createBlockRegistryKey("limestone_brick_wall"))));
    public static final Block LIMESTONE_PILLAR = registerBlock("limestone_pillar",
            new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(LIMESTONE)
                    .setId(createBlockRegistryKey("limestone_pillar"))));
    public static final Block CHISELED_LIMESTONE_BRICKS = registerBlock("chiseled_limestone_bricks",
            new Block(BlockBehaviour.Properties.ofFullCopy(LIMESTONE_BRICKS)
                    .setId(createBlockRegistryKey("chiseled_limestone_bricks"))));

    public static final Block OXYGEN_BLOCK = registerBlock("oxygen_block",
            new OxygenBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.NETHER)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 3)
                    .strength(0.5F)
                    .isValidSpawn((state, world, pos, entityType) -> entityType.fireImmune())
                    .emissiveRendering(state -> true)
                    .setId(createBlockRegistryKey("oxygen_block"))));

    public static final Block OXYGEN_BUBBLE = registerBlock("oxygen_bubble",
            new OxygenBubbleBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WATER)
                    .replaceable()
                    .noCollision()
                    .noLootTable()
                    .pushReaction(PushReaction.POPPED)
                    .liquid()
                    .sound(SoundType.EMPTY)
                    .setId(createBlockRegistryKey("oxygen_bubble"))));

    public static final Block REINFORCED_SPAWNER = registerBlock("reinforced_spawner",
            new ReinforcedSpawnerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .strength(50.0F)
                    .sound(SoundType.TRIAL_SPAWNER)
                    .isViewBlocking((state, level, pos, nearPlaneBox) -> false)
                    .noOcclusion()
                    .pushReaction(PushReaction.IMMOVEABLE)
                    .lightLevel(state -> state.getValue(ReinforcedSpawnerBlock.ACTIVE) ? 4 : 0)
                    .setId(createBlockRegistryKey("reinforced_spawner"))
            )
    );

    private static ResourceKey<Block> createBlockRegistryKey(String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(EarthWater.MOD_ID, name));
    }

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(BuiltInRegistries.BLOCK, createBlockRegistryKey(name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(EarthWater.MOD_ID, name));
        Registry.register(BuiltInRegistries.ITEM, itemKey,
                new BlockItem(block, new Item.Properties()
                        .useBlockDescriptionPrefix()
                        .setId(itemKey)));
    }

    public static void registerModBlocks () {
        EarthWater.LOGGER.info("Registering Mod Blocks for " + EarthWater.MOD_ID);

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS)
                .register(output -> {

                    output.insertBefore(Items.IRON_CHAIN, List.of(
                            new ItemStack(STEEL_BLOCK)
                    ), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

                    output.insertAfter(Items.CHISELED_TUFF_BRICKS, List.of(
                            new ItemStack(DRIPSTONE_BLOCK),
                            new ItemStack(DRIPSTONE_STAIRS),
                            new ItemStack(DRIPSTONE_SLAB),
                            new ItemStack(DRIPSTONE_WALL),
                            new ItemStack(POLISHED_DRIPSTONE),
                            new ItemStack(POLISHED_DRIPSTONE_STAIRS),
                            new ItemStack(POLISHED_DRIPSTONE_SLAB),
                            new ItemStack(POLISHED_DRIPSTONE_WALL),
                            new ItemStack(DRIPSTONE_BRICKS),
                            new ItemStack(DRIPSTONE_BRICK_STAIRS),
                            new ItemStack(DRIPSTONE_BRICK_SLAB),
                            new ItemStack(DRIPSTONE_BRICK_WALL),
                            new ItemStack(CHISELED_DRIPSTONE_BRICKS),
                            new ItemStack(DRIPSTONE_PILLAR),
                            new ItemStack(POLISHED_DRIPSTONE_TILES),
                            new ItemStack(DARK_DRIPSTONE_BLOCK),
                            new ItemStack(DARK_DRIPSTONE_STAIRS),
                            new ItemStack(DARK_DRIPSTONE_SLAB),
                            new ItemStack(DARK_DRIPSTONE_WALL),
                            new ItemStack(POLISHED_DARK_DRIPSTONE),
                            new ItemStack(POLISHED_DARK_DRIPSTONE_STAIRS),
                            new ItemStack(POLISHED_DARK_DRIPSTONE_SLAB),
                            new ItemStack(POLISHED_DARK_DRIPSTONE_WALL),
                            new ItemStack(DARK_DRIPSTONE_BRICKS),
                            new ItemStack(DARK_DRIPSTONE_BRICK_STAIRS),
                            new ItemStack(DARK_DRIPSTONE_BRICK_SLAB),
                            new ItemStack(DARK_DRIPSTONE_BRICK_WALL),
                            new ItemStack(CHISELED_DARK_DRIPSTONE_BRICKS),
                            new ItemStack(DARK_DRIPSTONE_PILLAR),
                            new ItemStack(LIMESTONE),
                            new ItemStack(LIMESTONE_STAIRS),
                            new ItemStack(LIMESTONE_SLAB),
                            new ItemStack(LIMESTONE_WALL),
                            new ItemStack(POLISHED_LIMESTONE),
                            new ItemStack(POLISHED_LIMESTONE_STAIRS),
                            new ItemStack(POLISHED_LIMESTONE_SLAB),
                            new ItemStack(POLISHED_LIMESTONE_WALL),
                            new ItemStack(LIMESTONE_BRICKS),
                            new ItemStack(LIMESTONE_BRICK_STAIRS),
                            new ItemStack(LIMESTONE_BRICK_SLAB),
                            new ItemStack(LIMESTONE_BRICK_WALL),
                            new ItemStack(CHISELED_LIMESTONE_BRICKS),
                            new ItemStack(LIMESTONE_PILLAR)
                    ), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

                    output.insertAfter(Items.PRISMARINE_BRICK_SLAB, List.of(
                            new ItemStack(CHISELED_PRISMARINE_BRICKS),
                            new ItemStack(PRISMARINE_TILES),
                            new ItemStack(PRISMARINE_TILE_STAIRS),
                            new ItemStack(PRISMARINE_TILE_SLAB),
                            new ItemStack(PRISMARINE_TILE_WALL),
                            new ItemStack(MIXED_PRISMARINE_TILES)
                    ), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

                    output.insertAfter(Items.PRISMARINE, List.of(
                            new ItemStack(PRISMARINE_PILLAR)
                    ), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

                    output.insertAfter(Items.DARK_PRISMARINE_SLAB, List.of(
                            new ItemStack(DARK_PRISMARINE_WALL),
                            new ItemStack(DARK_PRISMARINE_PILLAR),
                            new ItemStack(CHISELED_DARK_PRISMARINE)
                    ), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS)
                .register(output -> {

                    output.insertAfter(Items.MAGMA_BLOCK, List.of(
                            new ItemStack(OXYGEN_BLOCK)
                    ), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

                    output.insertAfter(Items.POINTED_DRIPSTONE, List.of(
                            new ItemStack(DARK_DRIPSTONE_BLOCK),
                            new ItemStack(POINTED_DARK_DRIPSTONE),
                            new ItemStack(LIMESTONE)
                    ), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS)
                .register(output -> {
                    output.insertAfter(Items.TRIAL_SPAWNER, List.of(
                            new ItemStack(REINFORCED_SPAWNER)
                    ), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                });
    }
}