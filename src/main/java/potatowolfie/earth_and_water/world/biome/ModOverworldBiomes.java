package potatowolfie.earth_and_water.world.biome;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.MusicType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;
import potatowolfie.earth_and_water.world.feature.ModPlacedFeatures;

public class ModOverworldBiomes {
    private static void addFeature(GenerationSettings.LookupBackedBuilder builder, GenerationStep.Feature step, RegistryKey<PlacedFeature> feature) {
        builder.feature(step, feature);
    }

    private static void addBasicFeatures(GenerationSettings.LookupBackedBuilder builder) {
        DefaultBiomeFeatures.addLandCarvers(builder);
        DefaultBiomeFeatures.addAmethystGeodes(builder);
        DefaultBiomeFeatures.addDungeons(builder);
        DefaultBiomeFeatures.addMineables(builder);
        DefaultBiomeFeatures.addSprings(builder);
        DefaultBiomeFeatures.addFrozenTopLayer(builder);
    }

    public static Biome darkDripstoneCaves(RegistryEntryLookup<PlacedFeature> placedFeatureGetter, RegistryEntryLookup<ConfiguredCarver<?>> carverGetter) {
        SpawnSettings.Builder spawnBuilder = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addCaveMobs(spawnBuilder);
        DefaultBiomeFeatures.addMonsters(spawnBuilder, 95, 5, 100, false);
        spawnBuilder.spawn(SpawnGroup.MONSTER, 95, new SpawnSettings.SpawnEntry(EntityType.DROWNED, 4, 4));

        GenerationSettings.LookupBackedBuilder biomeBuilder = new GenerationSettings.LookupBackedBuilder(placedFeatureGetter, carverGetter);
        addBasicFeatures(biomeBuilder);
        DefaultBiomeFeatures.addPlainsTallGrass(biomeBuilder);
        DefaultBiomeFeatures.addDefaultOres(biomeBuilder, true);
        DefaultBiomeFeatures.addDefaultDisks(biomeBuilder);
        DefaultBiomeFeatures.addPlainsFeatures(biomeBuilder);
        DefaultBiomeFeatures.addDefaultMushrooms(biomeBuilder);
        DefaultBiomeFeatures.addDefaultVegetation(biomeBuilder, false);
        DefaultBiomeFeatures.addDripstone(biomeBuilder);

        addFeature(biomeBuilder, GenerationStep.Feature.LOCAL_MODIFICATIONS, ModPlacedFeatures.LARGE_DARK_DRIPSTONE_PLACED);
        addFeature(biomeBuilder, GenerationStep.Feature.LOCAL_MODIFICATIONS, ModPlacedFeatures.DARK_DRIPSTONE_CLUSTER_PLACED);
        addFeature(biomeBuilder, GenerationStep.Feature.UNDERGROUND_DECORATION, ModPlacedFeatures.POINTED_DARK_DRIPSTONE_PLACED);

        MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_DRIPSTONE_CAVES);

        return createBiome(true, 0.8F, 0.4F, spawnBuilder, biomeBuilder, musicSound);
    }

    private static Biome createBiome(boolean precipitation, float temperature, float downfall,
                                     SpawnSettings.Builder spawnBuilder,
                                     GenerationSettings.LookupBackedBuilder biomeBuilder,
                                     MusicSound musicSound) {
        return new Biome.Builder()
                .precipitation(precipitation)
                .temperature(temperature)
                .downfall(downfall)
                .effects((new BiomeEffects.Builder())
                        .waterColor(4159204)
                        .waterFogColor(329011)
                        .fogColor(12638463)
                        .skyColor(getSkyColor(temperature))
                        .moodSound(new BiomeMoodSound(SoundEvents.AMBIENT_CAVE, 6000, 8, 2.0D))
                        .music(musicSound)
                        .build())
                .spawnSettings(spawnBuilder.build())
                .generationSettings(biomeBuilder.build())
                .build();
    }

    public static int getSkyColor(float temperature) {
        float f = temperature / 3.0F;
        f = MathHelper.clamp(f, -1.0F, 1.0F);
        return MathHelper.hsvToRgb(0.62222224F - f * 0.05F, 0.5F + f * 0.1F, 1.0F);
    }
}