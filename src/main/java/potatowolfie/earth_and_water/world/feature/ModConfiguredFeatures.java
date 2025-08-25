package potatowolfie.earth_and_water.world.feature;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.floatprovider.TrapezoidFloatProvider;
import net.minecraft.util.math.floatprovider.UniformFloatProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.feature.*;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.world.feature.custom.DarkDripstoneClusterFeatureConfig;
import potatowolfie.earth_and_water.world.feature.custom.LargeDarkDripstoneFeatureConfig;
import potatowolfie.earth_and_water.world.feature.custom.OxygenFeatureConfig;
import potatowolfie.earth_and_water.world.feature.custom.SmallDarkDripstoneFeatureConfig;

public class ModConfiguredFeatures {

    public static final RegistryKey<ConfiguredFeature<?, ?>> DARK_DRIPSTONE_CLUSTER = registerKey("dark_dripstone_cluster");
    public static final RegistryKey<ConfiguredFeature<?, ?>> LARGE_DARK_DRIPSTONE = registerKey("large_dark_dripstone");
    public static final RegistryKey<ConfiguredFeature<?, ?>> SMALL_DARK_DRIPSTONE = registerKey("small_dark_dripstone");
    public static final RegistryKey<ConfiguredFeature<?, ?>> POINTED_DARK_DRIPSTONE = registerKey("pointed_dark_dripstone");
    public static final RegistryKey<ConfiguredFeature<?, ?>> OXYGEN_CROSS = registerKey("oxygen_cross");

    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
        register(context, DARK_DRIPSTONE_CLUSTER, ModFeatures.DARK_DRIPSTONE_CLUSTER,
                new DarkDripstoneClusterFeatureConfig(
                        30,
                        UniformIntProvider.create(3, 19),
                        UniformIntProvider.create(2, 8),
                        8,
                        16,
                        UniformIntProvider.create(0, 2),
                        UniformFloatProvider.create(0.3F, 0.7F),
                        TrapezoidFloatProvider.create(0.2F, 0.7F, 0.5F),
                        0.02F,
                        13,
                        16
                ));

        register(context, LARGE_DARK_DRIPSTONE, ModFeatures.LARGE_DARK_DRIPSTONE,
                new LargeDarkDripstoneFeatureConfig(
                        30,
                        UniformIntProvider.create(3, 19),
                        UniformFloatProvider.create(0.5F, 1.0F),
                        0.33F,
                        UniformFloatProvider.create(0.2F, 0.7F),
                        UniformFloatProvider.create(0.2F, 0.7F),
                        UniformFloatProvider.create(0.5F, 1.5F),
                        1,
                        0.0F
                ));

        register(context, SMALL_DARK_DRIPSTONE, ModFeatures.SMALL_DARK_DRIPSTONE,
                new SmallDarkDripstoneFeatureConfig(
                        0.2F,
                        0.7F,
                        0.5F,
                        0.3F
                ));

        register(context, POINTED_DARK_DRIPSTONE, ModFeatures.SMALL_DARK_DRIPSTONE,
                new SmallDarkDripstoneFeatureConfig(
                        0.1F,
                        0.3F,
                        0.2F,
                        0.1F
                ));

        register(context, OXYGEN_CROSS, ModFeatures.OXYGEN_CROSS,
                new OxygenFeatureConfig());
    }

    public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.of(EarthWater.MOD_ID, name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(
            Registerable<ConfiguredFeature<?, ?>> context,
            RegistryKey<ConfiguredFeature<?, ?>> key,
            F feature,
            FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}