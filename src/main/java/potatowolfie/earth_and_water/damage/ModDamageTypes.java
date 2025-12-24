package potatowolfie.earth_and_water.damage;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import potatowolfie.earth_and_water.EarthWater;

public class ModDamageTypes {
    public static final RegistryKey<DamageType> BATTLE_AXE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(EarthWater.MOD_ID, "battle_axe"));
    public static final RegistryKey<DamageType> WHIP = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(EarthWater.MOD_ID, "whip"));
    public static final RegistryKey<DamageType> EARTH_CHARGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(EarthWater.MOD_ID, "earth_charge"));
    public static final RegistryKey<DamageType> WATER_CHARGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(EarthWater.MOD_ID, "water_charge"));
    public static final RegistryKey<DamageType> SPIKED_SHIELD = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(EarthWater.MOD_ID, "spiked_shield"));
}