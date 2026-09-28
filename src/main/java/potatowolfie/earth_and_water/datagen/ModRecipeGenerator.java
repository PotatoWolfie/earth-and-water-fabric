package potatowolfie.earth_and_water.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
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
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, BootstrapContext<Recipe<?>> bootstrapContext, BootstrapContext<Advancement> bootstrapContext1) {
        return new RecipeProvider(bootstrapContext, bootstrapContext1) {
            @Override
            public void buildRecipes() {
                HolderLookup.RegistryLookup<Item> itemLookup = provider.lookupOrThrow(Registries.ITEM);

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

                shaped(RecipeCategory.COMBAT, ModItems.BATTLE_AXE)
                        .pattern("# #")
                        .pattern("#X#")
                        .pattern(" X ")
                        .define('#', ModItems.STEEL_INGOT)
                        .define('X', ModItems.BORE_ROD)
                        .unlockedBy(getHasName(ModItems.STEEL_INGOT), has(ModItems.STEEL_INGOT))
                        .save(output);

                shaped(RecipeCategory.MISC, ModItems.BLOCK_ARMOR_TRIM_SMITHING_TEMPLATE, 2)
                        .pattern("#S#")
                        .pattern("#C#")
                        .pattern("###")
                        .define('#', Items.DIAMOND)
                        .define('C', Blocks.DRIPSTONE_BLOCK)
                        .define('S', ModItems.BLOCK_ARMOR_TRIM_SMITHING_TEMPLATE)
                        .unlockedBy(getHasName(ModItems.BLOCK_ARMOR_TRIM_SMITHING_TEMPLATE), has(ModItems.BLOCK_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .save(output);

                twoByTwoPacker(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_DRIPSTONE_BLOCK, ModBlocks.POINTED_DARK_DRIPSTONE);

                shapeless(RecipeCategory.COMBAT, ModItems.EARTH_CHARGE, 2)
                        .requires(ModItems.BORE_ROD)
                        .unlockedBy(getHasName(ModItems.BORE_ROD), has(ModItems.BORE_ROD))
                        .save(output);

                shaped(RecipeCategory.COMBAT, ModItems.EARTH_CHARGE, 4)
                        .pattern(" D ")
                        .pattern("DBD")
                        .pattern(" D ")
                        .define('D', Items.POINTED_DRIPSTONE)
                        .define('B', Blocks.DRIPSTONE_BLOCK)
                        .unlockedBy(getHasName(Items.POINTED_DRIPSTONE), has(Items.POINTED_DRIPSTONE))
                        .save(output, String.valueOf(Identifier.fromNamespaceAndPath(EarthWater.MOD_ID, "earth_charge_dripstone")));

                shaped(RecipeCategory.MISC, ModItems.GUARD_ARMOR_TRIM_SMITHING_TEMPLATE, 2)
                        .pattern("#S#")
                        .pattern("#C#")
                        .pattern("###")
                        .define('#', Items.DIAMOND)
                        .define('C', Blocks.PRISMARINE)
                        .define('S', ModItems.GUARD_ARMOR_TRIM_SMITHING_TEMPLATE)
                        .unlockedBy(getHasName(ModItems.GUARD_ARMOR_TRIM_SMITHING_TEMPLATE), has(ModItems.GUARD_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .save(output);

                shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MIXED_PRISMARINE_TILES, 4)
                        .pattern("X#")
                        .pattern("#X")
                        .define('#', ModBlocks.PRISMARINE_TILES)
                        .define('X', Blocks.DARK_PRISMARINE)
                        .group("mixed_prismarine_tiles")
                        .unlockedBy(getHasName(ModBlocks.PRISMARINE_TILES), has(ModBlocks.PRISMARINE_TILES))
                        .save(output);

                shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MIXED_PRISMARINE_TILES, 4)
                        .pattern("#X")
                        .pattern("X#")
                        .define('#', ModBlocks.PRISMARINE_TILES)
                        .define('X', Blocks.DARK_PRISMARINE)
                        .group("mixed_prismarine_tiles")
                        .unlockedBy(getHasName(ModBlocks.PRISMARINE_TILES), has(ModBlocks.PRISMARINE_TILES))
                        .save(output, String.valueOf(Identifier.fromNamespaceAndPath(EarthWater.MOD_ID, "mixed_prismarine_tiles_2")));

                shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DRIPSTONE_TILES, 4)
                        .pattern("#X")
                        .pattern("X#")
                        .define('#', ModBlocks.POLISHED_DRIPSTONE)
                        .define('X', ModBlocks.POLISHED_DARK_DRIPSTONE)
                        .group("polished_dripstone_tiles")
                        .unlockedBy(getHasName(ModBlocks.POLISHED_DRIPSTONE), has(ModBlocks.POLISHED_DRIPSTONE))
                        .save(output);

                shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DRIPSTONE_TILES, 4)
                        .pattern("X#")
                        .pattern("#X")
                        .define('#', ModBlocks.POLISHED_DRIPSTONE)
                        .define('X', ModBlocks.POLISHED_DARK_DRIPSTONE)
                        .group("polished_dripstone_tiles")
                        .unlockedBy(getHasName(ModBlocks.POLISHED_DRIPSTONE), has(ModBlocks.POLISHED_DRIPSTONE))
                        .save(output, String.valueOf(Identifier.fromNamespaceAndPath(EarthWater.MOD_ID, "polished_dripstone_tiles_2")));

                shaped(RecipeCategory.MISC, ModItems.REINFORCED_KEY, 2)
                        .pattern("#C#")
                        .pattern("CSC")
                        .pattern("#C#")
                        .define('#', ModItems.STEEL_INGOT)
                        .define('C', ModItems.STEEL_NUGGET)
                        .define('S', ModItems.REINFORCED_KEY)
                        .unlockedBy(getHasName(ModItems.REINFORCED_KEY), has(ModItems.REINFORCED_KEY))
                        .save(output, String.valueOf(Identifier.fromNamespaceAndPath(EarthWater.MOD_ID, "reinforced_key_dupli")));

                shapeless(RecipeCategory.COMBAT, ModItems.SPIKED_SHIELD)
                        .requires(ModItems.SPIKED_SHIELD)
                        .requires(ItemTags.BANNERS)
                        .unlockedBy(getHasName(ModItems.SPIKED_SHIELD), has(ModItems.SPIKED_SHIELD))
                        .save(output, String.valueOf(Identifier.fromNamespaceAndPath(EarthWater.MOD_ID, "spiked_shield_from_banner")));

                shapeless(RecipeCategory.TOOLS, Items.FLINT_AND_STEEL)
                        .requires(ModItems.STEEL_INGOT)
                        .requires(Items.FLINT)
                        .unlockedBy(getHasName(ModItems.STEEL_INGOT), has(ModItems.STEEL_INGOT))
                        .save(output, String.valueOf(Identifier.fromNamespaceAndPath(EarthWater.MOD_ID, "steel_and_flint")));

                shaped(RecipeCategory.MISC, ModItems.STEEL_UPGRADE_SMITHING_TEMPLATE, 2)
                        .pattern("#S#")
                        .pattern("#C#")
                        .pattern("###")
                        .define('#', Items.DIAMOND)
                        .define('C', ModBlocks.STEEL_BLOCK)
                        .define('S', ModItems.STEEL_UPGRADE_SMITHING_TEMPLATE)
                        .unlockedBy(getHasName(ModItems.STEEL_UPGRADE_SMITHING_TEMPLATE), has(ModItems.STEEL_UPGRADE_SMITHING_TEMPLATE))
                        .save(output, String.valueOf(Identifier.fromNamespaceAndPath(EarthWater.MOD_ID, "steel_upgrade_dupli")));

                shaped(RecipeCategory.COMBAT, ModItems.WATER_CHARGE, 4)
                        .pattern(" K ")
                        .pattern("KBK")
                        .pattern(" K ")
                        .define('K', Items.KELP)
                        .define('B', ModBlocks.OXYGEN_BLOCK)
                        .unlockedBy(getHasName(ModBlocks.OXYGEN_BLOCK), has(ModBlocks.OXYGEN_BLOCK));

                shaped(RecipeCategory.COMBAT, ModItems.WHIP)
                        .pattern("  X")
                        .pattern(" X#")
                        .pattern("X# ")
                        .define('#', Items.STRING)
                        .define('X', ModItems.BRINE_ROD)
                        .unlockedBy(getHasName(ModItems.BRINE_ROD), has(ModItems.BRINE_ROD))
                        .save(output);

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