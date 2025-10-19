package potatowolfie.earth_and_water.item.custom;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;

public class SpikedShieldItem extends ShieldItem {

    public SpikedShieldItem(Item.Settings settings) {
        super(settings);
    }

    public Text getName(ItemStack stack) {
        DyeColor dyeColor = (DyeColor)stack.get(DataComponentTypes.BASE_COLOR);
        if (dyeColor != null) {
            String var10000 = this.translationKey;
            return Text.translatable(var10000 + "." + dyeColor.getId());
        } else {
            return super.getName(stack);
        }
    }

    public boolean handleExplosiveDamage(LivingEntity user, DamageSource damageSource, float amount) {
        if (!user.isBlocking()) {
            return false;
        }

        if (isExplosionDamage(damageSource)) {
            LivingEntity attacker = getActualAttacker(damageSource);

            if (attacker != null && attacker != user) {
                if (user.getEntityWorld() instanceof ServerWorld serverWorld) {
                    DamageSource reflectedSource = user.getDamageSources().thorns(user);
                    attacker.damage(serverWorld, reflectedSource, amount);
                }

                return true;
            }

            return true;
        }

        return false;
    }

    private boolean isExplosionDamage(DamageSource damageSource) {
        if (damageSource.isOf(net.minecraft.entity.damage.DamageTypes.EXPLOSION) ||
                damageSource.isOf(net.minecraft.entity.damage.DamageTypes.PLAYER_EXPLOSION)) {
            return true;
        }

        if (damageSource.getSource() != null || damageSource.getAttacker() != null) {
            String sourceName = damageSource.getName();
            if (sourceName != null && sourceName.toLowerCase().contains("explosion")) {
                return true;
            }
        }

        return false;
    }

    private LivingEntity getActualAttacker(DamageSource damageSource) {
        if (damageSource.getAttacker() instanceof LivingEntity livingAttacker) {
            return livingAttacker;
        }

        if (damageSource.getSource() instanceof LivingEntity livingSource) {
            return livingSource;
        }

        if (damageSource.getSource() != null) {
            var source = damageSource.getSource();
            if (source instanceof net.minecraft.entity.projectile.ProjectileEntity projectile) {
                if (projectile.getOwner() instanceof LivingEntity owner) {
                    return owner;
                }
            }
        }

        return null;
    }
}