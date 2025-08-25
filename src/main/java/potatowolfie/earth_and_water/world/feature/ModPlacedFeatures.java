package potatowolfie.earth_and_water.world.feature;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.*;
import potatowolfie.earth_and_water.EarthWater;

import java.util.List;

public class ModPlacedFeatures {

    public static final RegistryKey<PlacedFeature> DARK_DRIPSTONE_CLUSTER_PLACED = registerKey("dark_dripstone_cluster");
    public static final RegistryKey<PlacedFeature> LARGE_DARK_DRIPSTONE_PLACED = registerKey("large_dark_dripstone");
    public static final RegistryKey<PlacedFeature> SMALL_DARK_DRIPSTONE_PLACED = registerKey("small_dark_dripstone");
    public static final RegistryKey<PlacedFeature> POINTED_DARK_DRIPSTONE_PLACED = registerKey("pointed_dark_dripstone");
    public static final RegistryKey<PlacedFeature> OXYGEN_CROSS_PLACED = registerKey("oxygen_cross");

    public static void bootstrap(Registerable<PlacedFeature> context) {
        RegistryEntryLookup<ConfiguredFeature<?, ?>> configuredFeatureRegistryEntryLookup =
                context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        register(context, DARK_DRIPSTONE_CLUSTER_PLACED,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.DARK_DRIPSTONE_CLUSTER),
                List.of(
                        CountPlacementModifier.of(8),
                        SquarePlacementModifier.of(),
                        HeightRangePlacementModifier.uniform(net.minecraft.world.gen.YOffset.fixed(-64), net.minecraft.world.gen.YOffset.fixed(60)),
                        EnvironmentScanPlacementModifier.of(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.IS_AIR, 12),
                        RandomOffsetPlacementModifier.vertically(net.minecraft.util.math.intprovider.UniformIntProvider.create(1, 2)),
                        BiomePlacementModifier.of()
                ));

        register(context, LARGE_DARK_DRIPSTONE_PLACED,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.LARGE_DARK_DRIPSTONE),
                List.of(
                        CountPlacementModifier.of(4),
                        SquarePlacementModifier.of(),
                        HeightRangePlacementModifier.uniform(net.minecraft.world.gen.YOffset.fixed(-64), net.minecraft.world.gen.YOffset.fixed(60)),
                        EnvironmentScanPlacementModifier.of(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.IS_AIR, 12),
                        RandomOffsetPlacementModifier.vertically(net.minecraft.util.math.intprovider.UniformIntProvider.create(1, 2)),
                        BiomePlacementModifier.of()
                ));

        register(context, SMALL_DARK_DRIPSTONE_PLACED,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.SMALL_DARK_DRIPSTONE),
                List.of(
                        CountPlacementModifier.of(12),
                        SquarePlacementModifier.of(),
                        HeightRangePlacementModifier.uniform(net.minecraft.world.gen.YOffset.fixed(-64), net.minecraft.world.gen.YOffset.fixed(60)),
                        EnvironmentScanPlacementModifier.of(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.IS_AIR, 12),
                        RandomOffsetPlacementModifier.vertically(net.minecraft.util.math.intprovider.UniformIntProvider.create(1, 2)),
                        BiomePlacementModifier.of()
                ));

        register(context, POINTED_DARK_DRIPSTONE_PLACED,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.POINTED_DARK_DRIPSTONE),
                List.of(
                        CountPlacementModifier.of(20),
                        SquarePlacementModifier.of(),
                        HeightRangePlacementModifier.uniform(net.minecraft.world.gen.YOffset.fixed(-64), net.minecraft.world.gen.YOffset.fixed(60)),
                        EnvironmentScanPlacementModifier.of(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.IS_AIR, 12),
                        EnvironmentScanPlacementModifier.of(Direction.UP, BlockPredicate.solid(), BlockPredicate.IS_AIR, 12),
                        RandomOffsetPlacementModifier.vertically(net.minecraft.util.math.intprovider.UniformIntProvider.create(1, 2)),
                        RarityFilterPlacementModifier.of(2),
                        BiomePlacementModifier.of()
                ));

        register(context, OXYGEN_CROSS_PLACED,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.OXYGEN_CROSS),
                List.of(
                        CountPlacementModifier.of(1),
                        SquarePlacementModifier.of(),
                        RarityFilterPlacementModifier.of(12),
                        HeightmapPlacementModifier.of(Heightmap.Type.OCEAN_FLOOR_WG),
                        BiomePlacementModifier.of()
                )
        );
    }

    public static RegistryKey<PlacedFeature> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(EarthWater.MOD_ID, name));
    }

    private static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key,
                                 RegistryEntry<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key,
                                                                                   RegistryEntry<ConfiguredFeature<?, ?>> configuration,
                                                                                   PlacementModifier... modifiers) {
        register(context, key, configuration, List.of(modifiers));
    }
}