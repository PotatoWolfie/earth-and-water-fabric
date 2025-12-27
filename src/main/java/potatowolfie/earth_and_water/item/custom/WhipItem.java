package potatowolfie.earth_and_water.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import potatowolfie.earth_and_water.damage.ModDamageTypes;
import potatowolfie.earth_and_water.effect.ModEffects;

import java.util.List;

public class WhipItem extends Item {

    public WhipItem(ToolMaterial toolMaterial, Item.Settings settings) {
        super(settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target.isSubmergedIn(FluidTags.WATER)) {
            target.addStatusEffect(new StatusEffectInstance(ModEffects.STUN, 40, 1));

            if (attacker.getEntityWorld() instanceof ServerWorld serverWorld) {
                DamageSource whipDamage = new DamageSource(
                        serverWorld.getRegistryManager()
                                .get(RegistryKeys.DAMAGE_TYPE)
                                .entryOf(ModDamageTypes.WHIP),
                        attacker
                );
                target.damage(whipDamage, 1.0f);
            }
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.earth-and-water.tooltipempty"));
        tooltip.add(Text.translatable("tooltip.earth-and-water.whip.tooltip1"));
        tooltip.add(Text.translatable("tooltip.earth-and-water.whip.tooltip2"));
        tooltip.add(Text.translatable("tooltip.earth-and-water.tooltipempty"));
        tooltip.add(Text.translatable("tooltip.earth-and-water.whip.tooltip3"));
        tooltip.add(Text.translatable("tooltip.earth-and-water.whip.tooltip4"));
        super.appendTooltip(stack, context, tooltip, type);
    }
}