package potatowolfie.earth_and_water.world.feature.custom.limestone_rock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.gen.feature.FeatureConfig;

public class LimestoneRockFeatureConfig implements FeatureConfig {
    public static final Codec<LimestoneRockFeatureConfig> CODEC = MapCodec.unit(new LimestoneRockFeatureConfig()).codec();

    public LimestoneRockFeatureConfig() {
    }
}