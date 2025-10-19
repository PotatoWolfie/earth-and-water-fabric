package potatowolfie.earth_and_water.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import potatowolfie.earth_and_water.item.ModItems;
import potatowolfie.earth_and_water.item.custom.BattleAxeItem;
import potatowolfie.earth_and_water.item.custom.SpikedShieldItem;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Unique
    private static final int SHIELD_DISABLE_DURATION = 100;

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onDamage(ServerWorld world, DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (damageSource.getAttacker() instanceof LivingEntity attacker) {
            ItemStack weapon = attacker.getMainHandStack();

            if (weapon.getItem() instanceof BattleAxeItem) {
                if (self.isBlocking()) {
                    ItemStack shieldStack = self.getActiveItem();
                    Item shieldItem = shieldStack.getItem();

                    if (shieldItem instanceof ShieldItem || shieldItem instanceof SpikedShieldItem) {
                        self.getEntityWorld().playSound(null,
                                self.getX(), self.getY(), self.getZ(),
                                SoundEvents.ITEM_SHIELD_BREAK,
                                SoundCategory.PLAYERS,
                                0.8F,
                                0.8F + self.getEntityWorld().getRandom().nextFloat() * 0.4F);

                        if (self instanceof PlayerEntity playerTarget) {
                            playerTarget.getItemCooldownManager().set(new ItemStack(Items.SHIELD), SHIELD_DISABLE_DURATION);

                            if (ModItems.SPIKED_SHIELD != null) {
                                playerTarget.getItemCooldownManager().set(new ItemStack(ModItems.SPIKED_SHIELD), SHIELD_DISABLE_DURATION);
                            }

                            playerTarget.clearActiveItem();
                            playerTarget.getEntityWorld().sendEntityStatus(playerTarget, (byte)30);
                        } else {
                            self.clearActiveItem();
                            self.getEntityWorld().sendEntityStatus(self, (byte)30);
                        }

                        return;
                    }
                }
            }
        }

        if (self.isBlocking()) {
            ItemStack activeItem = self.getActiveItem();
            if (activeItem.getItem() instanceof SpikedShieldItem spikedShield) {
                if (spikedShield.handleExplosiveDamage(self, damageSource, amount)) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}