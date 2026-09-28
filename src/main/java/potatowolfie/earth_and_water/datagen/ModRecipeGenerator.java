package potatowolfie.earth_and_water.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.block.ModBlocks;
import potatowolfie.earth_and_water.item.ModItems;
import potatowolfie.earth_and_water.trim.ModTrimPatterns;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeGenerator extends FabricRecipeProvider {
    public ModRecipeGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
        return new RecipeProvider(registryLookup, exporter) {
            @Override
            public void buildRecipes() {
                HolderLookup.RegistryLookup<Item> itemLookup = registries.lookupOrThrow(Registries.ITEM);

                nineBlockStorageRecipes(RecipeCategory.BUILDING_BLOCKS, ModItems.STEEL_INGOT, RecipeCategory.MISC, ModBlocks.STEEL_BLOCK);

                shaped(RecipeCategory.MISC, ModItems.STEEL_INGOT, 1)
                        .pattern("XXX")
                        .pattern("XXX")
                        .pattern("XXX")
                        .define('X', ModItems.STEEL_NUGGET)
                        .unlockedBy(getHasName(ModItems.STEEL_NUGGET), has(ModItems.STEEL_NUGGET))
                        .save(output, String.valueOf(Identifier.fromNamespaceAndPath(EarthWater.MOD_ID, "steel_ingot_from_nuggets")));

                shaped(RecipeCategory.MISC, ModItems.STEEL_NUGGET, 9)
                        .pattern("X")
                        .define('X', ModItems.STEEL_INGOT)
                        .unlockedBy(getHasName(ModItems.STEEL_INGOT), has(ModItems.STEEL_INGOT))
                        .save(output);

                oreSmelting(
                        List.of(ModItems.BATTLE_AXE),
                        RecipeCategory.MISC,
                        CookingBookCategory.MISC,
                        ModItems.STEEL_NUGGET,
                        0.1f,
                        200,
                        "steel"
                );

                oreBlasting(
                        List.of(ModItems.BATTLE_AXE),
                        RecipeCategory.MISC,
                        CookingBookCategory.MISC,
                        ModItems.STEEL_NUGGET,
                        0.1f,
                        100,
                        "steel"
                );

                trimSmithing(ModItems.BLOCK_ARMOR_TRIM_SMITHING_TEMPLATE, ModTrimPatterns.BLOCK,
                        ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(EarthWater.MOD_ID, "block")));
                trimSmithing(ModItems.GUARD_ARMOR_TRIM_SMITHING_TEMPLATE, ModTrimPatterns.GUARD,
                        ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(EarthWater.MOD_ID, "guard")));

                generateDripstoneRecipes();
                generateDarkDripstoneRecipes();
                generatePrismarineRecipes();
                generateLimestoneRecipes();
            }

            private void generateDripstoneRecipes() {
                stairBuilder(ModBlocks.DRIPSTONE_STAIRS, Ingredient.of(Blocks.DRIPSTONE_BLOCK));
                slabBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_SLAB, Ingredient.of(Blocks.DRIPSTONE_BLOCK));
                wall(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_WALL, Blocks.DRIPSTONE_BLOCK);

                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_STAIRS, Blocks.DRIPSTONE_BLOCK);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_SLAB, Blocks.DRIPSTONE_BLOCK, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_WALL, Blocks.DRIPSTONE_BLOCK);

                polished(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DRIPSTONE, Blocks.DRIPSTONE_BLOCK);
                stairBuilder(ModBlocks.POLISHED_DRIPSTONE_STAIRS, Ingredient.of(ModBlocks.POLISHED_DRIPSTONE));
                slabBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DRIPSTONE_SLAB, Ingredient.of(ModBlocks.POLISHED_DRIPSTONE));
                wall(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DRIPSTONE_WALL, ModBlocks.POLISHED_DRIPSTONE);

                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DRIPSTONE, Blocks.DRIPSTONE_BLOCK);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DRIPSTONE_STAIRS, ModBlocks.POLISHED_DRIPSTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DRIPSTONE_STAIRS, Blocks.DRIPSTONE_BLOCK);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DRIPSTONE_SLAB, ModBlocks.POLISHED_DRIPSTONE, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DRIPSTONE_SLAB, Blocks.DRIPSTONE_BLOCK, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DRIPSTONE_WALL, ModBlocks.POLISHED_DRIPSTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DRIPSTONE_WALL, Blocks.DRIPSTONE_BLOCK);

                bricksBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_BRICKS, Ingredient.of(ModBlocks.POLISHED_DRIPSTONE)).unlockedBy(getHasName(ModBlocks.POLISHED_DRIPSTONE), has(ModBlocks.POLISHED_DRIPSTONE)).save(output);
                stairBuilder(ModBlocks.DRIPSTONE_BRICK_STAIRS, Ingredient.of(ModBlocks.DRIPSTONE_BRICKS));
                slabBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_BRICK_SLAB, Ingredient.of(ModBlocks.DRIPSTONE_BRICKS));
                wall(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_BRICK_WALL, ModBlocks.DRIPSTONE_BRICKS);

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

                chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_DRIPSTONE_BRICKS, Ingredient.of(ModBlocks.DRIPSTONE_BRICK_SLAB));
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_DRIPSTONE_BRICKS, ModBlocks.DRIPSTONE_BRICKS);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_DRIPSTONE_BRICKS, ModBlocks.POLISHED_DRIPSTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_DRIPSTONE_BRICKS, Blocks.DRIPSTONE_BLOCK);

                chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_PILLAR, Ingredient.of(ModBlocks.DRIPSTONE_SLAB));
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIPSTONE_PILLAR, Blocks.DRIPSTONE_BLOCK);
            }

            private void generateDarkDripstoneRecipes() {
                stairBuilder(ModBlocks.DARK_DRIPSTONE_STAIRS, Ingredient.of(ModBlocks.DARK_DRIPSTONE_BLOCK));
                slabBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_SLAB, Ingredient.of(ModBlocks.DARK_DRIPSTONE_BLOCK));
                wall(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_WALL, ModBlocks.DARK_DRIPSTONE_BLOCK);

                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_STAIRS, ModBlocks.DARK_DRIPSTONE_BLOCK);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_SLAB, ModBlocks.DARK_DRIPSTONE_BLOCK, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_WALL, ModBlocks.DARK_DRIPSTONE_BLOCK);

                chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_PILLAR, Ingredient.of(ModBlocks.DARK_DRIPSTONE_SLAB));
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_PILLAR, ModBlocks.DARK_DRIPSTONE_BLOCK);

                polished(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DARK_DRIPSTONE, ModBlocks.DARK_DRIPSTONE_BLOCK);
                stairBuilder(ModBlocks.POLISHED_DARK_DRIPSTONE_STAIRS, Ingredient.of(ModBlocks.POLISHED_DARK_DRIPSTONE));
                slabBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DARK_DRIPSTONE_SLAB, Ingredient.of(ModBlocks.POLISHED_DARK_DRIPSTONE));
                wall(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DARK_DRIPSTONE_WALL, ModBlocks.POLISHED_DARK_DRIPSTONE);

                chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_DARK_DRIPSTONE_BRICKS, Ingredient.of(ModBlocks.DARK_DRIPSTONE_BRICK_SLAB));
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_DARK_DRIPSTONE_BRICKS, ModBlocks.DARK_DRIPSTONE_BRICKS);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_DARK_DRIPSTONE_BRICKS, ModBlocks.POLISHED_DARK_DRIPSTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_DARK_DRIPSTONE_BRICKS, ModBlocks.DARK_DRIPSTONE_BLOCK);

                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DARK_DRIPSTONE, ModBlocks.DARK_DRIPSTONE_BLOCK);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DARK_DRIPSTONE_STAIRS, ModBlocks.POLISHED_DARK_DRIPSTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DARK_DRIPSTONE_STAIRS, ModBlocks.DARK_DRIPSTONE_BLOCK);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DARK_DRIPSTONE_SLAB, ModBlocks.POLISHED_DARK_DRIPSTONE, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DARK_DRIPSTONE_SLAB, ModBlocks.DARK_DRIPSTONE_BLOCK, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DARK_DRIPSTONE_WALL, ModBlocks.POLISHED_DARK_DRIPSTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DARK_DRIPSTONE_WALL, ModBlocks.DARK_DRIPSTONE_BLOCK);

                bricksBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_BRICKS, Ingredient.of(ModBlocks.POLISHED_DARK_DRIPSTONE)).unlockedBy(getHasName(ModBlocks.POLISHED_DARK_DRIPSTONE), has(ModBlocks.POLISHED_DARK_DRIPSTONE)).save(output);
                stairBuilder(ModBlocks.DARK_DRIPSTONE_BRICK_STAIRS, Ingredient.of(ModBlocks.DARK_DRIPSTONE_BRICKS));
                slabBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_BRICK_SLAB, Ingredient.of(ModBlocks.DARK_DRIPSTONE_BRICKS));
                wall(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_BRICK_WALL, ModBlocks.DARK_DRIPSTONE_BRICKS);

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
                slabBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PRISMARINE_TILE_SLAB, Ingredient.of(ModBlocks.PRISMARINE_TILES));
                wall(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PRISMARINE_TILE_WALL, ModBlocks.PRISMARINE_TILES);

                tilesBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PRISMARINE_TILES, Ingredient.of(Blocks.PRISMARINE_BRICKS)).unlockedBy(getHasName(Blocks.PRISMARINE_BRICKS), has(Blocks.PRISMARINE_BRICKS)).save(output);
                stairBuilder(ModBlocks.PRISMARINE_TILE_STAIRS, Ingredient.of(ModBlocks.PRISMARINE_TILES));

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

                chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PRISMARINE_PILLAR, Ingredient.of(Blocks.PRISMARINE_SLAB));
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PRISMARINE_PILLAR, Blocks.PRISMARINE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PRISMARINE_PILLAR, Blocks.PRISMARINE_BRICKS);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PRISMARINE_PILLAR, ModBlocks.PRISMARINE_TILES);

                chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_PRISMARINE_BRICKS, Ingredient.of(Blocks.PRISMARINE_BRICK_SLAB));
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_PRISMARINE_BRICKS, Blocks.PRISMARINE_BRICKS);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_PRISMARINE_BRICKS, Blocks.PRISMARINE);

                chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_PRISMARINE_PILLAR, Ingredient.of(Blocks.DARK_PRISMARINE_SLAB));
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_PRISMARINE_PILLAR, Blocks.DARK_PRISMARINE);

                chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_DARK_PRISMARINE, Ingredient.of(Blocks.DARK_PRISMARINE_SLAB));
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_DARK_PRISMARINE, Blocks.DARK_PRISMARINE);

                wall(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_PRISMARINE_WALL, Blocks.DARK_PRISMARINE);
            }

            private void generateLimestoneRecipes() {
                stairBuilder(ModBlocks.LIMESTONE_STAIRS, Ingredient.of(ModBlocks.LIMESTONE));
                slabBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_SLAB, Ingredient.of(ModBlocks.LIMESTONE));
                wall(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_WALL, ModBlocks.LIMESTONE);

                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_STAIRS, ModBlocks.LIMESTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_SLAB, ModBlocks.LIMESTONE, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_WALL, ModBlocks.LIMESTONE);

                polished(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_LIMESTONE, ModBlocks.LIMESTONE);
                stairBuilder(ModBlocks.POLISHED_LIMESTONE_STAIRS, Ingredient.of(ModBlocks.POLISHED_LIMESTONE));
                slabBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_LIMESTONE_SLAB, Ingredient.of(ModBlocks.POLISHED_LIMESTONE));
                wall(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_LIMESTONE_WALL, ModBlocks.POLISHED_LIMESTONE);

                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_LIMESTONE, ModBlocks.LIMESTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_LIMESTONE_STAIRS, ModBlocks.POLISHED_LIMESTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_LIMESTONE_STAIRS, ModBlocks.LIMESTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_LIMESTONE_SLAB, ModBlocks.POLISHED_LIMESTONE, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_LIMESTONE_SLAB, ModBlocks.LIMESTONE, 2);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_LIMESTONE_WALL, ModBlocks.POLISHED_LIMESTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_LIMESTONE_WALL, ModBlocks.LIMESTONE);

                bricksBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_BRICKS, Ingredient.of(ModBlocks.POLISHED_LIMESTONE)).unlockedBy(getHasName(ModBlocks.POLISHED_LIMESTONE), has(ModBlocks.POLISHED_LIMESTONE)).save(output);
                stairBuilder(ModBlocks.LIMESTONE_BRICK_STAIRS, Ingredient.of(ModBlocks.LIMESTONE_BRICKS));
                slabBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_BRICK_SLAB, Ingredient.of(ModBlocks.LIMESTONE_BRICKS));
                wall(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_BRICK_WALL, ModBlocks.LIMESTONE_BRICKS);

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

                chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_LIMESTONE_BRICKS, Ingredient.of(ModBlocks.LIMESTONE_BRICK_SLAB));
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_LIMESTONE_BRICKS, ModBlocks.LIMESTONE_BRICKS);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_LIMESTONE_BRICKS, ModBlocks.POLISHED_LIMESTONE);
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_LIMESTONE_BRICKS, ModBlocks.LIMESTONE);

                chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_PILLAR, Ingredient.of(ModBlocks.LIMESTONE_SLAB));
                createStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIMESTONE_PILLAR, ModBlocks.LIMESTONE);
            }

            private void createStonecuttingRecipe(RecipeCategory category, ItemLike output, ItemLike input) {
                createStonecuttingRecipe(category, output, input, 1);
            }

            private void createStonecuttingRecipe(RecipeCategory category, ItemLike result, ItemLike input, int count) {
                SingleItemRecipeBuilder.stonecutting(Ingredient.of(input), category, result, count)
                        .unlockedBy(getHasName(input), has(input))
                        .save(output, getConversionRecipeName(result, input) + "_stonecutting");
            }

            public static String getConversionRecipeName(ItemLike to, ItemLike from) {
                return getItemName(to) + "_from_" + getItemName(from);
            }
        };
    }

    @Override
    public String getName() {
        return "ModRecipeGenerator";
    }
}