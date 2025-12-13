package potatowolfie.earth_and_water.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ShieldDecorationRecipe;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import potatowolfie.earth_and_water.util.ModTags;

// Code used from "More Shield Variants" by hypothetiKal and pnku under a MIT License

@Mixin(ShieldDecorationRecipe.class)
public abstract class ShieldDecorationRecipeMixin extends SpecialCraftingRecipe {

    public ShieldDecorationRecipeMixin(CraftingRecipeCategory category) {
        super(category);
    }

    @Inject(method = "matches", at = @At("HEAD"), cancellable = true)
    private void injectedMatches(CraftingRecipeInput input, World world, CallbackInfoReturnable<Boolean> cir) {
        ItemStack shield = ItemStack.EMPTY;
        ItemStack banner = ItemStack.EMPTY;
        boolean hasSpikedShield = false;

        for (int i = 0; i < input.size(); ++i) {
            ItemStack stack = input.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            if (stack.isIn(ModTags.Item.SPIKED_SHIELD)) {
                hasSpikedShield = true;
                break;
            }
        }

        if (!hasSpikedShield) {
            return;
        }

        for (int i = 0; i < input.size(); ++i) {
            ItemStack stack = input.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof BannerItem) {
                if (!banner.isEmpty()) {
                    cir.setReturnValue(false);
                    return;
                }
                banner = stack;
            } else if (stack.isIn(ModTags.Item.SPIKED_SHIELD)) {
                if (!shield.isEmpty()) {
                    cir.setReturnValue(false);
                    return;
                }

                BannerPatternsComponent patterns = stack.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT);
                if (!patterns.layers().isEmpty()) {
                    cir.setReturnValue(false);
                    return;
                }

                shield = stack;
            } else {
                cir.setReturnValue(false);
                return;
            }
        }

        cir.setReturnValue(!shield.isEmpty() && !banner.isEmpty());
    }

    @Inject(method = "craft", at = @At("HEAD"), cancellable = true)
    private void injectedCraft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup lookup, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack banner = ItemStack.EMPTY;
        ItemStack shield = ItemStack.EMPTY;
        boolean hasSpikedShield = false;

        for (int i = 0; i < input.size(); ++i) {
            ItemStack stack = input.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            if (stack.isIn(ModTags.Item.SPIKED_SHIELD)) {
                hasSpikedShield = true;
                shield = stack.copy();
            } else if (stack.getItem() instanceof BannerItem) {
                banner = stack;
            }
        }

        if (!hasSpikedShield) {
            return;
        }

        if (shield.isEmpty()) {
            cir.setReturnValue(ItemStack.EMPTY);
            return;
        }

        shield.set(DataComponentTypes.BANNER_PATTERNS, banner.get(DataComponentTypes.BANNER_PATTERNS));
        shield.set(DataComponentTypes.BASE_COLOR, ((BannerItem) banner.getItem()).getColor());
        cir.setReturnValue(shield);
    }
}