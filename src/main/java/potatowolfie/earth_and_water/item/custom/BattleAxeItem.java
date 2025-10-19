package potatowolfie.earth_and_water.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
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

    private static final float MAX_HORIZONTAL_MULTIPLIER = 1.414f;
    private static final float MAX_VERTICAL_MULTIPLIER = 0.5f;

    public BattleAxeItem(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postHit(stack, target, attacker);
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
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (player.getItemCooldownManager().isCoolingDown(itemStack)) {
            return ActionResult.PASS;
        }

        if (!world.isClient()) {
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

            Vec3d playerPos = player.getEntityPos();
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
                                .getOrThrow(RegistryKeys.DAMAGE_TYPE)
                                .getEntry(ModDamageTypes.BATTLE_AXE.getValue()).get()
                );
                entity.damage(null, battleAxeDamage, DASH_DAMAGE);
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
            player.getItemCooldownManager().set(itemStack, cooldown);
        }

        return ActionResult.SUCCESS;
    }
}