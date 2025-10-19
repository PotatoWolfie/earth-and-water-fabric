package potatowolfie.earth_and_water.util;

import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExplosionUtil {

    public static void createSilentExplosion(ServerWorld world, Vec3d pos, float power, @Nullable Entity sourceEntity) {
        createSilentExplosion(world, pos, power, sourceEntity, null);
    }

    public static void createSilentExplosion(ServerWorld world, Vec3d pos, float power, @Nullable Entity sourceEntity, @Nullable Entity directHit) {
        createSilentExplosion(world, pos, power, sourceEntity, directHit, -1, -1);
    }

    public static void createSilentExplosion(ServerWorld world, Vec3d pos, float power, @Nullable Entity sourceEntity, @Nullable Entity directHit, float customDamage, float knockbackMultiplier) {
        world.emitGameEvent(sourceEntity, GameEvent.EXPLODE, pos);

        float radius = power * 2.0F;
        int minX = MathHelper.floor(pos.x - radius - 1.0);
        int maxX = MathHelper.floor(pos.x + radius + 1.0);
        int minY = MathHelper.floor(pos.y - radius - 1.0);
        int maxY = MathHelper.floor(pos.y + radius + 1.0);
        int minZ = MathHelper.floor(pos.z - radius - 1.0);
        int maxZ = MathHelper.floor(pos.z + radius + 1.0);

        List<Entity> entities = world.getOtherEntities(
                sourceEntity,
                new Box(minX, minY, minZ, maxX, maxY, maxZ)
        );

        Map<PlayerEntity, Vec3d> affectedPlayers = new HashMap<>();
        DamageSource damageSource = world.getDamageSources().explosion(sourceEntity, getCausingEntity(sourceEntity));

        for (Entity entity : entities) {
            if (entity == directHit || shouldSkipEntity(entity)) {
                continue;
            }

            Vec3d entityCenter = getEntityCenter(entity);
            double distance = entityCenter.distanceTo(pos);
            double normalizedDistance = distance / radius;

            if (normalizedDistance <= 1.0) {
                Vec3d direction = entityCenter.subtract(pos);
                double totalDistance = direction.length();

                if (totalDistance > 0.0) {
                    direction = direction.normalize();

                    float damage;
                    if (customDamage > 0) {
                        damage = customDamage * (float)(1.0 - normalizedDistance);
                    } else {
                        damage = calculateImprovedDamage(power, normalizedDistance);
                    }

                    if (damage > 0.1f) {
                        entity.damage(world, damageSource, damage);
                    }

                    double exposure = getExposure(pos, entity);
                    double knockbackStrength = (1.0 - normalizedDistance) * exposure;

                    if (knockbackMultiplier > 0) {
                        knockbackStrength *= knockbackMultiplier;
                    }

                    if (entity instanceof LivingEntity livingEntity) {
                        knockbackStrength *= (1.0 - livingEntity.getAttributeValue(EntityAttributes.EXPLOSION_KNOCKBACK_RESISTANCE));
                    }

                    Vec3d knockback = direction.multiply(knockbackStrength);
                    entity.setVelocity(entity.getVelocity().add(knockback));

                    if (entity instanceof PlayerEntity player) {
                        if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
                            affectedPlayers.put(player, knockback);
                        }
                    }

                    entity.onExplodedBy(sourceEntity);
                }
            }
        }

        for (Map.Entry<PlayerEntity, Vec3d> entry : affectedPlayers.entrySet()) {
            PlayerEntity player = entry.getKey();
            player.velocityModified = true;
        }
    }

    private static boolean shouldSkipEntity(Entity entity) {
        if (entity instanceof ItemEntity) {
            return true;
        }
        if (entity instanceof ExperienceOrbEntity) {
            return true;
        }

        return false;
    }

    private static Vec3d getEntityCenter(Entity entity) {
        Box box = entity.getBoundingBox();
        return new Vec3d(
                (box.minX + box.maxX) * 0.5,
                (box.minY + box.maxY) * 0.5,
                (box.minZ + box.maxZ) * 0.5
        );
    }

    private static float calculateImprovedDamage(float power, double normalizedDistance) {
        float maxDamage = power * 7.0F;
        return (float) (maxDamage * (1.0 - normalizedDistance));
    }

    private static float calculateDamage(float power, double distance) {
        float maxDamage = (power * 2.0F + 1.0F) * 8.0F;
        return (float) (maxDamage * (1.0 - distance));
    }

    private static float getExposure(Vec3d source, Entity entity) {
        Box box = entity.getBoundingBox();
        double stepX = 1.0 / ((box.maxX - box.minX) * 2.0 + 1.0);
        double stepY = 1.0 / ((box.maxY - box.minY) * 2.0 + 1.0);
        double stepZ = 1.0 / ((box.maxZ - box.minZ) * 2.0 + 1.0);
        double offsetX = (1.0 - Math.floor(1.0 / stepX) * stepX) / 2.0;
        double offsetZ = (1.0 - Math.floor(1.0 / stepZ) * stepZ) / 2.0;

        if (stepX < 0.0 || stepY < 0.0 || stepZ < 0.0) {
            return 0.0F;
        }

        int visiblePoints = 0;
        int totalPoints = 0;

        for (double x = 0.0; x <= 1.0; x += stepX) {
            for (double y = 0.0; y <= 1.0; y += stepY) {
                for (double z = 0.0; z <= 1.0; z += stepZ) {
                    double pointX = MathHelper.lerp(x, box.minX, box.maxX);
                    double pointY = MathHelper.lerp(y, box.minY, box.maxY);
                    double pointZ = MathHelper.lerp(z, box.minZ, box.maxZ);
                    Vec3d point = new Vec3d(pointX + offsetX, pointY, pointZ + offsetZ);

                    if (entity.getWorld().raycast(new RaycastContext(
                            point, source,
                            RaycastContext.ShapeType.COLLIDER,
                            RaycastContext.FluidHandling.NONE,
                            entity
                    )).getType() == HitResult.Type.MISS) {
                        visiblePoints++;
                    }
                    totalPoints++;
                }
            }
        }

        return (float) visiblePoints / (float) totalPoints;
    }

    @Nullable
    private static LivingEntity getCausingEntity(@Nullable Entity from) {
        if (from == null) {
            return null;
        } else if (from instanceof TntEntity tntEntity) {
            return tntEntity.getOwner();
        } else if (from instanceof LivingEntity livingEntity) {
            return livingEntity;
        } else if (from instanceof ProjectileEntity projectileEntity) {
            Entity owner = projectileEntity.getOwner();
            if (owner instanceof LivingEntity livingEntity) {
                return livingEntity;
            }
        }
        return null;
    }

    public static void createSilentExplosion(ServerWorld world, Vec3d pos, float radius, float damage, double areaKnockback, double directHitKnockback, @Nullable Entity directHit) {
        createSilentExplosion(world, pos, radius, null, directHit, damage, (float)areaKnockback);
    }
}