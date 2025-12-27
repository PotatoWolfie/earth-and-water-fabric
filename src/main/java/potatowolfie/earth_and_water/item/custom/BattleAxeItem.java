package potatowolfie.earth_and_water.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import potatowolfie.earth_and_water.damage.ModDamageTypes;
import potatowolfie.earth_and_water.sound.ModSounds;

import java.util.List;

public class BattleAxeItem extends AxeItem {
    private static final int DASH_COOLDOWN = 45;
    private static final int CREATIVE_DASH_COOLDOWN = 10;
    private static final float DASH_STRENGTH = 1.1383f;
    private static final float MIDAIR_DASH_STRENGTH = 0.6f;
    private static final int DASH_DISTANCE = 4;
    private static final float DASH_DAMAGE = 5.0f;
    private static final int SHIELD_DISABLE_DURATION = 100;

    private static final float MAX_HORIZONTAL_MULTIPLIER = 1.414f;
    private static final float MAX_VERTICAL_MULTIPLIER = 0.5f;

    public BattleAxeItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.earth-and-water.tooltipempty"));
        tooltip.add(Text.translatable("tooltip.earth-and-water.battle_axe.tooltip1"));
        tooltip.add(Text.translatable("tooltip.earth-and-water.battle_axe.tooltip2"));
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target.isBlocking()) {
            boolean isCriticalHit = false;

            if (attacker instanceof PlayerEntity playerAttacker) {
                isCriticalHit = playerAttacker.getVelocity().y < 0
                        && !playerAttacker.isOnGround()
                        && !playerAttacker.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.BLINDNESS)
                        && !playerAttacker.isTouchingWater()
                        && !playerAttacker.getAbilities().flying;
            }

            if (isCriticalHit) {
                target.getWorld().playSound(null,
                        target.getX(), target.getY(), target.getZ(),
                        SoundEvents.ITEM_SHIELD_BREAK,
                        SoundCategory.PLAYERS,
                        0.8F,
                        0.8F + target.getWorld().getRandom().nextFloat() * 0.4F);

                if (target instanceof PlayerEntity playerTarget) {
                    ItemStack shieldStack = playerTarget.getActiveItem();
                    if (shieldStack.getItem() instanceof ShieldItem || shieldStack.getItem() instanceof SpikedShieldItem) {
                        playerTarget.getItemCooldownManager().set(shieldStack.getItem(), SHIELD_DISABLE_DURATION);
                        playerTarget.clearActiveItem();
                        playerTarget.getWorld().sendEntityStatus(playerTarget, (byte)30);
                    }
                }
            }
        }

        return super.postHit(stack, target, attacker);
    }

    private float calculateHorizontalAngleMultiplier(Vec3d lookVec) {
        double horizontalMagnitude = Math.sqrt(lookVec.x * lookVec.x + lookVec.z * lookVec.z);

        if (horizontalMagnitude < 0.001) {
            return 1.0f;
        }

        double angle = Math.atan2(lookVec.z, lookVec.x) * 180.0 / Math.PI;

        if (angle < 0) angle += 360;

        double distanceFromCardinal = Math.min(
                Math.min(Math.abs(angle), Math.abs(angle - 360)),
                Math.min(
                        Math.min(Math.abs(angle - 90), Math.abs(angle - 180)),
                        Math.abs(angle - 270)
                )
        );

        double normalizedDistance = Math.min(distanceFromCardinal, 45.0) / 45.0;

        return (float) (1.0 + (MAX_HORIZONTAL_MULTIPLIER - 1.0) * normalizedDistance);
    }

    private float calculateVerticalAngleMultiplier(Vec3d lookVec) {
        double horizontalMagnitude = Math.sqrt(lookVec.x * lookVec.x + lookVec.z * lookVec.z);
        double verticalComponent = lookVec.y;
        double pitchAngle = Math.atan2(Math.abs(verticalComponent), horizontalMagnitude) * 180.0 / Math.PI;
        double distanceFromCardinal;
        if (pitchAngle <= 45.0) {
            distanceFromCardinal = pitchAngle;
        } else {
            distanceFromCardinal = 90.0 - pitchAngle;
        }

        double normalizedDistance = distanceFromCardinal / 45.0;

        return (float) (1.0 + (MAX_VERTICAL_MULTIPLIER - 1.0) * normalizedDistance);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (player.getItemCooldownManager().isCoolingDown(this)) {
            return TypedActionResult.pass(itemStack);
        }

        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            Vec3d lookVec = player.getRotationVector();
            boolean isInAir = !player.isOnGround();

            float horizontalMultiplier = calculateHorizontalAngleMultiplier(lookVec);
            float verticalMultiplier = calculateVerticalAngleMultiplier(lookVec);

            float currentDashStrength;
            float verticalScalingFactor;

            if (isInAir) {
                float upwardBoost = (float) (0.18f * Math.max(0, lookVec.y));
                currentDashStrength = MIDAIR_DASH_STRENGTH + upwardBoost;

                currentDashStrength *= horizontalMultiplier * verticalMultiplier;
                verticalScalingFactor = 1.0f;
            } else {
                currentDashStrength = DASH_STRENGTH;

                currentDashStrength *= horizontalMultiplier * Math.min(verticalMultiplier, 1.2f);

                verticalScalingFactor = (float) (1.0f - 0.3f * Math.max(0, lookVec.y));
            }

            Vec3d dashVec = new Vec3d(
                    lookVec.x * currentDashStrength,
                    lookVec.y * currentDashStrength * verticalScalingFactor,
                    lookVec.z * currentDashStrength
            );

            player.setVelocity(dashVec);
            player.velocityModified = true;
            if (player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player));
            }

            Vec3d playerPos = player.getPos();
            Vec3d dashEnd = playerPos.add(dashVec.multiply(DASH_DISTANCE));
            Box collisionBox = new Box(
                    Math.min(playerPos.x, dashEnd.x) - 1,
                    Math.min(playerPos.y, dashEnd.y) - 1,
                    Math.min(playerPos.z, dashEnd.z) - 1,
                    Math.max(playerPos.x, dashEnd.x) + 1,
                    Math.max(playerPos.y, dashEnd.y) + 1,
                    Math.max(playerPos.z, dashEnd.z) + 1
            );

            List<LivingEntity> entities = world.getEntitiesByClass(
                    LivingEntity.class,
                    collisionBox,
                    entity -> entity != player && !entity.isSpectator()
            );

            boolean hitAnyMob = false;

            for (LivingEntity entity : entities) {
                DamageSource battleAxeDamage = new DamageSource(
                        world.getRegistryManager()
                                .get(RegistryKeys.DAMAGE_TYPE)
                                .entryOf(ModDamageTypes.BATTLE_AXE),
                        player
                );
                entity.damage(battleAxeDamage, DASH_DAMAGE);
                entity.takeKnockback(0.5, -lookVec.x, -lookVec.z);
                hitAnyMob = true;
            }

            world.playSound(
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    ModSounds.BATTLE_AXE_DASH,
                    SoundCategory.PLAYERS,
                    0.5F,
                    1.0F
            );

            int cooldown = player.getAbilities().creativeMode ? CREATIVE_DASH_COOLDOWN : DASH_COOLDOWN;
            player.getItemCooldownManager().set(this, cooldown);
        }

        return TypedActionResult.success(itemStack);
    }
}