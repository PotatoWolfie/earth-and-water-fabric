package potatowolfie.earth_and_water.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SmithingTemplateItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import potatowolfie.earth_and_water.item.ModItems;

import java.util.List;

@Mixin(SmithingTemplateItem.class)
public class SmithingTemplateItemMixin {

    @Unique
    private static final Text STEEL_UPGRADE_APPLIES_TO_TEXT = Text.translatable(
                    Util.createTranslationKey("item",
                            Identifier.of("earth-and-water", "smithing_template.steel_upgrade.applies_to")))
            .formatted(Formatting.BLUE);

    @Unique
    private static final Text STEEL_UPGRADE_INGREDIENTS_TEXT = Text.translatable(
                    Util.createTranslationKey("item",
                            Identifier.of("earth-and-water", "smithing_template.steel_upgrade.ingredients")))
            .formatted(Formatting.BLUE);

    @Unique
    private static final Text STEEL_UPGRADE_TEXT = Text.translatable(
                    Util.createTranslationKey("item",
                            Identifier.of("earth-and-water", "smithing_template.steel_upgrade.text")))
            .formatted(Formatting.GRAY);

    @Inject(method = "appendTooltip", at = @At("HEAD"), cancellable = true)
    private void injectSteelUpgradeTooltip(ItemStack stack, Item.TooltipContext context,
                                           List<Text> tooltip, TooltipType type,
                                           CallbackInfo ci) {

        if (stack.getItem() == ModItems.STEEL_UPGRADE_SMITHING_TEMPLATE) {
            tooltip.add(STEEL_UPGRADE_TEXT);

            tooltip.add(ScreenTexts.EMPTY);

            tooltip.add(Text.translatable(
                            Util.createTranslationKey("item",
                                    Identifier.ofVanilla("smithing_template.applies_to")))
                    .formatted(Formatting.GRAY));

            tooltip.add(ScreenTexts.space().append(STEEL_UPGRADE_APPLIES_TO_TEXT));

            tooltip.add(Text.translatable(
                            Util.createTranslationKey("item",
                                    Identifier.ofVanilla("smithing_template.ingredients")))
                    .formatted(Formatting.GRAY));

            tooltip.add(ScreenTexts.space().append(STEEL_UPGRADE_INGREDIENTS_TEXT));

            ci.cancel();
        }
    }
}