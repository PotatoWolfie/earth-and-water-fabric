package potatowolfie.earth_and_water.world.feature.custom;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.FeatureConfig;

public class OxygenFeatureConfig implements FeatureConfig {
    public static final Codec<OxygenFeatureConfig> CODEC = Codec.unit(OxygenFeatureConfig::new);

    public OxygenFeatureConfig() {
    }
}