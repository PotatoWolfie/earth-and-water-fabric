package potatowolfie.earth_and_water.world.gen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;
import potatowolfie.earth_and_water.world.feature.ModPlacedFeatures;

public class ModDarkDripstoneGeneration {
    public static void addFeaturesToBiomes() {
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(BiomeKeys.DRIPSTONE_CAVES),
                GenerationStep.Feature.UNDERGROUND_DECORATION,
                ModPlacedFeatures.DARK_DRIPSTONE_CLUSTER_PLACED
        );

        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(BiomeKeys.DRIPSTONE_CAVES),
                GenerationStep.Feature.UNDERGROUND_DECORATION,
                ModPlacedFeatures.LARGE_DARK_DRIPSTONE_PLACED
        );

        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(BiomeKeys.DRIPSTONE_CAVES),
                GenerationStep.Feature.UNDERGROUND_DECORATION,
                ModPlacedFeatures.POINTED_DARK_DRIPSTONE_PLACED
        );
    }
}