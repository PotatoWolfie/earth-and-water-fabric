package potatowolfie.earth_and_water.block.entity.custom;

import net.minecraft.world.item.component.TypedEntityData;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.block.custom.ReinforcedSpawnerBlock;
import potatowolfie.earth_and_water.block.entity.ModBlockEntities;
import potatowolfie.earth_and_water.item.custom.ReinforcedKeyItem;

import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

public class ReinforcedSpawnerBlockEntity extends BlockEntity implements Spawner {
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

    private final BaseSpawner spawner = new BaseSpawner() {
        @Override
        public void broadcastEvent(Level level, BlockPos pos, int id) {
            level.blockEvent(pos, level.getBlockState(pos).getBlock(), id, 0);
        }

        @Override
        public void setNextSpawnData(@Nullable Level level, BlockPos pos, SpawnData nextSpawnData) {
            super.setNextSpawnData(level, pos, nextSpawnData);
            if (level != null) {
                BlockState state = level.getBlockState(pos);
                level.sendBlockUpdated(pos, state, state, 260);
            }
        }
    };

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

    public static void serverTick(Level world, BlockPos pos, BlockState state, ReinforcedSpawnerBlockEntity spawner) {
        if (!world.isClientSide()) {
            spawner.updateDisplayRotation();
            spawner.checkForNearbyKeyHolders(world, pos, state);

            spawner.cleanupWaveMobs((ServerLevel) world);

            if (spawner.activationParticleTimer > 0) {
                if (spawner.activationParticleTimer == ACTIVATION_PARTICLE_DURATION) {
                    spawner.spawnActivationParticles((ServerLevel) world, pos);
                }
                spawner.activationParticleTimer--;
                if (spawner.activationParticleTimer == 0) {
                    spawner.isActivating = false;
                }
            }

            if (spawner.deactivationParticleTimer > 0) {
                if (spawner.deactivationParticleTimer == ACTIVATION_PARTICLE_DURATION) {
                    spawner.spawnDeactivationParticles((ServerLevel) world, pos);
                }
                spawner.deactivationParticleTimer--;
                if (spawner.deactivationParticleTimer == 0) {
                    spawner.isDeactivating = false;
                }
            }

            if (spawner.waveParticleTimer > 0) {
                if (spawner.waveParticleTimer == WAVE_PARTICLE_DURATION) {
                    spawner.spawnWaveParticles((ServerLevel) world, pos);
                }
                spawner.waveParticleTimer--;
                if (spawner.waveParticleTimer == 0) {
                    spawner.isSpawningWave = false;
                }
            }

            if (state.getValue(ReinforcedSpawnerBlock.ACTIVE)) {
                spawner.tick((ServerLevel) world);
            }
        }
    }

    public static void clientTick(Level world, BlockPos pos, BlockState state, ReinforcedSpawnerBlockEntity spawner) {
        if (world.isClientSide()) {
            spawner.updateDisplayRotation();

            if (state.getValue(ReinforcedSpawnerBlock.ACTIVE)) {
                spawner.tickClient(world, pos);
            }
        }
    }

    public Entity getDisplayEntity(Level world) {
        if (entityType == null) return null;

        if (cachedDisplayEntity == null || cachedDisplayEntity.getType() != entityType) {
            cachedDisplayEntity = entityType.create(world, EntitySpawnReason.SPAWNER);
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

    private void checkForNearbyKeyHolders(Level world, BlockPos pos, BlockState state) {
        long currentTime = world.getGameTime();
        boolean isInCooldown = (currentTime - lastKeyUsageTime) < KEY_USAGE_COOLDOWN;

        if (wasInCooldown && !isInCooldown) {
            wasInCooldown = false;
        } else if (isInCooldown) {
            wasInCooldown = true;
            return;
        } else {
            wasInCooldown = false;
        }

        AABB detectionBox = new AABB(pos).inflate(KEY_DETECTION_RADIUS);
        List<Player> nearbyPlayers = world.getEntitiesOfClass(Player.class, detectionBox,
                player -> player.isAlive() && !player.isSpectator());

        boolean hasKeyHolder = false;
        for (Player player : nearbyPlayers) {
            if (player.getMainHandItem().getItem() instanceof ReinforcedKeyItem ||
                    player.getOffhandItem().getItem() instanceof ReinforcedKeyItem) {
                hasKeyHolder = true;
                break;
            }
        }

        boolean currentKeyhole = state.getValue(ReinforcedSpawnerBlock.KEYHOLE);
        if (currentKeyhole != hasKeyHolder) {
            world.setBlockAndUpdate(pos, state.setValue(ReinforcedSpawnerBlock.KEYHOLE, hasKeyHolder));

            if (hasKeyHolder) {
                world.playSound(null, pos, SoundEvents.VAULT_ACTIVATE,
                        SoundSource.BLOCKS, 1.0f, 1.0f);
            } else {
                world.playSound(null, pos, SoundEvents.VAULT_DEACTIVATE,
                        SoundSource.BLOCKS, 1.0f, 1.0f);
            }
        }
    }

    private void tickClient(Level world, BlockPos pos) {
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
            world.playSound(null, pos, SoundEvents.TRIAL_SPAWNER_AMBIENT,
                    SoundSource.BLOCKS, 1.0f, 1.0f);
        }
    }

    private void emitIdleParticles(Level world, BlockPos pos) {
        if (random.nextFloat() < 0.3f) {
            double d = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            double e = (double)pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            double f = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            world.addParticle(ParticleTypes.SMALL_FLAME, d, e, f, 0.0, 0.0, 0.0);
        }
    }

    private void spawnActivationParticles(ServerLevel world, BlockPos pos) {
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

            world.sendParticles(EarthWater.REINFORCED_SPAWNER_DETECTION_INNER,
                    x, topY, z,
                    2, 0, 0.6, 0, 0);
        }

        int outwardCount = 26 + random.nextInt(6);

        for (int i = 0; i < outwardCount; i++) {
            world.sendParticles(EarthWater.REINFORCED_SPAWNER_DETECTION_OUTWARD,
                    centerX, centerY, centerZ,
                    1, 0, 0, 0, 0);
        }
    }

    private void spawnDeactivationParticles(ServerLevel world, BlockPos pos) {
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

            world.sendParticles(EarthWater.REINFORCED_SPAWNER_DETECTION,
                    x, topY, z,
                    1, 0, 0.3, 0, 0);
        }

        int outwardCount = 26 + random.nextInt(6);

        for (int i = 0; i < outwardCount; i++) {
            double angle = (2 * Math.PI * i) / outwardCount;
            double velX = Math.cos(angle);
            double velZ = Math.sin(angle);

            world.sendParticles(EarthWater.REINFORCED_SPAWNER_DETECTION_OUTWARD,
                    centerX, centerY, centerZ,
                    1, velX * 0.08, 0, velZ * 0.08, 0);
        }
    }

    private void spawnWaveParticles(ServerLevel world, BlockPos pos) {
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

            world.sendParticles(EarthWater.REINFORCED_SPAWNER_DETECTION,
                    x, topY, z,
                    1, 0, 0.3, 0, 0);
        }
    }

    private void tick(ServerLevel world) {
        if (!isActive || entityType == null) return;

        List<Player> nearbyPlayers = getNearbyPlayers(world);
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

            world.playSound(null, worldPosition, SoundEvents.TRIAL_SPAWNER_SPAWN_MOB,
                    SoundSource.BLOCKS, 1.0f, 1.5f);
        }

        if (!isWaveActive && spawnDelay <= 0) {
            startNewWave(world, nearbyPlayers.size());
        }

        if (spawnDelay > 0) {
            spawnDelay--;
        }
    }

    private void cleanupWaveMobs(ServerLevel world) {
        currentWaveMobs.removeIf(uuid -> {
            Entity entity = world.getEntity(uuid);
            if (entity == null || !entity.isAlive()) {
                return true;
            }

            double distance = entity.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);
            if (distance > MOB_TRACKING_RANGE * MOB_TRACKING_RANGE) {
                return true;
            }

            return false;
        });
    }

    private void startNewWave(ServerLevel world, int playerCount) {
        currentWaveSize = BASE_WAVE_SIZE + (playerCount - 1) * ADDITIONAL_MOBS_PER_PLAYER;
        isWaveActive = true;

        pendingSpawns = currentWaveSize;
        nextSpawnDelay = 0;

        if (!isActivating && !isDeactivating) {
            isSpawningWave = true;
            waveParticleTimer = WAVE_PARTICLE_DURATION;
        }

        world.playSound(null, worldPosition, SoundEvents.TRIAL_SPAWNER_AMBIENT,
                SoundSource.BLOCKS, 1.0f, 0.8f);

        spawnDelay = SPAWN_DELAY;
    }

    private List<Player> getNearbyPlayers(ServerLevel world) {
        AABB detectionBox = new AABB(worldPosition).inflate(DETECTION_RADIUS);
        return world.getEntitiesOfClass(Player.class, detectionBox,
                player -> player.isAlive() && !player.isSpectator() && !player.isCreative());
    }

    private boolean trySpawnMob(ServerLevel world) {
        for (int i = 0; i < 4; i++) {
            double x = worldPosition.getX() + (random.nextDouble() - 0.5) * 8.0;
            double y = worldPosition.getY() + random.nextInt(3) - 1;
            double z = worldPosition.getZ() + (random.nextDouble() - 0.5) * 8.0;

            BlockPos spawnPos = BlockPos.containing(x, y, z);

            if (world.noCollision(new AABB(spawnPos).inflate(0.5))) {
                Entity entity = entityType.create(world, EntitySpawnReason.SPAWNER);
                if (entity instanceof Mob mob) {
                    mob.snapTo(x, y, z, random.nextFloat() * 360, 0);
                    mob.finalizeSpawn(world, world.getCurrentDifficultyAt(spawnPos),
                            EntitySpawnReason.SPAWNER, null);

                    if (world.addFreshEntity(mob)) {
                        currentWaveMobs.add(mob.getUUID());

                        world.playSound(null, spawnPos, SoundEvents.TRIAL_SPAWNER_SPAWN_MOB,
                                SoundSource.BLOCKS, 1.0f, 1.0f);

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

        setChanged();
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

        setChanged();
    }

    public void setEntityType(EntityType<?> entityType) {
        this.entityType = entityType;
        this.cachedDisplayEntity = null;

        if (this.level != null) {
            this.spawner.setEntityId(entityType, this.level, this.level.getRandom(), this.worldPosition);
        }
        setChanged();

        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public EntityType<?> getEntityType() {
        return this.entityType;
    }

    public void onKeyUsed(Level world) {
        this.lastKeyUsageTime = world.getGameTime();
        setChanged();
    }

    public boolean canUseKey(Level world) {
        return (world.getGameTime() - lastKeyUsageTime) >= KEY_USAGE_COOLDOWN;
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
    public void setEntityId(EntityType<?> type, RandomSource random) {
        this.spawner.setEntityId(type, this.level, random, this.worldPosition);
        this.entityType = type;
        this.cachedDisplayEntity = null;
        this.setChanged();
    }

    @Override
    public void setEntityData(TypedEntityData<EntityType<?>> entityData, RandomSource random) {

    }

    public BaseSpawner getSpawner() {
        return this.spawner;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = this.saveCustomOnly(registries);
        tag.remove("SpawnPotentials");
        return tag;
    }

    @Override
    public void loadAdditional(ValueInput readView) {
        super.loadAdditional(readView);
        this.spawner.load(this.level, this.worldPosition, readView);

        readView.getString("EntityType").ifPresent(entityTypeId -> {
            this.entityType = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse(entityTypeId));
        });

        this.isActive = readView.getBooleanOr("Active", false);
        this.spawnDelay = readView.getIntOr("SpawnDelay", 0);
        this.rotation = readView.getDoubleOr("Rotation", 0.0);
        this.lastRotation = readView.getDoubleOr("LastRotation", 0.0);
        this.lastKeyUsageTime = 0;
        this.wasInCooldown = readView.getBooleanOr("WasInCooldown", false);

        this.isWaveActive = readView.getBooleanOr("IsWaveActive", false);
        this.currentWaveSize = readView.getIntOr("CurrentWaveSize", 0);
        this.waveDelayCounter = readView.getIntOr("WaveDelayCounter", 0);
        this.currentWaveNumber = readView.getIntOr("CurrentWaveNumber", 1);

        this.pendingSpawns = readView.getIntOr("PendingSpawns", 0);
        this.nextSpawnDelay = readView.getIntOr("NextSpawnDelay", 0);

        this.activationParticleTimer = readView.getIntOr("ActivationParticleTimer", 0);
        this.isActivating = readView.getBooleanOr("IsActivating", false);
        this.waveParticleTimer = readView.getIntOr("WaveParticleTimer", 0);
        this.isSpawningWave = readView.getBooleanOr("IsSpawningWave", false);
        this.deactivationParticleTimer = readView.getIntOr("DeactivationParticleTimer", 0);
        this.isDeactivating = readView.getBooleanOr("IsDeactivating", false);

        this.currentWaveMobs.clear();
        int mobCount = readView.getIntOr("CurrentWaveMobsCount", 0);
        for (int i = 0; i < mobCount; i++) {
            readView.getString("CurrentWaveMob_" + i).ifPresent(uuidString -> {
                try {
                    UUID uuid = UUID.fromString(uuidString);
                    this.currentWaveMobs.add(uuid);
                } catch (IllegalArgumentException e) {
                }
            });
        }

        this.cachedDisplayEntity = null;
    }

    @Override
    public void saveAdditional(ValueOutput writeView) {
        super.saveAdditional(writeView);
        this.spawner.save(writeView);

        if (this.entityType != null) {
            Identifier entityTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(this.entityType);
            writeView.putString("EntityType", entityTypeId.toString());
        }

        writeView.putBoolean("Active", this.isActive);
        writeView.putInt("SpawnDelay", this.spawnDelay);
        writeView.putDouble("Rotation", this.rotation);
        writeView.putDouble("LastRotation", this.lastRotation);
        writeView.putLong("LastKeyUsageTime", this.lastKeyUsageTime);
        writeView.putBoolean("WasInCooldown", this.wasInCooldown);

        writeView.putBoolean("IsWaveActive", this.isWaveActive);
        writeView.putInt("CurrentWaveSize", this.currentWaveSize);
        writeView.putInt("WaveDelayCounter", this.waveDelayCounter);
        writeView.putInt("CurrentWaveNumber", this.currentWaveNumber);

        writeView.putInt("PendingSpawns", this.pendingSpawns);
        writeView.putInt("NextSpawnDelay", this.nextSpawnDelay);

        writeView.putInt("ActivationParticleTimer", this.activationParticleTimer);
        writeView.putBoolean("IsActivating", this.isActivating);
        writeView.putInt("WaveParticleTimer", this.waveParticleTimer);
        writeView.putBoolean("IsSpawningWave", this.isSpawningWave);
        writeView.putInt("DeactivationParticleTimer", this.deactivationParticleTimer);
        writeView.putBoolean("IsDeactivating", this.isDeactivating);

        writeView.putInt("CurrentWaveMobsCount", this.currentWaveMobs.size());
        int index = 0;
        for (UUID uuid : this.currentWaveMobs) {
            writeView.putString("CurrentWaveMob_" + index, uuid.toString());
            index++;
        }
    }
}