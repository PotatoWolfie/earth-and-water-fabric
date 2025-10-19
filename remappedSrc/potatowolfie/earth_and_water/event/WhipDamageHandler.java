package potatowolfie.earth_and_water.event;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import potatowolfie.earth_and_water.item.custom.WhipItem;

public class WhipDamageHandler {

    public static void register() {
        AttackEntityCallback.EVENT.register(WhipDamageHandler::onAttackEntity);
    }

    private static ActionResult onAttackEntity(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        ItemStack stack = player.getStackInHand(hand);

        if (!(stack.getItem() instanceof WhipItem)) {
            return ActionResult.PASS;
        }

        if (!(entity instanceof LivingEntity target)) {
            return ActionResult.PASS;
        }

        if (!player.isSubmergedIn(FluidTags.WATER)) {
            if (!world.isClient) {
                DamageSource damageSource = player.getDamageSources().playerAttack(player);
                target.damage((net.minecraft.server.world.ServerWorld) world, damageSource, 3.5f);
                stack.damage(1, player, LivingEntity.getSlotForHand(hand));
                stack.getItem().postHit(stack, target, player);
                player.resetLastAttackedTicks();
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        net.minecraft.sound.SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
                        player.getSoundCategory(), 1.0F, 1.0F);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}