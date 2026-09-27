package potatowolfie.earth_and_water.world.feature;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ClampedNormalFloat;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.LargeDripstoneFeature;
import net.minecraft.world.level.levelgen.feature.SimpleRandomSelectorFeature;
import net.minecraft.world.level.levelgen.feature.SpeleothemClusterFeature;
import net.minecraft.world.level.levelgen.feature.SpeleothemFeature;
import net.minecraft.world.level.levelgen.placement.EnvironmentScanPlacement;
import net.minecraft.world.level.levelgen.placement.OffsetPlacement;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.block.ModBlocks;
import potatowolfie.earth_and_water.world.feature.custom.OxygenFeature;
import potatowolfie.earth_and_water.world.feature.custom.limestone_rock.LimestoneRockFeature;

public class ModConfiguredFeatures {

    public static final ResourceKey<Feature> DARK_DRIPSTONE_CLUSTER = registerKey("dark_dripstone_cluster");
    public static final ResourceKey<Feature> LARGE_DARK_DRIPSTONE = registerKey("large_dark_dripstone");
    public static final ResourceKey<Feature> POINTED_DARK_DRIPSTONE = registerKey("pointed_dark_dripstone");
    public static final ResourceKey<Feature> OXYGEN_CROSS = registerKey("oxygen_cross");
    public static final ResourceKey<Feature> LIMESTONE_ROCK = registerKey("limestone_rock");

    public static void bootstrap(BootstrapContext<Feature> context) {
        HolderGetter<Block> blocks = context.lookup(Registries.BLOCK);

        HolderSet.Named<Block> replaceableTag = blocks.getOrThrow(
                BlockTags.DRIPSTONE_REPLACEABLE
        );

        context.register(DARK_DRIPSTONE_CLUSTER, new SpeleothemClusterFeature(
                ModBlocks.DARK_DRIPSTONE_BLOCK.defaultBlockState(),
                ModBlocks.POINTED_DARK_DRIPSTONE.defaultBlockState(),
                replaceableTag,
                30,
                UniformInt.of(3, 19),
                UniformInt.of(2, 8),
                8,
                16,
                UniformInt.of(0, 2),
                UniformFloat.of(0.3F, 0.7F),
                ClampedNormalFloat.of(0.2F, 0.7F, 0.0F, 1.0F),
                0.02F,
                13,
                16
        ));

        context.register(LARGE_DARK_DRIPSTONE, new LargeDripstoneFeature(
                replaceableTag,
                30,
                UniformInt.of(3, 15),
                UniformFloat.of(0.4F, 2.0F),
                0.33F,
                UniformFloat.of(0.3F, 0.9F),
                UniformFloat.of(0.4F, 1.0F),
                UniformFloat.of(0.0F, 0.3F),
                4,
                0.6F
        ));

        context.register(POINTED_DARK_DRIPSTONE, new SimpleRandomSelectorFeature(HolderSet.direct(new Holder[]{
                PlacementUtils.inlinePlaced(
                        new SpeleothemFeature(
                                ModBlocks.DARK_DRIPSTONE_BLOCK.defaultBlockState(),
                                ModBlocks.POINTED_DARK_DRIPSTONE.defaultBlockState(),
                                replaceableTag,
                                0.2F,
                                0.7F,
                                0.5F,
                                0.5F
                        ),
                        new PlacementModifier[]{
                                EnvironmentScanPlacement.scanningFor(
                                        Direction.DOWN,
                                        BlockPredicate.solid(),
                                        BlockPredicate.ONLY_IN_AIR_OR_WATER_PREDICATE,
                                        12),
                                OffsetPlacement.vertical(ConstantInt.of(1))
                        }),
                PlacementUtils.inlinePlaced(
                        new SpeleothemFeature(
                                ModBlocks.DARK_DRIPSTONE_BLOCK.defaultBlockState(),
                                ModBlocks.POINTED_DARK_DRIPSTONE.defaultBlockState(),
                                replaceableTag,
                                0.2F,
                                0.7F,
                                0.5F,
                                0.5F
                        ),
                        new PlacementModifier[]{
                                EnvironmentScanPlacement.scanningFor(
                                        Direction.UP,
                                        BlockPredicate.solid(),
                                        BlockPredicate.ONLY_IN_AIR_OR_WATER_PREDICATE,
                                        12),
                                OffsetPlacement.vertical(ConstantInt.of(-1))
                        })
        })));

        context.register(OXYGEN_CROSS, new OxygenFeature());

        context.register(LIMESTONE_ROCK, new LimestoneRockFeature());
    }

    public static ResourceKey<Feature> registerKey(String name) {
        return ResourceKey.create(Registries.FEATURE, Identifier.fromNamespaceAndPath(EarthWater.MOD_ID, name));
    }
}