package potatowolfie.earth_and_water.world.feature.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;

public class DarkDripstoneClusterFeatureConfig implements FeatureConfig {
    public static final Codec<DarkDripstoneClusterFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                Codec.intRange(1, 512).fieldOf("floor_to_ceiling_search_range").forGetter((config) -> {
                    return config.floorToCeilingSearchRange;
                }),
                IntProvider.createValidatingCodec(1, 128).fieldOf("height").forGetter((config) -> {
                    return config.height;
                }),
                IntProvider.createValidatingCodec(1, 128).fieldOf("radius").forGetter((config) -> {
                    return config.radius;
                }),
                Codec.intRange(0, 64).fieldOf("max_stalagmite_stalactite_height_diff").forGetter((config) -> {
                    return config.maxStalagmiteStalactiteHeightDiff;
                }),
                Codec.intRange(1, 64).fieldOf("height_deviation").forGetter((config) -> {
                    return config.heightDeviation;
                }),
                IntProvider.createValidatingCodec(0, 128).fieldOf("dripstone_block_layer_thickness").forGetter((config) -> {
                    return config.dripstoneBlockLayerThickness;
                }),
                FloatProvider.createValidatedCodec(0.0F, 2.0F).fieldOf("density").forGetter((config) -> {
                    return config.density;
                }),
                FloatProvider.createValidatedCodec(0.0F, 2.0F).fieldOf("wetness").forGetter((config) -> {
                    return config.wetness;
                }),
                Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_dripstone_column_at_max_distance_from_center").forGetter((config) -> {
                    return config.chanceOfDripstoneColumnAtMaxDistanceFromCenter;
                }),
                Codec.intRange(1, 64).fieldOf("max_distance_from_edge_affecting_chance_of_dripstone_column").forGetter((config) -> {
                    return config.maxDistanceFromCenterAffectingChanceOfDripstoneColumn;
                }),
                Codec.intRange(1, 64).fieldOf("max_distance_from_center_affecting_height_bias").forGetter((config) -> {
                    return config.maxDistanceFromCenterAffectingHeightBias;
                })
        ).apply(instance, DarkDripstoneClusterFeatureConfig::new);
    });

    public final int floorToCeilingSearchRange;
    public final IntProvider height;
    public final IntProvider radius;
    public final int maxStalagmiteStalactiteHeightDiff;
    public final int heightDeviation;
    public final IntProvider dripstoneBlockLayerThickness;
    public final FloatProvider density;
    public final FloatProvider wetness;
    public final float chanceOfDripstoneColumnAtMaxDistanceFromCenter;
    public final int maxDistanceFromCenterAffectingChanceOfDripstoneColumn;
    public final int maxDistanceFromCenterAffectingHeightBias;

    public DarkDripstoneClusterFeatureConfig(int floorToCeilingSearchRange, IntProvider height, IntProvider radius, int maxStalagmiteStalactiteHeightDiff, int heightDeviation, IntProvider dripstoneBlockLayerThickness, FloatProvider density, FloatProvider wetness, float chanceOfDripstoneColumnAtMaxDistanceFromCenter, int maxDistanceFromCenterAffectingChanceOfDripstoneColumn, int maxDistanceFromCenterAffectingHeightBias) {
        this.floorToCeilingSearchRange = floorToCeilingSearchRange;
        this.height = height;
        this.radius = radius;
        this.maxStalagmiteStalactiteHeightDiff = maxStalagmiteStalactiteHeightDiff;
        this.heightDeviation = heightDeviation;
        this.dripstoneBlockLayerThickness = dripstoneBlockLayerThickness;
        this.density = density;
        this.wetness = wetness;
        this.chanceOfDripstoneColumnAtMaxDistanceFromCenter = chanceOfDripstoneColumnAtMaxDistanceFromCenter;
        this.maxDistanceFromCenterAffectingChanceOfDripstoneColumn = maxDistanceFromCenterAffectingChanceOfDripstoneColumn;
        this.maxDistanceFromCenterAffectingHeightBias = maxDistanceFromCenterAffectingHeightBias;
    }
}