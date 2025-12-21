package potatowolfie.earth_and_water.world.gen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;
import potatowolfie.earth_and_water.world.feature.ModPlacedFeatures;

public class ModLimestoneRockGeneration {
    public static void addFeaturesToBiomes() {
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(
                        BiomeKeys.LUKEWARM_OCEAN,
                        BiomeKeys.DEEP_LUKEWARM_OCEAN,
                        BiomeKeys.WARM_OCEAN
                ),
                GenerationStep.Feature.VEGETAL_DECORATION,
                ModPlacedFeatures.LIMESTONE_ROCK_PLACED
        );
    }
}