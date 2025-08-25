package potatowolfie.earth_and_water.world.biome.region;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import potatowolfie.earth_and_water.world.biome.ModBiomes;
import terrablender.api.ParameterUtils;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.VanillaParameterOverlayBuilder;

import java.util.function.Consumer;

import static terrablender.api.ParameterUtils.*;

public class OverworldRegion extends Region {
    public OverworldRegion(Identifier name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<com.mojang.datafixers.util.Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> mapper) {
        VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();

        new ParameterUtils.ParameterPointListBuilder()
                .temperature(Temperature.span(Temperature.COOL, Temperature.NEUTRAL))
                .humidity(Humidity.span(Humidity.ARID, Humidity.DRY))
                .continentalness(Continentalness.span(Continentalness.INLAND, Continentalness.FAR_INLAND))
                .erosion(Erosion.span(Erosion.EROSION_1, Erosion.EROSION_4))
                .depth(Depth.UNDERGROUND)
                .weirdness(Weirdness.span(Weirdness.LOW_SLICE_NORMAL_DESCENDING, Weirdness.LOW_SLICE_VARIANT_ASCENDING))
                .build().forEach(point -> builder.add(point, ModBiomes.DARK_DRIPSTONE_CAVES));

        new ParameterUtils.ParameterPointListBuilder()
                .temperature(Temperature.span(Temperature.NEUTRAL, Temperature.WARM))
                .humidity(Humidity.span(Humidity.DRY, Humidity.NEUTRAL))
                .continentalness(Continentalness.span(Continentalness.NEAR_INLAND, Continentalness.INLAND))
                .erosion(Erosion.span(Erosion.EROSION_2, Erosion.EROSION_5))
                .depth(Depth.UNDERGROUND)
                .weirdness(Weirdness.span(Weirdness.MID_SLICE_NORMAL_ASCENDING, Weirdness.HIGH_SLICE_NORMAL_ASCENDING))
                .build().forEach(point -> builder.add(point, ModBiomes.DARK_DRIPSTONE_CAVES));

        new ParameterUtils.ParameterPointListBuilder()
                .temperature(Temperature.COOL)
                .humidity(Humidity.DRY)
                .continentalness(Continentalness.INLAND)
                .erosion(Erosion.EROSION_3)
                .depth(Depth.UNDERGROUND)
                .weirdness(Weirdness.MID_SLICE_VARIANT_ASCENDING)
                .build().forEach(point -> builder.add(point, ModBiomes.DARK_DRIPSTONE_CAVES));

        builder.build().forEach(mapper);
    }
}