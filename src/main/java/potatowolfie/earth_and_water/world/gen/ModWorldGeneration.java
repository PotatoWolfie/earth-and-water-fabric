package potatowolfie.earth_and_water.world.gen;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import potatowolfie.earth_and_water.world.feature.ModConfiguredFeatures;
import potatowolfie.earth_and_water.world.feature.ModPlacedFeatures;

public class ModWorldGeneration {
    public static void registerConfiguredFeatures(BootstrapContext<Feature> context) {
        ModConfiguredFeatures.bootstrap(context);
    }

    public static void registerPlacedFeatures(BootstrapContext<PlacedFeature> context) {
        ModPlacedFeatures.bootstrap(context);
    }

    public static void init() {
        ModOxygenCrossGeneration.addFeatureToBiomes();
        ModDarkDripstoneGeneration.addFeaturesToBiomes();
        ModLimestoneRockGeneration.addFeaturesToBiomes();
    }
}