package potatowolfie.earth_and_water.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import potatowolfie.earth_and_water.EarthWater;

public class ModSounds {
    public static final SoundEvent BATTLE_AXE_DASH = registerSoundEvent("battle_axe_dash");


    private static SoundEvent registerSoundEvent(String name) {
        return Registry.register(Registries.SOUND_EVENT, Identifier.of(EarthWater.MOD_ID, name),
                SoundEvent.of(Identifier.of(EarthWater.MOD_ID, name)));
    }

    public static void registerSounds() {
        EarthWater.LOGGER.info("Registering Mod Sounds for " + EarthWater.MOD_ID);
    }
}