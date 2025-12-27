package potatowolfie.earth_and_water.item.custom;

import net.minecraft.block.DispenserBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import potatowolfie.earth_and_water.damage.ModDamageTypes;

import java.util.List;

public class SpikedShieldItem extends ShieldItem {

    public static final int field_30918 = 5;
    public static final float MIN_DAMAGE_AMOUNT_TO_BREAK = 3.0F;

    public SpikedShieldItem(Item.Settings settings) {
        super(settings);
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        DyeColor dyeColor = stack.get(DataComponentTypes.BASE_COLOR);
        return dyeColor != null ? this.getTranslationKey() + "." + dyeColor.getName() : super.getTranslationKey(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        BannerItem.appendBannerTooltip(stack, tooltip);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isIn(ItemTags.PLANKS) || super.canRepair(stack, ingredient);
    }

    @Override
    public EquipmentSlot getSlotType() {
        return EquipmentSlot.OFFHAND;
    }

    public boolean handleExplosiveDamage(LivingEntity user, DamageSource damageSource, float amount) {
        if (!user.isBlocking()) {
            return false;
        }

        if (isExplosionDamage(damageSource)) {
            LivingEntity attacker = getActualAttacker(damageSource);

            if (attacker != null && attacker != user) {
                if (user.getEntityWorld() instanceof ServerWorld serverWorld) {
                    DamageSource spikedShieldDamage = new DamageSource(
                            serverWorld.getRegistryManager()
                                    .get(RegistryKeys.DAMAGE_TYPE)
                                    .getEntry(ModDamageTypes.SPIKED_SHIELD.getValue()).get(),
                            user
                    );
                    attacker.damage(spikedShieldDamage, amount);
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
