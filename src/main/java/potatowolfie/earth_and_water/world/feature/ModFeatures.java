package potatowolfie.earth_and_water.world.feature;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.world.feature.custom.OxygenFeature;
import potatowolfie.earth_and_water.world.feature.custom.limestone_rock.LimestoneRockFeature;

public class ModFeatures {

    public static final MapCodec<OxygenFeature> OXYGEN_CROSS =
            Registry.register(BuiltInRegistries.FEATURE_TYPE,
                    Identifier.fromNamespaceAndPath(EarthWater.MOD_ID, "oxygen_cross"),
                    OxygenFeature.CODEC);

    public static final MapCodec<LimestoneRockFeature> LIMESTONE_ROCK =
            Registry.register(BuiltInRegistries.FEATURE_TYPE,
                    Identifier.fromNamespaceAndPath(EarthWater.MOD_ID, "limestone_rock"),
                    LimestoneRockFeature.CODEC);

    public static void registerModFeatures() {
        EarthWater.LOGGER.info("Registering Mod Features for " + EarthWater.MOD_ID);
    }
}