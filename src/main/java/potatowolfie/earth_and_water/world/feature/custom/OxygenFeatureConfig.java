package potatowolfie.earth_and_water.world.feature.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.gen.feature.FeatureConfig;

public class OxygenFeatureConfig implements FeatureConfig {
    public static final Codec<OxygenFeatureConfig> CODEC = MapCodec.unit(new OxygenFeatureConfig()).codec();

    public OxygenFeatureConfig() {
    }
}