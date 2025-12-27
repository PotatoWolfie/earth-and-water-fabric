package potatowolfie.earth_and_water.block.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.block.custom.ReinforcedSpawnerBlock;
import potatowolfie.earth_and_water.block.entity.ModBlockEntities;
import potatowolfie.earth_and_water.item.custom.ReinforcedKeyItem;

import java.util.*;

public class ReinforcedSpawnerBlockEntity extends BlockEntity {
    private static final int DETECTION_RADIUS = 14;
    private static final int SPAWN_DELAY = 20;
    private static final int BASE_WAVE_SIZE = 3;
    private static final int ADDITIONAL_MOBS_PER_PLAYER = 2;
    private static final int KEY_DETECTION_RADIUS = 3;
    private static final int KEY_USAGE_COOLDOWN = 28;
    private static final int WAVE_DELAY = 60;
    private static final float DISPLAY_ROTATION_SPEED = 1.5f;
    private static final int MOB_SPAWN_INTERVAL = 5;
    private static final double MOB_TRACKING_RANGE = 64.0;

    private static final int ACTIVATION_PARTICLE_DURATION = 5;
    private static final int WAVE_PARTICLE_DURATION = 5;

    private EntityType<?> entityType = null;
    private boolean isActive = false;
    private int spawnDelay = 0;
    private Set<UUID> currentWaveMobs = new HashSet<>();
    private Random random = new Random();

    private boolean isWaveActive = false;
    private int currentWaveSize = 0;
    private int waveDelayCounter = 0;
    private int currentWaveNumber = 1;

    private int pendingSpawns = 0;
    private int nextSpawnDelay = 0;

    private Entity cachedDisplayEntity = null;
    private double rotation = 0.0;
    private double lastRotation = 0.0;

    private long lastKeyUsageTime = 0;
    private boolean wasInCooldown = false;

    private int activationParticleTimer = 0;
    private boolean isActivating = false;
    private int waveParticleTimer = 0;
    private boolean isSpawningWave = false;
    private int deactivationParticleTimer = 0;
    private boolean isDeactivating = false;

    public ReinforcedSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REINFORCED_SPAWNER_BLOCK_ENTITY, pos, state);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, ReinforcedSpawnerBlockEntity spawner) {
        if (!world.isClient()) {
            spawner.updateDisplayRotation();
            spawner.checkForNearbyKeyHolders(world, pos, state);

            spawner.cleanupWaveMobs((ServerWorld) world);

            if (spawner.activationParticleTimer > 0) {
                if (spawner.activationParticleTimer == ACTIVATION_PARTICLE_DURATION) {
                    spawner.spawnActivationParticles((ServerWorld) world, pos);
                }
                spawner.activationParticleTimer--;
                if (spawner.activationParticleTimer == 0) {
                    spawner.isActivating = false;
                }
            }

            if (spawner.deactivationParticleTimer > 0) {
                if (spawner.deactivationParticleTimer == ACTIVATION_PARTICLE_DURATION) {
                    spawner.spawnDeactivationParticles((ServerWorld) world, pos);
                }
                spawner.deactivationParticleTimer--;
                if (spawner.deactivationParticleTimer == 0) {
                    spawner.isDeactivating = false;
                }
            }

            if (spawner.waveParticleTimer > 0) {
                if (spawner.waveParticleTimer == WAVE_PARTICLE_DURATION) {
                    spawner.spawnWaveParticles((ServerWorld) world, pos);
                }
                spawner.waveParticleTimer--;
                if (spawner.waveParticleTimer == 0) {
                    spawner.isSpawningWave = false;
                }
            }

            if (state.get(ReinforcedSpawnerBlock.ACTIVE)) {
                spawner.tick((ServerWorld) world);
            }
        }
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, ReinforcedSpawnerBlockEntity spawner) {
        if (world.isClient()) {
            spawner.updateDisplayRotation();

            if (state.get(ReinforcedSpawnerBlock.ACTIVE)) {
                spawner.tickClient(world, pos);
            }
        }
    }

    public Entity getDisplayEntity(World world) {
        if (entityType == null) return null;

        if (cachedDisplayEntity == null || cachedDisplayEntity.getType() != entityType) {
            cachedDisplayEntity = entityType.create(world);
        }
        return cachedDisplayEntity;
    }

    public double getRotation() {
        return this.rotation;
    }

    public double getLastRotation() {
        return this.lastRotation;
    }

    public float getDisplayRotation(float tickDelta) {
        return (float)(this.lastRotation + (this.rotation - this.lastRotation) * tickDelta);
    }

    private void updateDisplayRotation() {
        this.lastRotation = this.rotation;
        this.rotation += DISPLAY_ROTATION_SPEED;
    }

    private void checkForNearbyKeyHolders(World world, BlockPos pos, BlockState state) {
        long currentTime = world.getTime();
        boolean isInCooldown = (currentTime - lastKeyUsageTime) < KEY_USAGE_COOLDOWN;

        if (wasInCooldown && !isInCooldown) {
            wasInCooldown = false;
        } else if (isInCooldown) {
            wasInCooldown = true;
            return;
        } else {
            wasInCooldown = false;
        }

        Box detectionBox = new Box(pos).expand(KEY_DETECTION_RADIUS);
        List<PlayerEntity> nearbyPlayers = world.getEntitiesByClass(PlayerEntity.class, detectionBox,
                player -> player.isAlive() && !player.isSpectator());

        boolean hasKeyHolder = false;
        for (PlayerEntity player : nearbyPlayers) {
            if (player.getMainHandStack().getItem() instanceof ReinforcedKeyItem ||
                    player.getOffHandStack().getItem() instanceof ReinforcedKeyItem) {
                hasKeyHolder = true;
                break;
            }
        }

        boolean currentKeyhole = state.get(ReinforcedSpawnerBlock.KEYHOLE);
        if (currentKeyhole != hasKeyHolder) {
            world.setBlockState(pos, state.with(ReinforcedSpawnerBlock.KEYHOLE, hasKeyHolder));

            if (hasKeyHolder) {
                world.playSound(null, pos, SoundEvents.BLOCK_VAULT_ACTIVATE,
                        SoundCategory.BLOCKS, 1.0f, 1.0f);
            } else {
                world.playSound(null, pos, SoundEvents.BLOCK_VAULT_DEACTIVATE,
                        SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
        }
    }

    private void tickClient(World world, BlockPos pos) {
        if (waveDelayCounter > 0) {
            emitIdleParticles(world, pos);
        }

        if (random.nextFloat() < 0.3f) {
            double d = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
            double e = (double)pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
            double f = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
            world.addParticle(ParticleTypes.FLAME, d, e, f, 0.0, 0.0, 0.0);
        }

        if (random.nextFloat() <= 0.02F) {
            world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.BLOCK_TRIAL_SPAWNER_AMBIENT, SoundCategory.BLOCKS,
                    1.0f, 1.0f, false);
        }
    }

    private void emitIdleParticles(World world, BlockPos pos) {
        if (random.nextFloat() < 0.3f) {
            double d = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            double e = (double)pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            double f = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            world.addParticle(ParticleTypes.SMALL_FLAME, d, e, f, 0.0, 0.0, 0.0);
        }
    }

    private void spawnActivationParticles(ServerWorld world, BlockPos pos) {
        double centerX = pos.getX() + 0.5;
        double centerY = pos.getY() + 0.5;
        double centerZ = pos.getZ() + 0.5;

        int particleCount = 15;
        double radius = 5.0 / 16.0;
        double topY = centerY + 0.5;

        for (int i = 0; i < particleCount; i++) {
            double angle = (2 * Math.PI * i) / particleCount;
            double x = centerX + radius * Math.cos(angle);
            double z = centerZ + radius * Math.sin(angle);

            world.spawnParticles(EarthWater.REINFORCED_SPAWNER_DETECTION_INNER,
                    x, topY, z,
                    2, 0, 0.6, 0, 0);
        }

        int outwardCount = 26 + random.nextInt(6);

        for (int i = 0; i < outwardCount; i++) {
            world.spawnParticles(EarthWater.REINFORCED_SPAWNER_DETECTION_OUTWARD,
                    centerX, centerY, centerZ,
                    1, 0, 0, 0, 0);
        }
    }

    private void spawnDeactivationParticles(ServerWorld world, BlockPos pos) {
        double centerX = pos.getX() + 0.5;
        double centerY = pos.getY() + 0.5;
        double centerZ = pos.getZ() + 0.5;

        int particleCount = 10;
        double radius = 5.0 / 16.0;
        double topY = centerY + 0.5;

        for (int i = 0; i < particleCount; i++) {
            double angle = (2 * Math.PI * i) / particleCount;
            double x = centerX + radius * Math.cos(angle);
            double z = centerZ + radius * Math.sin(angle);

            world.spawnParticles(EarthWater.REINFORCED_SPAWNER_DETECTION,
                    x, topY, z,
                    1, 0, 0.3, 0, 0);
        }

        int outwardCount = 26 + random.nextInt(6);

        for (int i = 0; i < outwardCount; i++) {
            double angle = (2 * Math.PI * i) / outwardCount;
            double velX = Math.cos(angle);
            double velZ = Math.sin(angle);

            world.spawnParticles(EarthWater.REINFORCED_SPAWNER_DETECTION_OUTWARD,
                    centerX, centerY, centerZ,
                    1, velX * 0.08, 0, velZ * 0.08, 0);
        }
    }

    private void spawnWaveParticles(ServerWorld world, BlockPos pos) {
        double centerX = pos.getX() + 0.5;
        double centerY = pos.getY() + 0.5;
        double centerZ = pos.getZ() + 0.5;

        int particleCount = 10;
        double radius = 5.0 / 16.0;
        double topY = centerY + 0.5;

        for (int i = 0; i < particleCount; i++) {
            double angle = (2 * Math.PI * i) / particleCount;
            double x = centerX + radius * Math.cos(angle);
            double z = centerZ + radius * Math.sin(angle);

            world.spawnParticles(EarthWater.REINFORCED_SPAWNER_DETECTION,
                    x, topY, z,
                    1, 0, 0.3, 0, 0);
        }
    }

    private void tick(ServerWorld world) {
        if (!isActive || entityType == null) return;

        List<PlayerEntity> nearbyPlayers = getNearbyPlayers(world);
        if (nearbyPlayers.isEmpty()) {
            return;
        }

        cleanupWaveMobs(world);

        if (pendingSpawns > 0) {
            if (nextSpawnDelay > 0) {
                nextSpawnDelay--;
            } else {
                if (trySpawnMob(world)) {
                    pendingSpawns--;
                }
                nextSpawnDelay = MOB_SPAWN_INTERVAL;
            }
        }

        if (waveDelayCounter > 0) {
            waveDelayCounter--;
            return;
        }

        if (isWaveActive && pendingSpawns == 0 && currentWaveMobs.isEmpty()) {
            isWaveActive = false;
            waveDelayCounter = WAVE_DELAY;
            currentWaveNumber++;

            world.playSound(null, pos, SoundEvents.BLOCK_TRIAL_SPAWNER_SPAWN_MOB,
                    SoundCategory.BLOCKS, 1.0f, 1.5f);
        }

        if (!isWaveActive && spawnDelay <= 0) {
            startNewWave(world, nearbyPlayers.size());
        }

        if (spawnDelay > 0) {
            spawnDelay--;
        }
    }

    private void cleanupWaveMobs(ServerWorld world) {
        currentWaveMobs.removeIf(uuid -> {
            Entity entity = world.getEntity(uuid);
            if (entity == null || !entity.isAlive()) {
                return true;
            }

            double distance = entity.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            if (distance > MOB_TRACKING_RANGE * MOB_TRACKING_RANGE) {
                return true;
            }

            return false;
        });
    }

    private void startNewWave(ServerWorld world, int playerCount) {
        currentWaveSize = BASE_WAVE_SIZE + (playerCount - 1) * ADDITIONAL_MOBS_PER_PLAYER;
        isWaveActive = true;

        pendingSpawns = currentWaveSize;
        nextSpawnDelay = 0;

        if (!isActivating && !isDeactivating) {
            isSpawningWave = true;
            waveParticleTimer = WAVE_PARTICLE_DURATION;
        }

        world.playSound(null, pos, SoundEvents.BLOCK_TRIAL_SPAWNER_AMBIENT,
                SoundCategory.BLOCKS, 1.0f, 0.8f);

        spawnDelay = SPAWN_DELAY;
    }

    private List<PlayerEntity> getNearbyPlayers(ServerWorld world) {
        Box detectionBox = new Box(pos).expand(DETECTION_RADIUS);
        return world.getEntitiesByClass(PlayerEntity.class, detectionBox,
                player -> player.isAlive() && !player.isSpectator() && !player.isCreative());
    }

    private boolean trySpawnMob(ServerWorld world) {
        for (int i = 0; i < 4; i++) {
            double x = pos.getX() + (random.nextDouble() - 0.5) * 8.0;
            double y = pos.getY() + random.nextInt(3) - 1;
            double z = pos.getZ() + (random.nextDouble() - 0.5) * 8.0;

            BlockPos spawnPos = BlockPos.ofFloored(x, y, z);

            if (world.isSpaceEmpty(new Box(spawnPos).expand(0.5))) {
                Entity entity = entityType.create(world);
                if (entity instanceof MobEntity mob) {
                    mob.refreshPositionAndAngles(x, y, z, random.nextFloat() * 360, 0);
                    mob.initialize(world, world.getLocalDifficulty(spawnPos),
                            SpawnReason.SPAWNER, null);

                    if (world.spawnEntity(mob)) {
                        currentWaveMobs.add(mob.getUuid());

                        world.playSound(null, spawnPos, SoundEvents.BLOCK_TRIAL_SPAWNER_SPAWN_MOB,
                                SoundCategory.BLOCKS, 1.0f, 1.0f);

                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void activate() {
        this.isActive = true;
        this.spawnDelay = 0;
        this.isWaveActive = false;
        this.waveDelayCounter = 0;
        this.currentWaveNumber = 1;
        this.pendingSpawns = 0;
        this.nextSpawnDelay = 0;

        this.isActivating = true;
        this.activationParticleTimer = ACTIVATION_PARTICLE_DURATION;

        markDirty();
    }

    public void deactivate() {
        this.isActive = false;
        this.spawnDelay = 0;
        this.isWaveActive = false;
        this.waveDelayCounter = 0;
        this.pendingSpawns = 0;
        this.nextSpawnDelay = 0;

        this.isDeactivating = true;
        this.deactivationParticleTimer = ACTIVATION_PARTICLE_DURATION;

        markDirty();
    }

    public void setEntityType(EntityType<?> entityType) {
        this.entityType = entityType;
        this.cachedDisplayEntity = null;
        markDirty();

        if (world != null && !world.isClient()) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    public EntityType<?> getEntityType() {
        return this.entityType;
    }

    public void onKeyUsed(World world) {
        this.lastKeyUsageTime = world.getTime();
        markDirty();
    }

    public boolean canUseKey(World world) {
        return (world.getTime() - lastKeyUsageTime) >= KEY_USAGE_COOLDOWN;
    }

    public int getCurrentWaveNumber() {
        return this.currentWaveNumber;
    }

    public boolean isWaveActive() {
        return this.isWaveActive;
    }

    public int getRemainingMobs() {
        return this.currentWaveMobs.size();
    }

    public boolean isInWaveDelay() {
        return this.waveDelayCounter > 0;
    }

    public int getPendingSpawns() {
        return this.pendingSpawns;
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return createNbt(registries);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        if (nbt.contains("EntityType")) {
            String entityTypeId = nbt.getString("EntityType");
            this.entityType = Registries.ENTITY_TYPE.get(Identifier.tryParse(entityTypeId));
        }

        this.isActive = nbt.getBoolean("Active");
        this.spawnDelay = nbt.getInt("SpawnDelay");
        this.rotation = nbt.getDouble("Rotation");
        this.lastRotation = nbt.getDouble("LastRotation");
        this.lastKeyUsageTime = nbt.getLong("LastKeyUsageTime");
        this.wasInCooldown = nbt.getBoolean("WasInCooldown");

        this.isWaveActive = nbt.getBoolean("IsWaveActive");
        this.currentWaveSize = nbt.getInt("CurrentWaveSize");
        this.waveDelayCounter = nbt.getInt("WaveDelayCounter");
        this.currentWaveNumber = nbt.getInt("CurrentWaveNumber");

        this.pendingSpawns = nbt.getInt("PendingSpawns");
        this.nextSpawnDelay = nbt.getInt("NextSpawnDelay");

        this.activationParticleTimer = nbt.getInt("ActivationParticleTimer");
        this.isActivating = nbt.getBoolean("IsActivating");
        this.waveParticleTimer = nbt.getInt("WaveParticleTimer");
        this.isSpawningWave = nbt.getBoolean("IsSpawningWave");
        this.deactivationParticleTimer = nbt.getInt("DeactivationParticleTimer");
        this.isDeactivating = nbt.getBoolean("IsDeactivating");

        this.currentWaveMobs.clear();
        int mobCount = nbt.getInt("CurrentWaveMobsCount");
        for (int i = 0; i < mobCount; i++) {
            String key = "CurrentWaveMob_" + i;
            if (nbt.contains(key)) {
                try {
                    UUID uuid = UUID.fromString(nbt.getString(key));
                    this.currentWaveMobs.add(uuid);
                } catch (IllegalArgumentException e) {
                }
            }
        }

        this.cachedDisplayEntity = null;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);

        if (this.entityType != null) {
            Identifier entityTypeId = Registries.ENTITY_TYPE.getId(this.entityType);
            nbt.putString("EntityType", entityTypeId.toString());
        }

        nbt.putBoolean("Active", this.isActive);
        nbt.putInt("SpawnDelay", this.spawnDelay);
        nbt.putDouble("Rotation", this.rotation);
        nbt.putDouble("LastRotation", this.lastRotation);
        nbt.putLong("LastKeyUsageTime", this.lastKeyUsageTime);
        nbt.putBoolean("WasInCooldown", this.wasInCooldown);

        nbt.putBoolean("IsWaveActive", this.isWaveActive);
        nbt.putInt("CurrentWaveSize", this.currentWaveSize);
        nbt.putInt("WaveDelayCounter", this.waveDelayCounter);
        nbt.putInt("CurrentWaveNumber", this.currentWaveNumber);

        nbt.putInt("PendingSpawns", this.pendingSpawns);
        nbt.putInt("NextSpawnDelay", this.nextSpawnDelay);

        nbt.putInt("ActivationParticleTimer", this.activationParticleTimer);
        nbt.putBoolean("IsActivating", this.isActivating);
        nbt.putInt("WaveParticleTimer", this.waveParticleTimer);
        nbt.putBoolean("IsSpawningWave", this.isSpawningWave);
        nbt.putInt("DeactivationParticleTimer", this.deactivationParticleTimer);
        nbt.putBoolean("IsDeactivating", this.isDeactivating);

        nbt.putInt("CurrentWaveMobsCount", this.currentWaveMobs.size());
        int index = 0;
        for (UUID uuid : this.currentWaveMobs) {
            nbt.putString("CurrentWaveMob_" + index, uuid.toString());
            index++;
        }
    }
}