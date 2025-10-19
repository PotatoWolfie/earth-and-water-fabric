package potatowolfie.earth_and_water.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.recipe.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.block.ModBlocks;
import potatowolfie.earth_and_water.item.ModItems;
import potatowolfie.earth_and_water.trim.ModTrimPatterns;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeGenerator extends FabricRecipeProvider {
    public ModRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter) {
        return new RecipeGenerator(registryLookup, exporter) {
            @Override
            public void generate() {
                RegistryWrapper.Impl<Item> itemLookup = registries.getOrThrow(RegistryKeys.ITEM);

                offerReversibleCompactingRecipes(RecipeCategory.BUILDING_BLOCKS, ModItems.STEEL_INGOT, RecipeCategory.MISC, ModBlocks.STEEL_BLOCK);

                createShaped(RecipeCategory.MISC, ModItems.STEEL_INGOT, 1)
                        .pattern("XXX")
                        .pattern("XXX")
                        .pattern("XXX")
                        .input('X', ModItems.STEEL_NUGGET)
                        .criterion(hasItem(ModItems.STEEL_NUGGET), conditionsFromItem(ModItems.STEEL_NUGGET))
                        .offerTo(exporter, String.valueOf(Identifier.of(EarthWater.MOD_ID, "steel_ingot_from_nuggets")));

                createShaped(RecipeCategory.MISC, ModItems.STEEL_NUGGET, 9)
                        .pattern("X")
                        .input('X', ModItems.STEEL_INGOT)
                        .criterion(hasItem(ModItems.STEEL_INGOT), conditionsFromItem(ModItems.STEEL_INGOT))
                        .offerTo(exporter);

                offerSmelting(List.of(ModItems.BATTLE_AXE), RecipeCategory.MISC, ModItems.STEEL_NUGGET, 0.1f, 200, "steel");
                offerBlasting(List.of(ModItems.BATTLE_AXE), RecipeCategory.MISC, ModItems.STEEL_NUGGET, 0.1f, 100, "steel");

                offerSmithingTrimRecipe(ModItems.BLOCK_ARMOR_TRIM_SMITHING_TEMPLATE, ModTrimPatterns.BLOCK,
                        RegistryKey.of(RegistryKeys.RECIPE, Identifier.of(EarthWater.MOD_ID, "block")));
                offerSmithingTrimRecipe(ModItems.GUARD_ARMOR_TRIM_SMITHING_TEMPLATE, ModTrimPatterns.GUARD,
                        RegistryKey.of(RegistryKeys.RECIPE, Identifier.of(EarthWater.MOD_ID, "guard")));

                generateDripstoneRecipes();
                generateDarkDripstoneRecipes();
                generatePrismarineRecipes();
                generateLimestoneRecipes();
            }

            private void generateDripstoneRecipes() {
                createStairsRecipe(ModBlocks.DRIPSTONE_STAIRS, Ingredient.ofItems(Blocks.DRIPSTONE_BLOCK));
                createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_SLAB, Ingredient.ofItem(Blocks.DRIPSTONE_BLOCK));
                offerWallRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_WALL, Blocks.DRIPSTONE_BLOCK);

                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_STAIRS, Blocks.DRIPSTONE_BLOCK);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_SLAB, Blocks.DRIPSTONE_BLOCK, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_WALL, Blocks.DRIPSTONE_BLOCK);

                offer2x2CompactingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DRIPSTONE, Blocks.DRIPSTONE_BLOCK);
                createStairsRecipe(ModBlocks.POLISHED_DRIPSTONE_STAIRS, Ingredient.ofItems(ModBlocks.POLISHED_DRIPSTONE));
                createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DRIPSTONE_SLAB, Ingredient.ofItem(ModBlocks.POLISHED_DRIPSTONE));
                offerWallRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DRIPSTONE_WALL, ModBlocks.POLISHED_DRIPSTONE);

                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DRIPSTONE, Blocks.DRIPSTONE_BLOCK);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DRIPSTONE_STAIRS, ModBlocks.POLISHED_DRIPSTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DRIPSTONE_STAIRS, Blocks.DRIPSTONE_BLOCK);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DRIPSTONE_SLAB, ModBlocks.POLISHED_DRIPSTONE, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DRIPSTONE_SLAB, Blocks.DRIPSTONE_BLOCK, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DRIPSTONE_WALL, ModBlocks.POLISHED_DRIPSTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DRIPSTONE_WALL, Blocks.DRIPSTONE_BLOCK);

                offer2x2CompactingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_BRICKS, ModBlocks.POLISHED_DRIPSTONE);
                createStairsRecipe(ModBlocks.DRIPSTONE_BRICK_STAIRS, Ingredient.ofItems(ModBlocks.DRIPSTONE_BRICKS));
                createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_BRICK_SLAB, Ingredient.ofItem(ModBlocks.DRIPSTONE_BRICKS));
                offerWallRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_BRICK_WALL, ModBlocks.DRIPSTONE_BRICKS);

                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_BRICKS, ModBlocks.POLISHED_DRIPSTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_BRICKS, Blocks.DRIPSTONE_BLOCK);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_BRICK_STAIRS, ModBlocks.DRIPSTONE_BRICKS);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_BRICK_STAIRS, ModBlocks.POLISHED_DRIPSTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_BRICK_STAIRS, Blocks.DRIPSTONE_BLOCK);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_BRICK_SLAB, ModBlocks.DRIPSTONE_BRICKS, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_BRICK_SLAB, ModBlocks.POLISHED_DRIPSTONE, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_BRICK_SLAB, Blocks.DRIPSTONE_BLOCK, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_BRICK_WALL, ModBlocks.DRIPSTONE_BRICKS);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_BRICK_WALL, ModBlocks.POLISHED_DRIPSTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_BRICK_WALL, Blocks.DRIPSTONE_BLOCK);

                createChiseledBlockRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_DRIPSTONE_BRICKS, Ingredient.ofItem(ModBlocks.DRIPSTONE_BRICK_SLAB));
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_DRIPSTONE_BRICKS, ModBlocks.DRIPSTONE_BRICKS);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_DRIPSTONE_BRICKS, ModBlocks.POLISHED_DRIPSTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_DRIPSTONE_BRICKS, Blocks.DRIPSTONE_BLOCK);

                createChiseledBlockRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_PILLAR, Ingredient.ofItem(ModBlocks.DRIPSTONE_SLAB));
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_PILLAR, Blocks.DRIPSTONE_BLOCK);
            }

            private void generateDarkDripstoneRecipes() {
                createStairsRecipe(ModBlocks.DARK_DRIPSTONE_STAIRS, Ingredient.ofItems(ModBlocks.DARK_DRIPSTONE_BLOCK));
                createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_SLAB, Ingredient.ofItem(ModBlocks.DARK_DRIPSTONE_BLOCK));
                offerWallRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_WALL, ModBlocks.DARK_DRIPSTONE_BLOCK);

                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_STAIRS, ModBlocks.DARK_DRIPSTONE_BLOCK);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_SLAB, ModBlocks.DARK_DRIPSTONE_BLOCK, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_WALL, ModBlocks.DARK_DRIPSTONE_BLOCK);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_PILLAR, ModBlocks.DARK_DRIPSTONE_BLOCK);

                offer2x2CompactingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DARK_DRIPSTONE, ModBlocks.DARK_DRIPSTONE_BLOCK);
                createStairsRecipe(ModBlocks.POLISHED_DARK_DRIPSTONE_STAIRS, Ingredient.ofItems(ModBlocks.POLISHED_DARK_DRIPSTONE));
                createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DARK_DRIPSTONE_SLAB, Ingredient.ofItem(ModBlocks.POLISHED_DARK_DRIPSTONE));
                offerWallRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DARK_DRIPSTONE_WALL, ModBlocks.POLISHED_DARK_DRIPSTONE);

                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DARK_DRIPSTONE, ModBlocks.DARK_DRIPSTONE_BLOCK);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DARK_DRIPSTONE_STAIRS, ModBlocks.POLISHED_DARK_DRIPSTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DARK_DRIPSTONE_STAIRS, ModBlocks.DARK_DRIPSTONE_BLOCK);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DARK_DRIPSTONE_SLAB, ModBlocks.POLISHED_DARK_DRIPSTONE, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DARK_DRIPSTONE_SLAB, ModBlocks.DARK_DRIPSTONE_BLOCK, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DARK_DRIPSTONE_WALL, ModBlocks.POLISHED_DARK_DRIPSTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DARK_DRIPSTONE_WALL, ModBlocks.DARK_DRIPSTONE_BLOCK);

                offer2x2CompactingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_BRICKS, ModBlocks.POLISHED_DARK_DRIPSTONE);
                createStairsRecipe(ModBlocks.DARK_DRIPSTONE_BRICK_STAIRS, Ingredient.ofItems(ModBlocks.DARK_DRIPSTONE_BRICKS));
                createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_BRICK_SLAB, Ingredient.ofItem(ModBlocks.DARK_DRIPSTONE_BRICKS));
                offerWallRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_BRICK_WALL, ModBlocks.DARK_DRIPSTONE_BRICKS);

                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_BRICKS, ModBlocks.POLISHED_DARK_DRIPSTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_BRICKS, ModBlocks.DARK_DRIPSTONE_BLOCK);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_BRICK_STAIRS, ModBlocks.DARK_DRIPSTONE_BRICKS);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_BRICK_STAIRS, ModBlocks.POLISHED_DARK_DRIPSTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_BRICK_STAIRS, ModBlocks.DARK_DRIPSTONE_BLOCK);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_BRICK_SLAB, ModBlocks.DARK_DRIPSTONE_BRICKS, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_BRICK_SLAB, ModBlocks.POLISHED_DARK_DRIPSTONE, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_BRICK_SLAB, ModBlocks.DARK_DRIPSTONE_BLOCK, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_BRICK_WALL, ModBlocks.DARK_DRIPSTONE_BRICKS);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_BRICK_WALL, ModBlocks.POLISHED_DARK_DRIPSTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_BRICK_WALL, ModBlocks.DARK_DRIPSTONE_BLOCK);
            }

            private void generatePrismarineRecipes() {
                createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PRISMARINE_TILE_SLAB, Ingredient.ofItem(ModBlocks.PRISMARINE_TILES));
                offerWallRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PRISMARINE_TILE_WALL, ModBlocks.PRISMARINE_TILES);

                offer2x2CompactingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PRISMARINE_TILES, Blocks.PRISMARINE_BRICKS);
                createStairsRecipe(ModBlocks.PRISMARINE_TILE_STAIRS, Ingredient.ofItems(ModBlocks.PRISMARINE_TILES));

                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PRISMARINE_TILES, Blocks.PRISMARINE_BRICKS);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PRISMARINE_TILES, Blocks.PRISMARINE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PRISMARINE_TILE_STAIRS, ModBlocks.PRISMARINE_TILES);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PRISMARINE_TILE_STAIRS, Blocks.PRISMARINE_BRICKS);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PRISMARINE_TILE_STAIRS, Blocks.PRISMARINE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PRISMARINE_TILE_SLAB, ModBlocks.PRISMARINE_TILES, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PRISMARINE_TILE_SLAB, Blocks.PRISMARINE_BRICKS, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PRISMARINE_TILE_SLAB, Blocks.PRISMARINE, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PRISMARINE_TILE_WALL, ModBlocks.PRISMARINE_TILES);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PRISMARINE_TILE_WALL, Blocks.PRISMARINE_BRICKS);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PRISMARINE_TILE_WALL, Blocks.PRISMARINE);

                createChiseledBlockRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PRISMARINE_PILLAR, Ingredient.ofItem(Blocks.PRISMARINE_SLAB));
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PRISMARINE_PILLAR, Blocks.PRISMARINE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PRISMARINE_PILLAR, Blocks.PRISMARINE_BRICKS);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PRISMARINE_PILLAR, ModBlocks.PRISMARINE_TILES);

                createChiseledBlockRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_PRISMARINE_BRICKS, Ingredient.ofItem(Blocks.PRISMARINE_BRICK_SLAB));
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_PRISMARINE_BRICKS, Blocks.PRISMARINE_BRICKS);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_PRISMARINE_BRICKS, Blocks.PRISMARINE);

                createChiseledBlockRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_PRISMARINE_PILLAR, Ingredient.ofItem(Blocks.DARK_PRISMARINE_SLAB));
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_PRISMARINE_PILLAR, Blocks.DARK_PRISMARINE);

                createChiseledBlockRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_DARK_PRISMARINE, Ingredient.ofItems(Blocks.DARK_PRISMARINE_SLAB));
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_DARK_PRISMARINE, Blocks.DARK_PRISMARINE);
            }

            private void generateLimestoneRecipes() {
                createStairsRecipe(ModBlocks.LIMESTONE_STAIRS, Ingredient.ofItems(ModBlocks.LIMESTONE));
                createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_SLAB, Ingredient.ofItem(ModBlocks.LIMESTONE));
                offerWallRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_WALL, ModBlocks.LIMESTONE);

                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_STAIRS, ModBlocks.LIMESTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_SLAB, ModBlocks.LIMESTONE, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_WALL, ModBlocks.LIMESTONE);

                offer2x2CompactingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_LIMESTONE, ModBlocks.LIMESTONE);
                createStairsRecipe(ModBlocks.POLISHED_LIMESTONE_STAIRS, Ingredient.ofItems(ModBlocks.POLISHED_LIMESTONE));
                createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_LIMESTONE_SLAB, Ingredient.ofItem(ModBlocks.POLISHED_LIMESTONE));
                offerWallRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_LIMESTONE_WALL, ModBlocks.POLISHED_LIMESTONE);

                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_LIMESTONE, ModBlocks.LIMESTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_LIMESTONE_STAIRS, ModBlocks.POLISHED_LIMESTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_LIMESTONE_STAIRS, ModBlocks.LIMESTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_LIMESTONE_SLAB, ModBlocks.POLISHED_LIMESTONE, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_LIMESTONE_SLAB, ModBlocks.LIMESTONE, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_LIMESTONE_WALL, ModBlocks.POLISHED_LIMESTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_LIMESTONE_WALL, ModBlocks.LIMESTONE);

                offer2x2CompactingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_BRICKS, ModBlocks.POLISHED_LIMESTONE);
                createStairsRecipe(ModBlocks.LIMESTONE_BRICK_STAIRS, Ingredient.ofItems(ModBlocks.LIMESTONE_BRICKS));
                createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_BRICK_SLAB, Ingredient.ofItem(ModBlocks.LIMESTONE_BRICKS));
                offerWallRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_BRICK_WALL, ModBlocks.LIMESTONE_BRICKS);

                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_BRICKS, ModBlocks.POLISHED_LIMESTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_BRICKS, ModBlocks.LIMESTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_BRICK_STAIRS, ModBlocks.LIMESTONE_BRICKS);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_BRICK_STAIRS, ModBlocks.POLISHED_LIMESTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_BRICK_STAIRS, ModBlocks.LIMESTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_BRICK_SLAB, ModBlocks.LIMESTONE_BRICKS, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_BRICK_SLAB, ModBlocks.POLISHED_LIMESTONE, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_BRICK_SLAB, ModBlocks.LIMESTONE, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_BRICK_WALL, ModBlocks.LIMESTONE_BRICKS);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_BRICK_WALL, ModBlocks.POLISHED_LIMESTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_BRICK_WALL, ModBlocks.LIMESTONE);

                createChiseledBlockRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_LIMESTONE_BRICKS, Ingredient.ofItem(ModBlocks.LIMESTONE_BRICK_SLAB));
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_LIMESTONE_BRICKS, ModBlocks.LIMESTONE_BRICKS);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_LIMESTONE_BRICKS, ModBlocks.POLISHED_LIMESTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_LIMESTONE_BRICKS, ModBlocks.LIMESTONE);

                createChiseledBlockRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_PILLAR, Ingredient.ofItem(ModBlocks.LIMESTONE_SLAB));
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_PILLAR, ModBlocks.LIMESTONE);
            }

            private void createStonecuttingRecipe(RecipeCategory category, ItemConvertible output, ItemConvertible input) {
                createStonecuttingRecipe(category, output, input, 1);
            }

            private void createStonecuttingRecipe(RecipeCategory category, ItemConvertible output, ItemConvertible input, int count) {
                StonecuttingRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(input), category, output, count)
                        .criterion(hasItem(input), conditionsFromItem(input))
                        .offerTo(exporter, convertBetween(output, input) + "_stonecutting");
            }

            public static String convertBetween(ItemConvertible to, ItemConvertible from) {
                return getItemPath(to) + "_from_" + getItemPath(from);
            }
        };
    }

    @Override
    public String getName() {
        return "ModRecipeGenerator";
    }
}