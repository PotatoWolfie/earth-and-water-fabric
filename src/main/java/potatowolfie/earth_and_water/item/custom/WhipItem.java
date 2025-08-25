package potatowolfie.earth_and_water.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.FluidTags;
import potatowolfie.earth_and_water.effect.ModEffects;

public class WhipItem extends Item {

    public WhipItem(ToolMaterial toolMaterial, Item.Settings settings) {
        super(settings);
    }

    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target.isSubmergedIn(FluidTags.WATER)) {
            target.addStatusEffect(new StatusEffectInstance(ModEffects.STUN, 40, 1));
        }
        super.postHit(stack, target, attacker);
    }
}