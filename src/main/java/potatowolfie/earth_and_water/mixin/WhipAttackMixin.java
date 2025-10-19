package potatowolfie.earth_and_water.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import potatowolfie.earth_and_water.item.custom.WhipItem;

@Mixin(PlayerEntity.class)
public abstract class WhipAttackMixin {

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void onAttack(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack stack = player.getMainHandStack();

        if (!(stack.getItem() instanceof WhipItem)) {
            return;
        }
        if (!(target instanceof LivingEntity livingTarget)) {
            return;
        }
        if (player.getEntityWorld().isClient()) {
            return;
        }
        ci.cancel();
        DamageSource damageSource = player.getDamageSources().playerAttack(player);
        float damage = player.isSubmergedIn(FluidTags.WATER) ? 7.0f : 3.5f;
        livingTarget.damage((ServerWorld) player.getEntityWorld(), damageSource, damage);
        stack.damage(1, player, player.getPreferredEquipmentSlot(stack));
        stack.getItem().postHit(stack, livingTarget, player);
        player.resetLastAttackedTicks();
        player.getEntityWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
                player.getSoundCategory(), 1.0F, 1.0F);
    }
}