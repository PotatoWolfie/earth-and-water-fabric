package potatowolfie.earth_and_water.entity.earth_charge;

import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import potatowolfie.earth_and_water.damage.ModDamageTypes;
import potatowolfie.earth_and_water.entity.ModEntities;
import potatowolfie.earth_and_water.item.ModItems;
import potatowolfie.earth_and_water.util.ExplosionUtil;

public class EarthChargeProjectileEntity extends PersistentProjectileEntity {
    private float rotation;
    public Vector2f groundedOffset;

    private static final float DAMAGE_RADIUS = 1.5F;
    private static final float BASE_DAMAGE = 14.0F;
    private static final float KNOCKBACK_MULTIPLIER = 0.2F;

    public EarthChargeProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(false);
    }

    public EarthChargeProjectileEntity(World world, PlayerEntity player) {
        super(ModEntities.EARTH_CHARGE, world);
        this.setOwner(player);
        this.setPosition(player.getX(), player.getEyeY() - 0.3, player.getZ());
        this.setPitch(0);
        this.setYaw(player.getYaw());

        Vec3d velocity = player.getRotationVector().multiply(1.0);
        this.setVelocity(velocity);

        this.setNoGravity(false);
    }

    public EarthChargeProjectileEntity(World world, double x, double y, double z, Vec3d vec3d) {
        super(ModEntities.EARTH_CHARGE, world);
        this.setPosition(x, y - 0.2, z);
        if (vec3d != null) {
            this.setVelocity(vec3d);
        }
        this.setNoGravity(false);
    }

    public EarthChargeProjectileEntity(World world, LivingEntity owner) {
        super(ModEntities.EARTH_CHARGE, world);
        this.setVelocity(owner, owner.getPitch(), owner.getYaw(), 0.0f, 1.0f, 1.0f);
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(ModItems.EARTH_CHARGE);
    }

    public float getRenderingRotation() {
        return 0.0f;
    }

    public boolean isGrounded() {
        return this.inGround;
    }

    private void applyDirectHitDamage(Entity hitEntity) {
        if (hitEntity instanceof LivingEntity livingEntity && !this.getEntityWorld().isClient()) {
            if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
                DamageSource earthChargeDamage = new DamageSource(
                        serverWorld.getRegistryManager()
                                .get(RegistryKeys.DAMAGE_TYPE)
                                .getEntry(ModDamageTypes.EARTH_CHARGE.getValue()).get(),
                        this,
                        this.getOwner()
                );
                livingEntity.damage(earthChargeDamage, 12.0f);
            }

            Vec3d knockbackDir = hitEntity.getPos().subtract(this.getPos()).normalize();
            if (knockbackDir.lengthSquared() < 0.001) {
                knockbackDir = new Vec3d(0, 1, 0);
            }
            double directKnockback = KNOCKBACK_MULTIPLIER * 3.5;
            hitEntity.addVelocity(
                    knockbackDir.x * directKnockback,
                    knockbackDir.y * directKnockback + 0.2,
                    knockbackDir.z * directKnockback
            );
            hitEntity.velocityDirty = true;
        }
    }

    private void applyAreaDamageExcluding(Entity excludedEntity) {
        World world = this.getEntityWorld();
        Vec3d pos = this.getPos();

        if (!world.isClient()) {
            if (world instanceof ServerWorld serverWorld) {
                DamageSource earthChargeDamage = new DamageSource(
                        serverWorld.getRegistryManager()
                                .get(RegistryKeys.DAMAGE_TYPE)
                                .getEntry(ModDamageTypes.EARTH_CHARGE.getValue()).get(),
                        this,
                        this.getOwner()
                );

                ExplosionUtil.createSilentExplosion(
                        serverWorld,
                        pos,
                        1.5f,
                        this,
                        excludedEntity,
                        earthChargeDamage,
                        26.0f,
                        0.4f
                );
            }
        }
    }

    private void createExplosionEffects() {
        World world = this.getEntityWorld();
        Vec3d pos = this.getPos();

        world.playSound(null, pos.x, pos.y, pos.z,
                SoundEvents.BLOCK_POINTED_DRIPSTONE_LAND,
                SoundCategory.BLOCKS, 1.0F, 0.8F);

        if (!world.isClient() && world instanceof ServerWorld serverWorld) {
            DamageSource waterChargeDamage = new DamageSource(
                    serverWorld.getRegistryManager()
                            .get(RegistryKeys.DAMAGE_TYPE)
                            .getEntry(ModDamageTypes.WATER_CHARGE.getValue()).get(),
                    this,
                    this.getOwner()
            );

            ExplosionUtil.createSilentExplosion(
                    serverWorld,
                    this.getPos(),
                    3.0f,
                    this,
                    null,
                    waterChargeDamage,
                    13.0f,
                    2.0f
            );

            for (int i = 0; i < 6; i++) {
                double angle = i * Math.PI / 3;
                double distance = 0.2;
                double offsetX = Math.cos(angle) * distance;
                double offsetZ = Math.sin(angle) * distance;
                serverWorld.spawnParticles(
                        ParticleTypes.DUST_PLUME,
                        pos.x + offsetX, pos.y + 0.03, pos.z + offsetZ,
                        5,
                        0.05, 0.02, 0.05,
                        0.15
                );
            }

            for (int i = 0; i < 6; i++) {
                double angle = world.getRandom().nextDouble() * Math.PI * 2;
                double distance = 0.1 + world.getRandom().nextDouble() * 0.25;
                double offsetX = Math.cos(angle) * distance;
                double offsetZ = Math.sin(angle) * distance;
                serverWorld.spawnParticles(
                        ParticleTypes.DUST_PLUME,
                        pos.x + offsetX * 0.3, pos.y + 0.05, pos.z + offsetZ * 0.3,
                        3,
                        0.03, 0.02, 0.03,
                        0.3
                );
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity hitEntity = entityHitResult.getEntity();
        applyDirectHitDamage(hitEntity);
        applyAreaDamageExcluding(hitEntity);

        if (!this.getEntityWorld().isClient()) {
            createExplosionEffects();
            this.getEntityWorld().sendEntityStatus(this, (byte)3);
            this.discard();
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult result) {
        switch (result.getSide()) {
            case SOUTH -> groundedOffset = new Vector2f(215f, 180f);
            case NORTH -> groundedOffset = new Vector2f(215f, 0f);
            case EAST -> groundedOffset = new Vector2f(215f, -90f);
            case WEST -> groundedOffset = new Vector2f(215f, 90f);
            case DOWN -> groundedOffset = new Vector2f(115f, 180f);
            case UP -> groundedOffset = new Vector2f(285f, 180f);
        }

        applyAreaDamageExcluding(null);

        if (!this.getEntityWorld().isClient()) {
            createExplosionEffects();
            this.getEntityWorld().sendEntityStatus(this, (byte)3);
            this.discard();
        }
    }

    @Override
    public void tick() {
        if (!this.getEntityWorld().isClient() && !this.isRemoved()) {
            if (this.isTouchingWater()) {
                Vec3d currentVelocity = this.getVelocity();
                this.setVelocity(currentVelocity.multiply(0.9));
            }
        }

        super.tick();

        if (!this.inGround) {
            Vec3d velocity = this.getVelocity();
            double length = velocity.length();
            if (length < 0.5 && (Math.abs(velocity.x) > 0.01 || Math.abs(velocity.z) > 0.01)) {
                double horizontalSpeed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
                if (horizontalSpeed < 0.5) {
                    double factor = 0.5 / horizontalSpeed;
                    this.setVelocity(
                            velocity.x * factor,
                            velocity.y,
                            velocity.z * factor
                    );
                }
            }
        }

        if (this.getEntityWorld().isClient() && !this.inGround) {
            this.getEntityWorld().addParticle(
                    ParticleTypes.SMOKE,
                    this.getX(), this.getY(), this.getZ(),
                    0, 0, 0);
        }
    }

    @Override
    public boolean isTouchingWater() {
        return this.getEntityWorld().isWater(this.getBlockPos());
    }

    @Override
    protected float getDragInWater() {
        return 0.8F;
    }

    @Override
    protected double getGravity() {
        return 0.05F;
    }
}