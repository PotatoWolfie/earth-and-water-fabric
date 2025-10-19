package potatowolfie.earth_and_water.world.feature;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.ClampedNormalIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.*;
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
                        CountPlacementModifier.of(net.minecraft.util.math.intprovider.UniformIntProvider.create(14, 29)),
                        SquarePlacementModifier.of(),
                        HeightRangePlacementModifier.uniform(YOffset.aboveBottom(0), YOffset.fixed(256)),
                        BiomePlacementModifier.of()
                ));

        register(context, LARGE_DARK_DRIPSTONE_PLACED,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.LARGE_DARK_DRIPSTONE),
                List.of(
                        CountPlacementModifier.of(net.minecraft.util.math.intprovider.UniformIntProvider.create(3, 14)),
                        SquarePlacementModifier.of(),
                        HeightRangePlacementModifier.uniform(YOffset.aboveBottom(0), YOffset.fixed(256)),
                        BiomePlacementModifier.of()
                ));

        register(context, POINTED_DARK_DRIPSTONE_PLACED,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.POINTED_DARK_DRIPSTONE),
                List.of(
                        new PlacementModifier[]{
                                CountPlacementModifier.of(UniformIntProvider.create(192, 256)),
                                SquarePlacementModifier.of(),
                                PlacedFeatures.BOTTOM_TO_120_RANGE,
                                CountPlacementModifier.of(UniformIntProvider.create(1, 5)),
                                RandomOffsetPlacementModifier.of
                                        (ClampedNormalIntProvider.of(0.0F, 3.0F, -10, 10),
                                                ClampedNormalIntProvider.of(0.0F, 0.6F, -2, 2)),
                                BiomePlacementModifier.of()}));

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