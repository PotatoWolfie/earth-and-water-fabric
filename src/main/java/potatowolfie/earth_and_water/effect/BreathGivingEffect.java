package potatowolfie.earth_and_water.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BreathGivingEffect extends StatusEffect {
    private static final int UPDATE_INTERVAL = 1;
    private static final int BASE_AIR_PER_UPDATE = 15;
    private static final float PARTIAL_AIR_THRESHOLD = 0.9f;
    private final Map<UUID, Float> partialAirValues = new HashMap<>();

    public BreathGivingEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x3CB4FF);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration % UPDATE_INTERVAL == 0;
    }

    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity == null) {
            return false;
        }

        int currentAir = entity.getAir();
        int maxAir = entity.getMaxAir();
        boolean isCreativeOrSpectator = false;

        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            isCreativeOrSpectator = player.isSpectator() || player.getAbilities().creativeMode;
        }

        if (currentAir < maxAir || isCreativeOrSpectator) {
            UUID entityId = entity.getUuid();
            float partialAir = partialAirValues.getOrDefault(entityId, 0f);
            float airToAddFloat = BASE_AIR_PER_UPDATE * (amplifier + 1);

            partialAir += airToAddFloat;
            int wholeAirToAdd = (int) partialAir;
            float remainder = partialAir - wholeAirToAdd;

            if (remainder > PARTIAL_AIR_THRESHOLD) {
                wholeAirToAdd++;
                remainder = 0;
            }

            partialAirValues.put(entityId, remainder);

            if (wholeAirToAdd > 0) {
                entity.setAir(Math.min(currentAir + wholeAirToAdd, maxAir));
            }

            if (currentAir + wholeAirToAdd >= maxAir && !isCreativeOrSpectator) {
                partialAirValues.remove(entityId);
            }

            return true;
        } else {
            partialAirValues.remove(entity.getUuid());
        }

        return false;
    }
}