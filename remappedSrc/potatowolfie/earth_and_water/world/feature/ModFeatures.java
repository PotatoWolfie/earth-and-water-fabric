package potatowolfie.earth_and_water.world.feature;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.Feature;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.world.feature.custom.*;

public class ModFeatures {
    public static final Feature<DarkDripstoneClusterFeatureConfig> DARK_DRIPSTONE_CLUSTER =
            Registry.register(Registries.FEATURE,
                    Identifier.of(EarthWater.MOD_ID, "dark_dripstone_cluster"),
                    new DarkDripstoneClusterFeature(DarkDripstoneClusterFeatureConfig.CODEC));

    public static final Feature<LargeDarkDripstoneFeatureConfig> LARGE_DARK_DRIPSTONE =
            Registry.register(Registries.FEATURE,
                    Identifier.of(EarthWater.MOD_ID, "large_dark_dripstone"),
                    new LargeDarkDripstoneFeature(LargeDarkDripstoneFeatureConfig.CODEC));

    public static final Feature<SmallDarkDripstoneFeatureConfig> POINTED_DARK_DRIPSTONE =
            Registry.register(Registries.FEATURE,
                    Identifier.of(EarthWater.MOD_ID, "pointed_dark_dripstone"),
                    new SmallDarkDripstoneFeature(SmallDarkDripstoneFeatureConfig.CODEC));

    public static final Feature<OxygenFeatureConfig> OXYGEN_CROSS =
            Registry.register(Registries.FEATURE,
                    Identifier.of(EarthWater.MOD_ID, "oxygen_cross"),
                    new OxygenFeature(OxygenFeatureConfig.CODEC));

    public static void registerModFeatures() {
        EarthWater.LOGGER.info("Registering Mod Features for " + EarthWater.MOD_ID);
    }
}