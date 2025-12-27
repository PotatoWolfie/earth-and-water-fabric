package potatowolfie.earth_and_water.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import java.util.UUID;

public class UnderwaterStunEffect extends StatusEffect {
    private static final UUID MOVEMENT_SPEED_MODIFIER_ID = UUID.fromString("7107DE5E-7CE8-4030-940E-514C1F160890");
    private static final UUID ATTACK_DAMAGE_MODIFIER_ID = UUID.fromString("22653B89-116E-49DC-9B6B-9971489B5BE5");

    protected UnderwaterStunEffect(StatusEffectCategory category, int color) {
        super(category, color);

        this.addAttributeModifier(
            EntityAttributes.GENERIC_MOVEMENT_SPEED,
                Identifier.of(MOVEMENT_SPEED_MODIFIER_ID.toString()),
            -0.8,
            EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );

        this.addAttributeModifier(
            EntityAttributes.GENERIC_MOVEMENT_SPEED,
                Identifier.of(ATTACK_DAMAGE_MODIFIER_ID.toString()),
            -0.65,
            EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
    }

    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
