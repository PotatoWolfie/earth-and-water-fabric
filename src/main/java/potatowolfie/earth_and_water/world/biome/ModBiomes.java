package potatowolfie.earth_and_water.world.biome;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.world.biome.region.OverworldRegion;
import terrablender.api.Regions;

public class ModBiomes {

    public static final RegistryKey<Biome> DARK_DRIPSTONE_CAVES = registerBiomeKey("dark_dripstone_cave");

    public static void registerBiomes() {
        Regions.register(new OverworldRegion(Identifier.of(EarthWater.MOD_ID, "earth_and_water_overworld"), 15));


    }

    public static void bootstrap(Registerable<Biome> context) {
        var carver = context.getRegistryLookup(RegistryKeys.CONFIGURED_CARVER);
        var placedFeatures = context.getRegistryLookup(RegistryKeys.PLACED_FEATURE);

        register(context, DARK_DRIPSTONE_CAVES, ModOverworldBiomes.darkDripstoneCaves(placedFeatures, carver));
    }

    private static void register(Registerable<Biome> context, RegistryKey<Biome> key, Biome biome) {
        context.register(key, biome);
    }

    private static RegistryKey<Biome> registerBiomeKey(String name) {
        return RegistryKey.of(RegistryKeys.BIOME, Identifier.of(EarthWater.MOD_ID, name));
    }
}