package potatowolfie.earth_and_water.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Blocks;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.Models;
import net.minecraft.client.data.TexturedModel;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.item.equipment.trim.ArmorTrimPattern;
import net.minecraft.registry.Registry;
import potatowolfie.earth_and_water.block.ModBlocks;
import potatowolfie.earth_and_water.item.ModItems;

import java.util.List;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        BlockStateModelGenerator.BlockTexturePool polisheddripstoneTexturePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.POLISHED_DRIPSTONE);

        BlockStateModelGenerator.BlockTexturePool dripstonebricksTexturePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.DRIPSTONE_BRICKS);
        dripstonebricksTexturePool.stairs(ModBlocks.DRIPSTONE_BRICK_STAIRS);
        dripstonebricksTexturePool.slab(ModBlocks.DRIPSTONE_BRICK_SLAB);
        dripstonebricksTexturePool.wall(ModBlocks.DRIPSTONE_BRICK_WALL);

        BlockStateModelGenerator.BlockTexturePool darkdripstoneTexturePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.DARK_DRIPSTONE_BLOCK);
        darkdripstoneTexturePool.stairs(ModBlocks.DARK_DRIPSTONE_STAIRS);
        darkdripstoneTexturePool.slab(ModBlocks.DARK_DRIPSTONE_SLAB);
        darkdripstoneTexturePool.wall(ModBlocks.DARK_DRIPSTONE_WALL);

        BlockStateModelGenerator.BlockTexturePool polisheddarkdripstoneTexturePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.POLISHED_DARK_DRIPSTONE);
        polisheddarkdripstoneTexturePool.stairs(ModBlocks.POLISHED_DARK_DRIPSTONE_STAIRS);
        polisheddarkdripstoneTexturePool.slab(ModBlocks.POLISHED_DARK_DRIPSTONE_SLAB);
        polisheddarkdripstoneTexturePool.wall(ModBlocks.POLISHED_DARK_DRIPSTONE_WALL);

        BlockStateModelGenerator.BlockTexturePool darkdripstonebricksTexturePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.DARK_DRIPSTONE_BRICKS);
        darkdripstonebricksTexturePool.stairs(ModBlocks.DARK_DRIPSTONE_BRICK_STAIRS);
        darkdripstonebricksTexturePool.slab(ModBlocks.DARK_DRIPSTONE_BRICK_SLAB);
        darkdripstonebricksTexturePool.wall(ModBlocks.DARK_DRIPSTONE_BRICK_WALL);

        BlockStateModelGenerator.BlockTexturePool dripstoneTexturePool = blockStateModelGenerator.registerCubeAllModelTexturePool(Blocks.DRIPSTONE_BLOCK);
        dripstoneTexturePool.stairs(ModBlocks.DRIPSTONE_STAIRS);
        dripstoneTexturePool.slab(ModBlocks.DRIPSTONE_SLAB);
        dripstoneTexturePool.wall(ModBlocks.DRIPSTONE_WALL);

        polisheddripstoneTexturePool.stairs(ModBlocks.POLISHED_DRIPSTONE_STAIRS);
        polisheddripstoneTexturePool.slab(ModBlocks.POLISHED_DRIPSTONE_SLAB);
        polisheddripstoneTexturePool.wall(ModBlocks.POLISHED_DRIPSTONE_WALL);

        BlockStateModelGenerator.BlockTexturePool limestoneTexturePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.LIMESTONE);
        limestoneTexturePool.stairs(ModBlocks.LIMESTONE_STAIRS);
        limestoneTexturePool.slab(ModBlocks.LIMESTONE_SLAB);
        limestoneTexturePool.wall(ModBlocks.LIMESTONE_WALL);

        BlockStateModelGenerator.BlockTexturePool polishedLimestoneTexturePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.POLISHED_LIMESTONE);
        polishedLimestoneTexturePool.stairs(ModBlocks.POLISHED_LIMESTONE_STAIRS);
        polishedLimestoneTexturePool.slab(ModBlocks.POLISHED_LIMESTONE_SLAB);
        polishedLimestoneTexturePool.wall(ModBlocks.POLISHED_LIMESTONE_WALL);

        BlockStateModelGenerator.BlockTexturePool limestoneBricksTexturePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.LIMESTONE_BRICKS);
        limestoneBricksTexturePool.stairs(ModBlocks.LIMESTONE_BRICK_STAIRS);
        limestoneBricksTexturePool.slab(ModBlocks.LIMESTONE_BRICK_SLAB);
        limestoneBricksTexturePool.wall(ModBlocks.LIMESTONE_BRICK_WALL);

        blockStateModelGenerator.registerAxisRotated(ModBlocks.LIMESTONE_PILLAR, TexturedModel.END_FOR_TOP_CUBE_COLUMN, TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL);
        blockStateModelGenerator.registerGeneric(ModBlocks.CHISELED_LIMESTONE_BRICKS);

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.STEEL_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.OXYGEN_BLOCK);

        blockStateModelGenerator.registerGeneric(ModBlocks.MIXED_PRISMARINE_TILES);
        blockStateModelGenerator.registerGeneric(ModBlocks.POLISHED_DRIPSTONE_TILES);

        BlockStateModelGenerator.BlockTexturePool prismarinetilesTexturePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.PRISMARINE_TILES);
        prismarinetilesTexturePool.stairs(ModBlocks.PRISMARINE_TILE_STAIRS);
        prismarinetilesTexturePool.slab(ModBlocks.PRISMARINE_TILE_SLAB);
        prismarinetilesTexturePool.wall(ModBlocks.PRISMARINE_TILE_WALL);

        BlockStateModelGenerator.BlockTexturePool darkprismarineTexturePool = blockStateModelGenerator.registerCubeAllModelTexturePool(Blocks.DARK_PRISMARINE);
        darkprismarineTexturePool.wall(ModBlocks.DARK_PRISMARINE_WALL);

        blockStateModelGenerator.registerAxisRotated(ModBlocks.DRIPSTONE_PILLAR, TexturedModel.END_FOR_TOP_CUBE_COLUMN, TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL);
        blockStateModelGenerator.registerAxisRotated(ModBlocks.DARK_DRIPSTONE_PILLAR, TexturedModel.END_FOR_TOP_CUBE_COLUMN, TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL);
        blockStateModelGenerator.registerAxisRotated(ModBlocks.DARK_PRISMARINE_PILLAR, TexturedModel.END_FOR_TOP_CUBE_COLUMN, TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL);
        blockStateModelGenerator.registerAxisRotated(ModBlocks.PRISMARINE_PILLAR, TexturedModel.END_FOR_TOP_CUBE_COLUMN, TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.BORE_ROD, Models.GENERATED);
        itemModelGenerator.register(ModItems.BRINE_ROD, Models.GENERATED);
        itemModelGenerator.register(ModItems.STEEL_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.STEEL_NUGGET, Models.GENERATED);
        itemModelGenerator.register(ModItems.STEEL_UPGRADE_SMITHING_TEMPLATE, Models.GENERATED);
        itemModelGenerator.register(ModItems.BLOCK_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
        itemModelGenerator.register(ModItems.GUARD_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
        itemModelGenerator.register(ModItems.REINFORCED_KEY, Models.GENERATED);
        itemModelGenerator.register(ModItems.WHIP, Models.HANDHELD_ROD);
        itemModelGenerator.register(ModItems.BATTLE_AXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.BORE_SPAWN_EGG, Models.HANDHELD);
        itemModelGenerator.register(ModItems.BRINE_SPAWN_EGG, Models.HANDHELD);
    }
}
