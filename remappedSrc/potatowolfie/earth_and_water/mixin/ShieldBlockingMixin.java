package potatowolfie.earth_and_water.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import potatowolfie.earth_and_water.item.custom.SpikedShieldItem;

@Mixin(LivingEntity.class)
public class ShieldBlockingMixin {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onSpikedShieldBlock(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity.isBlocking()) {
            ItemStack activeItem = entity.getActiveItem();
            if (activeItem.getItem() instanceof SpikedShieldItem) {
                if (entity instanceof PlayerEntity player) {
                    if (player.getItemCooldownManager().isCoolingDown(activeItem)) {
                        return;
                    }
                }

                if (source.getAttacker() instanceof LivingEntity attacker) {
                    attacker.damage(world, entity.getDamageSources().thorns(entity), 3.5F);

                    activeItem.damage(Math.max((int)amount, 1), entity, entity.getActiveHand() == null ?
                            (entity.getMainHandStack() == activeItem ?
                                    net.minecraft.entity.EquipmentSlot.MAINHAND :
                                    net.minecraft.entity.EquipmentSlot.OFFHAND) :
                            (entity.getActiveHand() == net.minecraft.util.Hand.MAIN_HAND ?
                                    net.minecraft.entity.EquipmentSlot.MAINHAND :
                                    net.minecraft.entity.EquipmentSlot.OFFHAND));
                }
            }
        }
    }
}