package potatowolfie.earth_and_water.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import potatowolfie.earth_and_water.block.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(
                        ModBlocks.DRIPSTONE_STAIRS,
                        ModBlocks.DRIPSTONE_SLAB,
                        ModBlocks.DRIPSTONE_WALL,
                        ModBlocks.DRIPSTONE_BRICKS,
                        ModBlocks.DRIPSTONE_BRICK_STAIRS,
                        ModBlocks.DRIPSTONE_BRICK_SLAB,
                        ModBlocks.DRIPSTONE_BRICK_WALL,
                        ModBlocks.POLISHED_DRIPSTONE,
                        ModBlocks.POLISHED_DRIPSTONE_STAIRS,
                        ModBlocks.POLISHED_DRIPSTONE_SLAB,
                        ModBlocks.POLISHED_DRIPSTONE_WALL,
                        ModBlocks.POLISHED_DRIPSTONE_TILES,
                        ModBlocks.DRIPSTONE_PILLAR,
                        ModBlocks.CHISELED_DRIPSTONE_BRICKS,
                        ModBlocks.DARK_DRIPSTONE_BLOCK,
                        ModBlocks.DARK_DRIPSTONE_STAIRS,
                        ModBlocks.DARK_DRIPSTONE_SLAB,
                        ModBlocks.DARK_DRIPSTONE_WALL,
                        ModBlocks.DARK_DRIPSTONE_BRICKS,
                        ModBlocks.DARK_DRIPSTONE_BRICK_STAIRS,
                        ModBlocks.DARK_DRIPSTONE_BRICK_SLAB,
                        ModBlocks.DARK_DRIPSTONE_BRICK_WALL,
                        ModBlocks.POLISHED_DARK_DRIPSTONE,
                        ModBlocks.POLISHED_DARK_DRIPSTONE_STAIRS,
                        ModBlocks.POLISHED_DARK_DRIPSTONE_SLAB,
                        ModBlocks.POLISHED_DARK_DRIPSTONE_WALL,
                        ModBlocks.DARK_DRIPSTONE_PILLAR,
                        ModBlocks.POINTED_DARK_DRIPSTONE,
                        ModBlocks.DARK_PRISMARINE_PILLAR,
                        ModBlocks.CHISELED_PRISMARINE_BRICKS,
                        ModBlocks.PRISMARINE_PILLAR,
                        ModBlocks.PRISMARINE_TILES,
                        ModBlocks.PRISMARINE_TILE_STAIRS,
                        ModBlocks.PRISMARINE_TILE_SLAB,
                        ModBlocks.PRISMARINE_TILE_WALL,
                        ModBlocks.MIXED_PRISMARINE_TILES,
                        ModBlocks.CHISELED_DARK_PRISMARINE,
                        ModBlocks.DARK_PRISMARINE_WALL,
                        ModBlocks.OXYGEN_BLOCK,
                        ModBlocks.LIMESTONE,
                        ModBlocks.LIMESTONE_STAIRS,
                        ModBlocks.LIMESTONE_SLAB,
                        ModBlocks.LIMESTONE_WALL,
                        ModBlocks.POLISHED_LIMESTONE,
                        ModBlocks.POLISHED_LIMESTONE_STAIRS,
                        ModBlocks.POLISHED_LIMESTONE_SLAB,
                        ModBlocks.POLISHED_LIMESTONE_WALL,
                        ModBlocks.LIMESTONE_BRICKS,
                        ModBlocks.LIMESTONE_BRICK_STAIRS,
                        ModBlocks.LIMESTONE_BRICK_SLAB,
                        ModBlocks.LIMESTONE_BRICK_WALL,
                        ModBlocks.LIMESTONE_PILLAR,
                        ModBlocks.CHISELED_LIMESTONE_BRICKS
                );

        getOrCreateTagBuilder(BlockTags.WALLS)
                .add(ModBlocks.POLISHED_DRIPSTONE_WALL,
                        ModBlocks.DRIPSTONE_WALL,
                        ModBlocks.PRISMARINE_TILE_WALL,
                        ModBlocks.DARK_DRIPSTONE_WALL,
                        ModBlocks.POLISHED_DARK_DRIPSTONE_WALL,
                        ModBlocks.DARK_DRIPSTONE_BRICK_WALL,
                        ModBlocks.DARK_PRISMARINE_WALL,
                        ModBlocks.LIMESTONE_WALL,
                        ModBlocks.POLISHED_LIMESTONE_WALL,
                        ModBlocks.LIMESTONE_BRICK_WALL
                );

        getOrCreateTagBuilder(BlockTags.SLABS)
                .add(ModBlocks.DRIPSTONE_SLAB,
                        ModBlocks.DRIPSTONE_BRICK_SLAB,
                        ModBlocks.POLISHED_DRIPSTONE_SLAB,
                        ModBlocks.DARK_DRIPSTONE_SLAB,
                        ModBlocks.DARK_DRIPSTONE_BRICK_SLAB,
                        ModBlocks.POLISHED_DARK_DRIPSTONE_SLAB,
                        ModBlocks.PRISMARINE_TILE_SLAB,
                        ModBlocks.LIMESTONE_SLAB,
                        ModBlocks.POLISHED_LIMESTONE_SLAB,
                        ModBlocks.LIMESTONE_BRICK_SLAB
                );

        getOrCreateTagBuilder(BlockTags.STAIRS)
                .add(ModBlocks.DRIPSTONE_STAIRS,
                        ModBlocks.DRIPSTONE_BRICK_STAIRS,
                        ModBlocks.POLISHED_DRIPSTONE_STAIRS,
                        ModBlocks.DARK_DRIPSTONE_STAIRS,
                        ModBlocks.DARK_DRIPSTONE_BRICK_STAIRS,
                        ModBlocks.POLISHED_DARK_DRIPSTONE_STAIRS,
                        ModBlocks.PRISMARINE_TILE_STAIRS,
                        ModBlocks.LIMESTONE_STAIRS,
                        ModBlocks.POLISHED_LIMESTONE_STAIRS,
                        ModBlocks.LIMESTONE_BRICK_STAIRS
                );

        getOrCreateTagBuilder(BlockTags.REPLACEABLE)
                .add(ModBlocks.OXYGEN_BUBBLE
                );
    }
}