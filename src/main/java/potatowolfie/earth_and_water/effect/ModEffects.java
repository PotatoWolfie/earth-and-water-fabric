package potatowolfie.earth_and_water.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import potatowolfie.earth_and_water.EarthWater;

public class ModEffects {
    public static final RegistryEntry<StatusEffect> STUN = registerStatusEffect("stun",
            new UnderwaterStunEffect(StatusEffectCategory.HARMFUL, 0xedc466)
                    .addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                            Identifier.of(EarthWater.MOD_ID, "underwater_stun"), -0.25f,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final RegistryEntry<StatusEffect> BREATH_GIVING = registerStatusEffect("breath_giving",
            new BreathGivingEffect());


    private static RegistryEntry<StatusEffect> registerStatusEffect(String name, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(EarthWater.MOD_ID, name), statusEffect);
    }

    public static void registerEffects() {
        EarthWater.LOGGER.info("Registering Mod Effects for " + EarthWater.MOD_ID);
    }
}