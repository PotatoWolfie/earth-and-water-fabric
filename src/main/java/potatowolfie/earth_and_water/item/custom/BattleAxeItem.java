package potatowolfie.earth_and_water.item.custom;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import potatowolfie.earth_and_water.damage.ModDamageTypes;
import potatowolfie.earth_and_water.sound.ModSounds;

import java.util.List;
import java.util.function.Consumer;

public class BattleAxeItem extends Item {
    private static final int DASH_COOLDOWN = 45;
    private static final int CREATIVE_DASH_COOLDOWN = 10;
    private static final float DASH_STRENGTH = 1.1383f;
    private static final float MIDAIR_DASH_STRENGTH = 0.6f;
    private static final int DASH_DISTANCE = 4;
    private static final float DASH_DAMAGE = 5.0f;

    private static final float MAX_HORIZONTAL_MULTIPLIER = 1.414f;
    private static final float MAX_VERTICAL_MULTIPLIER = 0.5f;

    public BattleAxeItem(final Item.Properties properties) {
        super(properties);
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.hurtEnemy(stack, target, attacker);
    }

    private float calculateHorizontalAngleMultiplier(Vec3 lookVec) {
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

    private float calculateVerticalAngleMultiplier(Vec3 lookVec) {
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
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(itemStack)) {
            return InteractionResult.PASS;
        }

        if (!world.isClientSide() && world instanceof ServerLevel serverWorld) {
            Vec3 lookVec = player.getLookAngle();
            boolean isInAir = !player.onGround();

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

            Vec3 dashVec = new Vec3(
                    lookVec.x * currentDashStrength,
                    lookVec.y * currentDashStrength * verticalScalingFactor,
                    lookVec.z * currentDashStrength
            );

            player.setDeltaMovement(dashVec);
            player.needsSync = true;
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(player));
            }

            Vec3 playerPos = player.position();
            Vec3 dashEnd = playerPos.add(dashVec.scale(DASH_DISTANCE));
            AABB collisionBox = new AABB(
                    Math.min(playerPos.x, dashEnd.x) - 1,
                    Math.min(playerPos.y, dashEnd.y) - 1,
                    Math.min(playerPos.z, dashEnd.z) - 1,
                    Math.max(playerPos.x, dashEnd.x) + 1,
                    Math.max(playerPos.y, dashEnd.y) + 1,
                    Math.max(playerPos.z, dashEnd.z) + 1
            );

            List<LivingEntity> entities = world.getEntitiesOfClass(
                    LivingEntity.class,
                    collisionBox,
                    entity -> entity != player && !entity.isSpectator()
            );

            boolean hitAnyMob = false;

            for (LivingEntity entity : entities) {
                DamageSource battleAxeDamage = new DamageSource(
                        world.registryAccess()
                                .lookupOrThrow(Registries.DAMAGE_TYPE)
                                .get(ModDamageTypes.BATTLE_AXE.identifier()).get(),
                        player
                );

                entity.hurtServer(serverWorld, battleAxeDamage, DASH_DAMAGE);
                entity.knockback(0.5, -lookVec.x, -lookVec.z, battleAxeDamage, DASH_DAMAGE);

                hitAnyMob = true;
            }

            world.playSound(
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    ModSounds.BATTLE_AXE_DASH,
                    SoundSource.PLAYERS,
                    0.5F,
                    1.0F
            );

            int cooldown = player.getAbilities().instabuild ? CREATIVE_DASH_COOLDOWN : DASH_COOLDOWN;
            player.getCooldowns().addCooldown(itemStack, cooldown);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        textConsumer.accept(Component.translatable("tooltip.earth-and-water.tooltipempty"));
        textConsumer.accept(Component.translatable("tooltip.earth-and-water.battle_axe.tooltip1"));
        textConsumer.accept(Component.translatable("tooltip.earth-and-water.battle_axe.tooltip2"));
        super.appendHoverText(stack, context, displayComponent, textConsumer, type);
    }
}