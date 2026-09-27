package potatowolfie.earth_and_water.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import potatowolfie.earth_and_water.block.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
    public ModBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {

        builder(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(
                        key(ModBlocks.DRIPSTONE_STAIRS),
                        key(ModBlocks.DRIPSTONE_SLAB),
                        key(ModBlocks.DRIPSTONE_WALL),
                        key(ModBlocks.DRIPSTONE_BRICKS),
                        key(ModBlocks.DRIPSTONE_BRICK_STAIRS),
                        key(ModBlocks.DRIPSTONE_BRICK_SLAB),
                        key(ModBlocks.DRIPSTONE_BRICK_WALL),
                        key(ModBlocks.POLISHED_DRIPSTONE),
                        key(ModBlocks.POLISHED_DRIPSTONE_STAIRS),
                        key(ModBlocks.POLISHED_DRIPSTONE_SLAB),
                        key(ModBlocks.POLISHED_DRIPSTONE_WALL),
                        key(ModBlocks.POLISHED_DRIPSTONE_TILES),
                        key(ModBlocks.DRIPSTONE_PILLAR),
                        key(ModBlocks.CHISELED_DRIPSTONE_BRICKS),
                        key(ModBlocks.DARK_DRIPSTONE_BLOCK),
                        key(ModBlocks.DARK_DRIPSTONE_STAIRS),
                        key(ModBlocks.DARK_DRIPSTONE_SLAB),
                        key(ModBlocks.DARK_DRIPSTONE_WALL),
                        key(ModBlocks.DARK_DRIPSTONE_BRICKS),
                        key(ModBlocks.DARK_DRIPSTONE_BRICK_STAIRS),
                        key(ModBlocks.DARK_DRIPSTONE_BRICK_SLAB),
                        key(ModBlocks.DARK_DRIPSTONE_BRICK_WALL),
                        key(ModBlocks.POLISHED_DARK_DRIPSTONE),
                        key(ModBlocks.POLISHED_DARK_DRIPSTONE_STAIRS),
                        key(ModBlocks.POLISHED_DARK_DRIPSTONE_SLAB),
                        key(ModBlocks.POLISHED_DARK_DRIPSTONE_WALL),
                        key(ModBlocks.DARK_DRIPSTONE_PILLAR),
                        key(ModBlocks.POINTED_DARK_DRIPSTONE),
                        key(ModBlocks.DARK_PRISMARINE_PILLAR),
                        key(ModBlocks.CHISELED_PRISMARINE_BRICKS),
                        key(ModBlocks.PRISMARINE_PILLAR),
                        key(ModBlocks.PRISMARINE_TILES),
                        key(ModBlocks.PRISMARINE_TILE_STAIRS),
                        key(ModBlocks.PRISMARINE_TILE_SLAB),
                        key(ModBlocks.PRISMARINE_TILE_WALL),
                        key(ModBlocks.MIXED_PRISMARINE_TILES),
                        key(ModBlocks.CHISELED_DARK_PRISMARINE),
                        key(ModBlocks.DARK_PRISMARINE_WALL),
                        key(ModBlocks.OXYGEN_BLOCK),
                        key(ModBlocks.LIMESTONE),
                        key(ModBlocks.LIMESTONE_STAIRS),
                        key(ModBlocks.LIMESTONE_SLAB),
                        key(ModBlocks.LIMESTONE_WALL),
                        key(ModBlocks.POLISHED_LIMESTONE),
                        key(ModBlocks.POLISHED_LIMESTONE_STAIRS),
                        key(ModBlocks.POLISHED_LIMESTONE_SLAB),
                        key(ModBlocks.POLISHED_LIMESTONE_WALL),
                        key(ModBlocks.LIMESTONE_BRICKS),
                        key(ModBlocks.LIMESTONE_BRICK_STAIRS),
                        key(ModBlocks.LIMESTONE_BRICK_SLAB),
                        key(ModBlocks.LIMESTONE_BRICK_WALL),
                        key(ModBlocks.LIMESTONE_PILLAR),
                        key(ModBlocks.CHISELED_LIMESTONE_BRICKS)
                );

        builder(BlockTags.WALLS)
                .add(
                        key(ModBlocks.POLISHED_DRIPSTONE_WALL),
                        key(ModBlocks.DRIPSTONE_WALL),
                        key(ModBlocks.PRISMARINE_TILE_WALL),
                        key(ModBlocks.DARK_DRIPSTONE_WALL),
                        key(ModBlocks.POLISHED_DARK_DRIPSTONE_WALL),
                        key(ModBlocks.DARK_DRIPSTONE_BRICK_WALL),
                        key(ModBlocks.DARK_PRISMARINE_WALL),
                        key(ModBlocks.LIMESTONE_WALL),
                        key(ModBlocks.POLISHED_LIMESTONE_WALL),
                        key(ModBlocks.LIMESTONE_BRICK_WALL)
                );

        builder(BlockTags.SLABS)
                .add(
                        key(ModBlocks.DRIPSTONE_SLAB),
                        key(ModBlocks.DRIPSTONE_BRICK_SLAB),
                        key(ModBlocks.POLISHED_DRIPSTONE_SLAB),
                        key(ModBlocks.DARK_DRIPSTONE_SLAB),
                        key(ModBlocks.DARK_DRIPSTONE_BRICK_SLAB),
                        key(ModBlocks.POLISHED_DARK_DRIPSTONE_SLAB),
                        key(ModBlocks.PRISMARINE_TILE_SLAB),
                        key(ModBlocks.LIMESTONE_SLAB),
                        key(ModBlocks.POLISHED_LIMESTONE_SLAB),
                        key(ModBlocks.LIMESTONE_BRICK_SLAB)
                );

        builder(BlockTags.STAIRS)
                .add(
                        key(ModBlocks.DRIPSTONE_STAIRS),
                        key(ModBlocks.DRIPSTONE_BRICK_STAIRS),
                        key(ModBlocks.POLISHED_DRIPSTONE_STAIRS),
                        key(ModBlocks.DARK_DRIPSTONE_STAIRS),
                        key(ModBlocks.DARK_DRIPSTONE_BRICK_STAIRS),
                        key(ModBlocks.POLISHED_DARK_DRIPSTONE_STAIRS),
                        key(ModBlocks.PRISMARINE_TILE_STAIRS),
                        key(ModBlocks.LIMESTONE_STAIRS),
                        key(ModBlocks.POLISHED_LIMESTONE_STAIRS),
                        key(ModBlocks.LIMESTONE_BRICK_STAIRS)
                );

        builder(BlockTags.REPLACEABLE)
                .add(
                        key(ModBlocks.OXYGEN_BUBBLE)
                );

        builder(BlockTags.CONDUIT_EFFECT_BLOCK)
                .add(
                        key(ModBlocks.CHISELED_PRISMARINE_BRICKS),
                        key(ModBlocks.PRISMARINE_TILES),
                        key(ModBlocks.MIXED_PRISMARINE_TILES),
                        key(ModBlocks.CHISELED_DARK_PRISMARINE),
                        key(ModBlocks.PRISMARINE_PILLAR)
                );
    }

    private static ResourceKey<Block> key(Block block) {
        return BuiltInRegistries.BLOCK.wrapAsHolder(block).unwrapKey().orElseThrow();
    }
}