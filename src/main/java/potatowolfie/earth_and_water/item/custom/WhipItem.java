package potatowolfie.earth_and_water.item.custom;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import potatowolfie.earth_and_water.effect.ModEffects;

import java.util.List;
import java.util.function.Consumer;

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

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.translatable("tooltip.earth-and-water.tooltipempty"));
        textConsumer.accept(Text.translatable("tooltip.earth-and-water.whip.tooltip1"));
        textConsumer.accept(Text.translatable("tooltip.earth-and-water.whip.tooltip2"));
        textConsumer.accept(Text.translatable("tooltip.earth-and-water.tooltipempty"));
        textConsumer.accept(Text.translatable("tooltip.earth-and-water.whip.tooltip3"));
        textConsumer.accept(Text.translatable("tooltip.earth-and-water.whip.tooltip4"));
        super.appendTooltip(stack, context, displayComponent, textConsumer, type);
    }
}