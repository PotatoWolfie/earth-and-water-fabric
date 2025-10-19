package potatowolfie.earth_and_water.world.gen;

import net.minecraft.registry.Registerable;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import potatowolfie.earth_and_water.world.feature.ModConfiguredFeatures;
import potatowolfie.earth_and_water.world.feature.ModPlacedFeatures;

public class ModWorldGeneration {
    public static void registerConfiguredFeatures(Registerable<ConfiguredFeature<?, ?>> context) {
        ModConfiguredFeatures.bootstrap(context);
    }

    public static void registerPlacedFeatures(Registerable<PlacedFeature> context) {
        ModPlacedFeatures.bootstrap(context);
    }

    public static void init() {
        ModOxygenCrossGeneration.addFeatureToBiomes();
        ModDarkDripstoneGeneration.addFeaturesToBiomes();
    }
}