package potatowolfie.earth_and_water.entity.water_charge;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
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
import net.minecraft.state.property.Properties;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import potatowolfie.earth_and_water.damage.ModDamageTypes;
import potatowolfie.earth_and_water.effect.ModEffects;
import potatowolfie.earth_and_water.entity.ModEntities;
import potatowolfie.earth_and_water.item.ModItems;

import java.util.List;

import net.minecraft.entity.effect.StatusEffectInstance;
import potatowolfie.earth_and_water.util.ExplosionUtil;

public class WaterChargeProjectileEntity extends PersistentProjectileEntity {
    boolean isStuck = false;
    private Entity attachedEntity = null;
    private BlockPos attachedBlock = null;
    private Direction attachedFace = null;
    private Vec3d exactHitPosition = null;

    private int stuckTicks = -1;
    private static final int TICKS_TO_EXPLODE = 40;

    private float initialEntityYaw = 0;

    private Vec3d initialDirection = null;

    private static final float DIRECT_DAMAGE = 4.5F;
    private static final float INDIRECT_KNOCKBACK_RADIUS = 5.0F;
    private static final float KNOCKBACK_STRENGTH = 0.5F;
    private static final int BUBBLE_EFFECT_DURATION = 60;
    private static final float WATER_BREATHING_DURATION = 2.5F;

    private static final float EXPLOSION_DAMAGE_FACTOR = 1.5F;
    private static final float MAX_EXPLOSION_DAMAGE = 4.5F;
    private static final float EXPLOSION_KNOCKBACK_MULTIPLIER = 1.2F;

    private int bubbleEffectTimer = 0;
    private boolean isPerformingBubbleEffect = false;
    private boolean isDirectHit = false;

    public WaterChargeProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.setNoClip(false);
        this.setDamage(0);
        this.setNoGravity(false);
    }

    public WaterChargeProjectileEntity(World world, PlayerEntity player) {
        super(ModEntities.WATER_CHARGE, player, world, new ItemStack(ModItems.WATER_CHARGE), null);

        this.setPosition(player.getX(), player.getEyeY() - 0.3, player.getZ());

        this.setPitch(player.getPitch());
        this.setYaw(player.getYaw());

        float pitch = player.getPitch() * 0.017453292F;
        float yaw = player.getYaw() * 0.017453292F;
        float x = -MathHelper.sin(yaw) * MathHelper.cos(pitch);
        float y = -MathHelper.sin(pitch);
        float z = MathHelper.cos(yaw) * MathHelper.cos(pitch);
        initialDirection = new Vec3d(x, y, z).normalize();

        float speed = 1.5F;
        this.setVelocity(x * speed, y * speed, z * speed);

        this.setNoClip(false);
        this.setDamage(0);
        this.setNoGravity(false);
    }

    public WaterChargeProjectileEntity(World world, double x, double y, double z, Vec3d vec3d) {
        super(ModEntities.WATER_CHARGE, world);
        this.setPosition(x, y - 0.2, z);
        if (vec3d != null) {
            this.setVelocity(vec3d);
            initialDirection = vec3d.normalize();
        }
        this.setNoClip(false);
        this.setDamage(0);
        this.setNoGravity(false);
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(ModItems.WATER_CHARGE);
    }

    public float getRenderingRotation() {
        if (!isStuck && initialDirection != null) {
            return (float) Math.toDegrees(Math.atan2(initialDirection.x, initialDirection.z));
        }
        return 0.0f;
    }

    public boolean isGrounded() {
        return this.isInGround();
    }

    private boolean isReallyInWater() {
        BlockPos pos = this.getBlockPos();
        World world = this.getEntityWorld();

        if (world.isWater(pos)) {
            return true;
        }

        for (Direction dir : Direction.values()) {
            if (world.isWater(pos.offset(dir))) {
                return true;
            }
        }

        return false;
    }


    private void restoreOxygen(LivingEntity entity) {
        if (!getEntityWorld().isClient() && entity instanceof PlayerEntity) {
            ServerWorld serverWorld = (ServerWorld) getEntityWorld();
            serverWorld.spawnParticles(
                    ParticleTypes.BUBBLE,
                    entity.getX(),
                    entity.getY() + entity.getHeight() * 0.5,
                    entity.getZ(),
                    10,
                    0.3, 0.3, 0.3,
                    0.1
            );
        }
    }

    private void createWaterExplosionEffects() {
        World world = this.getEntityWorld();
        Vec3d pos = this.getEntityPos();

        world.playSound(null, pos.x, pos.y, pos.z,
                SoundEvents.BLOCK_POINTED_DRIPSTONE_LAND,
                SoundCategory.BLOCKS, 1.0F, 0.8F);

        if (!world.isClient() && world instanceof ServerWorld serverWorld) {
            DamageSource waterChargeDamage = new DamageSource(
                    serverWorld.getRegistryManager()
                            .getOrThrow(RegistryKeys.DAMAGE_TYPE)
                            .getEntry(ModDamageTypes.WATER_CHARGE.getValue()).get(),
                    this,
                    this.getOwner()
            );

            ExplosionUtil.createSilentExplosion(
                    serverWorld,
                    this.getEntityPos(),
                    3.0f,
                    this,
                    null,
                    waterChargeDamage,
                    13.0f,
                    2.0f
            );

            serverWorld.spawnParticles(
                    ParticleTypes.BUBBLE,
                    pos.x, pos.y + 0.05, pos.z,
                    20,
                    0.2, 0.05, 0.2,
                    0.1
            );

            for (int i = 0; i < 6; i++) {
                double angle = i * Math.PI / 3;
                double distance = 0.2;
                double offsetX = Math.cos(angle) * distance;
                double offsetZ = Math.sin(angle) * distance;
                serverWorld.spawnParticles(
                        ParticleTypes.BUBBLE,
                        pos.x + offsetX, pos.y + 0.03, pos.z + offsetZ,
                        10,
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
                        ParticleTypes.BUBBLE,
                        pos.x + offsetX * 0.3, pos.y + 0.05, pos.z + offsetZ * 0.3,
                        7,
                        0.03, 0.02, 0.03,
                        0.3
                );
            }

            if (world.isWater(this.getBlockPos())) {
                serverWorld.spawnParticles(
                        ParticleTypes.BUBBLE_COLUMN_UP,
                        pos.x, pos.y, pos.z,
                        17, 0.7, 0.7, 0.7, 0.2
                );

                serverWorld.spawnParticles(
                        ParticleTypes.BUBBLE_POP,
                        pos.x, pos.y + 0.5, pos.z,
                        10, 1.2, 0.8, 1.2, 0.05
                );

                world.getServer().execute(() -> {
                    serverWorld.spawnParticles(
                            ParticleTypes.BUBBLE,
                            pos.x, pos.y, pos.z,
                            15, 2.0, 1.2, 2.0, 0.05
                    );
                });
            }

            float effectRadius = INDIRECT_KNOCKBACK_RADIUS * 1.2f;

            Box affectBox = new Box(
                    pos.x - effectRadius - 0.5,
                    pos.y - effectRadius - 0.5,
                    pos.z - effectRadius - 0.5,
                    pos.x + effectRadius + 0.5,
                    pos.y + effectRadius + 0.5,
                    pos.z + effectRadius + 0.5
            );

            List<Entity> nearbyEntities = world.getEntitiesByClass(Entity.class, affectBox, entity ->
                    entity != this && entity != this.getOwner());

            for (Entity entity : nearbyEntities) {
                double distance = entity.getEntityPos().distanceTo(pos);

                if (distance <= effectRadius) {
                    float distanceFactor = (float)(1.0 - distance / effectRadius);
                    float explosionFactor = (float)Math.pow(distanceFactor, 2.5) * EXPLOSION_KNOCKBACK_MULTIPLIER;

                    Vec3d knockbackDir = entity.getEntityPos().subtract(pos).normalize();
                    float knockbackStrength = KNOCKBACK_STRENGTH * explosionFactor;

                    double upwardForce = 0.2 + (0.3 * explosionFactor);

                    entity.addVelocity(
                            knockbackDir.x * knockbackStrength,
                            knockbackDir.y * knockbackStrength + upwardForce,
                            knockbackDir.z * knockbackStrength
                    );
                    entity.velocityDirty = true;

                    if (entity instanceof LivingEntity livingEntity) {
                        float damage = distanceFactor * EXPLOSION_DAMAGE_FACTOR * MAX_EXPLOSION_DAMAGE;
                        if (damage > 0.5f && world instanceof ServerWorld) {
                            new DamageSource(
                                    serverWorld.getRegistryManager()
                                            .getOrThrow(RegistryKeys.DAMAGE_TYPE)
                                            .getEntry(ModDamageTypes.WATER_CHARGE.getValue()).get(),
                                    this,
                                    this.getOwner()
                            );
                            livingEntity.damage(serverWorld, waterChargeDamage, damage);
                            restoreOxygen(livingEntity);
                        }
                    }
                }
            }
        }

        isPerformingBubbleEffect = true;
        bubbleEffectTimer = BUBBLE_EFFECT_DURATION;
    }

    private void createWaterShockwave() {
        createWaterExplosionEffects();
    }

    private void createDirectHitEffect(Entity hitEntity) {
        World world = this.getEntityWorld();
        Vec3d pos = hitEntity.getEntityPos();

        world.playSound(null, pos.x, pos.y, pos.z,
                SoundEvents.BLOCK_GILDED_BLACKSTONE_BREAK,
                SoundCategory.NEUTRAL, 0.8F, 1.2F);
        world.playSound(null, pos.x, pos.y, pos.z,
                SoundEvents.ENTITY_GENERIC_SPLASH,
                SoundCategory.BLOCKS, 1.0F, 1.2F);

        if (hitEntity instanceof LivingEntity livingEntity) {
            float damage = DIRECT_DAMAGE;
            if (world instanceof ServerWorld serverWorld) {
                DamageSource waterChargeDamage = new DamageSource(
                        serverWorld.getRegistryManager()
                                .getOrThrow(RegistryKeys.DAMAGE_TYPE)
                                .getEntry(ModDamageTypes.WATER_CHARGE.getValue()).get(),
                        this,
                        this.getOwner()
                );
                livingEntity.damage(serverWorld, waterChargeDamage, damage);
            }

            int durationTicks = (int)(WATER_BREATHING_DURATION * 20);
            StatusEffectInstance breathEffect = new StatusEffectInstance(ModEffects.BREATH_GIVING, durationTicks, 1);
            livingEntity.addStatusEffect(breathEffect);

            restoreOxygen(livingEntity);
        }

        if (!world.isClient() && world instanceof ServerWorld serverWorld) {
            float entityWidth = hitEntity.getWidth();
            float entityHeight = hitEntity.getHeight();
            float entitySize = (entityWidth + entityHeight) / 2.0f;

            Vec3d explosionPos = exactHitPosition != null ? exactHitPosition : this.getEntityPos();

            serverWorld.spawnParticles(
                    ParticleTypes.BUBBLE,
                    explosionPos.x, explosionPos.y + 0.05, explosionPos.z,
                    (int)(15 * Math.max(1.0f, entitySize)),
                    0.2, 0.05, 0.2,
                    0.1
            );

            for (int i = 0; i < 6; i++) {
                double angle = i * Math.PI / 3;
                double distance = 0.2 * Math.max(1.0f, entitySize);
                double offsetX = Math.cos(angle) * distance;
                double offsetZ = Math.sin(angle) * distance;
                serverWorld.spawnParticles(
                        ParticleTypes.BUBBLE,
                        explosionPos.x + offsetX, explosionPos.y + 0.03, explosionPos.z + offsetZ,
                        7,
                        0.05, 0.02, 0.05,
                        0.15
                );
            }
        }

        isPerformingBubbleEffect = true;
        isDirectHit = true;
        bubbleEffectTimer = BUBBLE_EFFECT_DURATION;
        attachedEntity = hitEntity;
    }

    private void spawnBubbleParticles() {
        World world = this.getEntityWorld();

        if (!world.isClient()) {
            return;
        }

        if (isDirectHit && attachedEntity != null) {
            Vec3d entityPos = attachedEntity.getEntityPos();
            float entityHeight = attachedEntity.getHeight();
            float entityWidth = attachedEntity.getWidth();

            int spiralLayers = 3;
            float timeMultiplier = 0.1f;

            for (int layer = 0; layer < spiralLayers; layer++) {
                double spiralRadius = entityWidth * (1.2 + layer * 0.4);
                double baseHeight = layer * entityHeight / (spiralLayers + 1);
                float rotationDirection = (layer % 2 == 0) ? 1.0f : -1.0f;
                double baseAngle = this.age * timeMultiplier * rotationDirection;
                int spiralsPerLayer = 2;
                for (int spiral = 0; spiral < spiralsPerLayer; spiral++) {
                    double spiralOffset = spiral * (Math.PI * 2.0 / spiralsPerLayer);

                    int particlesPerSpiral = 10;
                    for (int i = 0; i < particlesPerSpiral; i++) {
                        double progress = (double)i / particlesPerSpiral;
                        double angle = baseAngle + spiralOffset + progress * Math.PI * 2.0;
                        double posX = entityPos.x + Math.sin(angle) * spiralRadius;
                        double posY = entityPos.y + baseHeight + progress * entityHeight * 0.8;
                        double posZ = entityPos.z + Math.cos(angle) * spiralRadius;
                        double jitter = 0.05;
                        posX += (this.random.nextDouble() - 0.5) * jitter;
                        posY += (this.random.nextDouble() - 0.5) * jitter;
                        posZ += (this.random.nextDouble() - 0.5) * jitter;
                        double velX = Math.cos(angle) * 0.1 * rotationDirection;
                        double velY = 0.05 + (layer * 0.02);
                        double velZ = -Math.sin(angle) * 0.1 * rotationDirection;

                        if (i % 2 == 0) {
                            world.addParticleClient(
                                    ParticleTypes.BUBBLE,
                                    posX, posY, posZ,
                                    velX, velY, velZ);
                        }

                        if (this.random.nextInt(15) == 0) {
                            world.addParticleClient(
                                    ParticleTypes.BUBBLE_POP,
                                    posX, posY + 0.2, posZ,
                                    velX * 1.5, velY * 1.5, velZ * 1.5);
                        }
                    }
                }
            }
        } else {
            Vec3d pos = exactHitPosition != null ? exactHitPosition : this.getEntityPos();
            float explosionProgress = 1.0f - ((float)bubbleEffectTimer / BUBBLE_EFFECT_DURATION);

            if (bubbleEffectTimer > BUBBLE_EFFECT_DURATION * 0.3) {
                for (int i = 0; i < 2; i++) {
                    double randX = (this.random.nextDouble() - 0.5) * explosionProgress * 6.0;
                    double randY = (this.random.nextDouble() - 0.5) * explosionProgress * 6.0;
                    double randZ = (this.random.nextDouble() - 0.5) * explosionProgress * 6.0;

                    if (this.random.nextInt(2) == 0) {
                        world.addParticleClient(
                                ParticleTypes.BUBBLE_POP,
                                pos.x + randX * 0.5,
                                pos.y + randY * 0.5,
                                pos.z + randZ * 0.5,
                                randX * 0.1, 0.3, randZ * 0.1
                        );
                    }
                }
            }

            int spiralLayers = 2;
            double maxRadius = 1.0 + (double)(BUBBLE_EFFECT_DURATION - bubbleEffectTimer)
                    / BUBBLE_EFFECT_DURATION * INDIRECT_KNOCKBACK_RADIUS * 0.4;

            for (int layer = 0; layer < spiralLayers; layer++) {
                double layerRadius = maxRadius * (0.4 + 0.6 * ((double)layer / spiralLayers));
                double baseHeight = -1.0 + layer * 0.7;
                double heightRange = 2.0 + layer * 0.3;

                double baseAngle = this.age * (0.05 + layer * 0.02) * (layer % 2 == 0 ? 1 : -1);

                int spiralsPerLayer = 1 + layer;
                for (int spiral = 0; spiral < spiralsPerLayer; spiral++) {
                    double spiralOffset = spiral * (Math.PI * 2.0 / spiralsPerLayer);

                    int pointsPerSpiral = 7;
                    for (int i = 0; i < pointsPerSpiral; i++) {
                        double progress = (double)i / pointsPerSpiral;

                        double angle = baseAngle + spiralOffset + progress * Math.PI * 4.0;

                        double currentRadius = layerRadius * (0.3 + 0.7 * progress);

                        double offsetX = Math.sin(angle) * currentRadius;
                        double offsetY = baseHeight + progress * heightRange;
                        double offsetZ = Math.cos(angle) * currentRadius;

                        double spiralTightness = 0.12;
                        double upwardSpeed = 0.20;
                        double velX = Math.cos(angle) * spiralTightness * (layer % 2 == 0 ? -1 : 1);
                        double velY = upwardSpeed;
                        double velZ = -Math.sin(angle) * spiralTightness * (layer % 2 == 0 ? -1 : 1);

                        double rand = 0.03 * (layer + 1);
                        double randX = (this.random.nextDouble() - 0.5) * rand;
                        double randY = (this.random.nextDouble() - 0.5) * rand;
                        double randZ = (this.random.nextDouble() - 0.5) * rand;

                        if (i % 2 == 0) {
                            world.addParticleClient(
                                    ParticleTypes.BUBBLE,
                                    pos.x + offsetX + randX,
                                    pos.y + offsetY + randY,
                                    pos.z + offsetZ + randZ,
                                    velX, velY, velZ);
                        }

                        if (this.random.nextInt(20) == 0) {
                            world.addParticleClient(
                                    ParticleTypes.BUBBLE_POP,
                                    pos.x + offsetX + randX,
                                    pos.y + offsetY + randY + 0.2,
                                    pos.z + offsetZ + randZ,
                                    velX * 1.5, velY * 1.5, velZ * 1.5);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();

        if (entity instanceof ItemEntity || !entity.isAlive()) {
            return;
        }

        if (!isReallyInWater()) {
            spawnAsItem();
            return;
        }

        this.exactHitPosition = entityHitResult.getPos();

        if (entity instanceof LivingEntity livingEntity) {
            if (getEntityWorld() instanceof ServerWorld serverWorld) {
                DamageSource waterChargeDamage = new DamageSource(
                        serverWorld.getRegistryManager()
                                .getOrThrow(RegistryKeys.DAMAGE_TYPE)
                                .getEntry(ModDamageTypes.WATER_CHARGE.getValue()).get(),
                        this,
                        this.getOwner()
                );
                livingEntity.damage(serverWorld, waterChargeDamage, DIRECT_DAMAGE);
            }

            int durationTicks = (int)(WATER_BREATHING_DURATION * 20);
            StatusEffectInstance breathEffect = new StatusEffectInstance(ModEffects.BREATH_GIVING, durationTicks, 1);
            livingEntity.addStatusEffect(breathEffect);

            restoreOxygen(livingEntity);
        }

        createWaterExplosionEffects();

        this.getEntityWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.BLOCK_GILDED_BLACKSTONE_BREAK, SoundCategory.BLOCKS, 1.0F, 1.2F);
    }

    @Override
    protected void onBlockHit(BlockHitResult result) {
        super.onBlockHit(result);

        if (!isReallyInWater()) {
            spawnAsItem();
            return;
        }

        Vec3d hitPos = result.getPos();
        Direction face = result.getSide();
        double embedOffset = 0.05;

        Vec3d embeddedPos = hitPos.add(
                face.getOffsetX() * embedOffset,
                face.getOffsetY() * embedOffset,
                face.getOffsetZ() * embedOffset
        );

        this.setPos(embeddedPos.x, embeddedPos.y, embeddedPos.z);
        this.setVelocity(Vec3d.ZERO);
        this.setNoGravity(true);

        this.setYaw(0.0F);
        this.setPitch(0.0F);
        this.setHeadYaw(0.0F);;

        this.exactHitPosition = embeddedPos;
        this.attachedBlock = result.getBlockPos();
        this.attachedFace = face;
        this.isStuck = true;
        this.stuckTicks = 0;

        this.getEntityWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.BLOCK_GILDED_BLACKSTONE_BREAK,
                SoundCategory.NEUTRAL, 1.0F, 1.2F);
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        return ActionResult.PASS;
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
    }

    public PersistentProjectileEntity.PickupPermission getPickupType() {
        if (isStuck && isReallyInWater()) {
            return PersistentProjectileEntity.PickupPermission.DISALLOWED;
        } else {
            return PersistentProjectileEntity.PickupPermission.ALLOWED;
        }
    }


    @Override
    public void tick() {
        if (isPerformingBubbleEffect) {
            bubbleEffectTimer--;
            spawnBubbleParticles();

            if (bubbleEffectTimer <= 0) {
                isPerformingBubbleEffect = false;
                this.discard();
                return;
            }
        }

        if (isStuck && stuckTicks >= 0 && !isPerformingBubbleEffect) {
            stuckTicks++;

            if (stuckTicks > 20 && this.getEntityWorld().isClient()) {
                int particleChance = stuckTicks > 35 ? 1 : (stuckTicks > 30 ? 2 : 3);

                if (this.random.nextInt(particleChance) == 0) {
                    Vec3d pos = this.getEntityPos();
                    double spreadFactor = 0.1;

                    this.getEntityWorld().addParticleClient(
                            ParticleTypes.BUBBLE,
                            pos.x + (this.random.nextDouble() - 0.5) * spreadFactor,
                            pos.y + (this.random.nextDouble() - 0.5) * spreadFactor,
                            pos.z + (this.random.nextDouble() - 0.5) * spreadFactor,
                            (this.random.nextDouble() - 0.5) * 0.03,
                            0.1 + (this.random.nextDouble() * 0.05),
                            (this.random.nextDouble() - 0.5) * 0.03
                    );
                }
            }

            if (stuckTicks >= 40) {
                this.setInvisible(true);

                createWaterShockwave();

                if (!getEntityWorld().isClient()) {
                    this.discard();
                }

                stuckTicks = -1;
            }
        }

        if (!isStuck) {
            super.tick();

            if (this.isTouchingWater() && this.getEntityWorld().isClient()) {
                Vec3d velocity = this.getVelocity();
                double speed = velocity.length();

                if (speed > 0.1 && this.age % 2 == 0) {
                    Vec3d normalized = velocity.normalize();
                    Vec3d bubblePos = this.getEntityPos().subtract(normalized.multiply(0.3));

                    double spreadFactor = 0.05;

                    for (int i = 0; i < 2; i++) {
                        this.getEntityWorld().addParticleClient(
                                ParticleTypes.BUBBLE,
                                bubblePos.x + (this.random.nextDouble() - 0.5) * spreadFactor,
                                bubblePos.y + (this.random.nextDouble() - 0.5) * spreadFactor,
                                bubblePos.z + (this.random.nextDouble() - 0.5) * spreadFactor,
                                (this.random.nextDouble() - 0.5) * 0.02,
                                0.05,
                                (this.random.nextDouble() - 0.5) * 0.02
                        );
                    }
                }
            }

            if (this.isTouchingWater()) {
                Vec3d currentVelocity = this.getVelocity();
                this.setVelocity(currentVelocity.multiply(1.2));
            }

            if (initialDirection == null) {
                Vec3d velocity = this.getVelocity();
                double length = velocity.length();
                if (length > 0.1) {
                    initialDirection = velocity.normalize();
                }
            }
        }
    }

    @Override
    public void writeCustomData(WriteView nbt) {
        super.writeCustomData(nbt);

        nbt.putBoolean("IsStuck", isStuck);
        nbt.putInt("StuckTicks", stuckTicks);

        if (attachedBlock != null) {
            nbt.putInt("AttachedBlockX", attachedBlock.getX());
            nbt.putInt("AttachedBlockY", attachedBlock.getY());
            nbt.putInt("AttachedBlockZ", attachedBlock.getZ());
        }

        if (attachedFace != null) {
            nbt.putInt("AttachedFace", attachedFace.ordinal());
        }

        if (exactHitPosition != null) {
            nbt.putDouble("HitPosX", exactHitPosition.x);
            nbt.putDouble("HitPosY", exactHitPosition.y);
            nbt.putDouble("HitPosZ", exactHitPosition.z);
        }

        if (initialDirection != null) {
            nbt.putDouble("InitialDirX", initialDirection.x);
            nbt.putDouble("InitialDirY", initialDirection.y);
            nbt.putDouble("InitialDirZ", initialDirection.z);
        }
    }

    @Override
    public void readCustomData(ReadView nbt) {
        super.readCustomData(nbt);

        isStuck = nbt.getBoolean("IsStuck", false);
        stuckTicks = nbt.getInt("StuckTicks", 0);

        int blockX = nbt.getInt("AttachedBlockX", Integer.MIN_VALUE);
        if (blockX != Integer.MIN_VALUE) {
            int blockY = nbt.getInt("AttachedBlockY", 0);
            int blockZ = nbt.getInt("AttachedBlockZ", 0);
            attachedBlock = new BlockPos(blockX, blockY, blockZ);
        }

        int faceId = nbt.getInt("AttachedFace", -1);
        if (faceId != -1 && faceId < Direction.values().length) {
            attachedFace = Direction.values()[faceId];
        }

        double hitX = nbt.getDouble("HitPosX", Double.NaN);
        if (!Double.isNaN(hitX)) {
            double hitY = nbt.getDouble("HitPosY", 0.0);
            double hitZ = nbt.getDouble("HitPosZ", 0.0);
            exactHitPosition = new Vec3d(hitX, hitY, hitZ);
        }

        double dirX = nbt.getDouble("InitialDirX", Double.NaN);
        if (!Double.isNaN(dirX)) {
            double dirY = nbt.getDouble("InitialDirY", 0.0);
            double dirZ = nbt.getDouble("InitialDirZ", 0.0);
            initialDirection = new Vec3d(dirX, dirY, dirZ);
        }

        this.setNoGravity(isStuck);
    }

    private void spawnAsItem() {
        if (!this.getEntityWorld().isClient()) {
            ItemEntity itemEntity = new ItemEntity(
                    this.getEntityWorld(),
                    this.getX(), this.getY(), this.getZ(),
                    new ItemStack(ModItems.WATER_CHARGE)
            );
            this.getEntityWorld().spawnEntity(itemEntity);

            if (attachedEntity instanceof LivingEntity livingEntity && livingEntity.isAlive()) {
                livingEntity.setStuckArrowCount(Math.max(0, livingEntity.getStuckArrowCount() - 1));
            }

            this.discard();
        }
    }

    public boolean isStuck() {
        return isStuck;
    }

    @Override
    protected float getDragInWater() {
        return 0.99F;
    }

    @Override
    public void setDamage(double damage) {
        super.setDamage(0.0);
    }

    public double getDamage() {
        return 0.0;
    }

    public static void registerDispenserBehavior() {
        DispenserBehavior behavior = new DispenserBehavior() {
            @Override
            public ItemStack dispense(BlockPointer pointer, ItemStack stack) {
                World world = pointer.world();
                BlockPos pos = pointer.pos();
                Direction direction = pointer.state().get(Properties.FACING);

                double x = pos.getX() + 0.5 + direction.getOffsetX() * 0.5;
                double y = pos.getY() + 0.5 + direction.getOffsetY() * 0.5;
                double z = pos.getZ() + 0.5 + direction.getOffsetZ() * 0.5;

                float speed = 1.5f;
                Vec3d velocity = new Vec3d(
                        direction.getOffsetX() * speed,
                        direction.getOffsetY() * speed,
                        direction.getOffsetZ() * speed
                );

                WaterChargeProjectileEntity projectile = new WaterChargeProjectileEntity(
                        world, x, y, z, velocity
                );

                world.spawnEntity(projectile);

                stack.decrement(1);
                return stack;
            }
        };

        DispenserBlock.registerBehavior(ModItems.WATER_CHARGE, behavior);
    }

    @Override
    public void kill(ServerWorld serverWorld) {
        if (!isPerformingBubbleEffect && !getEntityWorld().isClient()) {
            Vec3d pos = this.getEntityPos();

            serverWorld.spawnParticles(
                    ParticleTypes.BUBBLE_COLUMN_UP,
                    pos.x, pos.y, pos.z,
                    20, 1.0, 1.0, 1.0, 0.2
            );
        }
        super.kill(serverWorld);
    }

    public boolean isStuckToEntity() {
        return isStuck && attachedEntity != null;
    }
}