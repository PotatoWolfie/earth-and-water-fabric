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
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
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

    public ReinforcedSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REINFORCED_SPAWNER_BLOCK_ENTITY, pos, state);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, ReinforcedSpawnerBlockEntity spawner) {
        if (!world.isClient) {
            spawner.updateDisplayRotation();
            spawner.checkForNearbyKeyHolders(world, pos, state);

            if (state.get(ReinforcedSpawnerBlock.ACTIVE)) {
                spawner.tick((ServerWorld) world);
            }
        }
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, ReinforcedSpawnerBlockEntity spawner) {
        if (world.isClient) {
            spawner.updateDisplayRotation();

            if (state.get(ReinforcedSpawnerBlock.ACTIVE)) {
                spawner.tickClient(world, pos);
            }
        }
    }

    public Entity getDisplayEntity(World world) {
        if (entityType == null) return null;

        if (cachedDisplayEntity == null || cachedDisplayEntity.getType() != entityType) {
            cachedDisplayEntity = entityType.create(world, SpawnReason.SPAWNER);
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

        wasInCooldown = isInCooldown;

        if (isInCooldown) {
            return;
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

        if (random.nextFloat() <= 0.02F) {
            world.playSound(null, pos, SoundEvents.BLOCK_TRIAL_SPAWNER_AMBIENT,
                    SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }

    private void emitIdleParticles(World world, BlockPos pos) {
        if (random.nextFloat() < 0.3f) {
            double d = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            double e = (double)pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            double f = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            world.addParticleClient(ParticleTypes.SMALL_FLAME, d, e, f, 0.0, 0.0, 0.0);
        }
    }

    private void tick(ServerWorld world) {
        if (!isActive || entityType == null) return;

        List<PlayerEntity> nearbyPlayers = getNearbyPlayers(world);
        if (nearbyPlayers.isEmpty()) {
            return;
        }

        // Modified mob tracking logic - remove mobs that are dead, null, or out of range
        currentWaveMobs.removeIf(uuid -> {
            Entity entity = world.getEntity(uuid);
            if (entity == null || !entity.isAlive()) {
                return true; // Remove dead or null entities
            }

            // Check if mob is beyond the tracking range
            double distance = entity.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            if (distance > MOB_TRACKING_RANGE * MOB_TRACKING_RANGE) {
                return true; // Remove mobs that are too far away
            }

            return false; // Keep the mob
        });

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

    private void startNewWave(ServerWorld world, int playerCount) {
        currentWaveSize = BASE_WAVE_SIZE + (playerCount - 1) * ADDITIONAL_MOBS_PER_PLAYER;
        isWaveActive = true;

        pendingSpawns = currentWaveSize;
        nextSpawnDelay = 0;

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
                Entity entity = entityType.create(world, SpawnReason.SPAWNER);
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
        this.currentWaveMobs.clear();
        this.spawnDelay = 0;
        this.isWaveActive = false;
        this.waveDelayCounter = 0;
        this.currentWaveNumber = 1;
        this.pendingSpawns = 0;
        this.nextSpawnDelay = 0;
        markDirty();
    }

    public void deactivate() {
        this.isActive = false;
        this.currentWaveMobs.clear();
        this.spawnDelay = 0;
        this.isWaveActive = false;
        this.waveDelayCounter = 0;
        this.currentWaveNumber = 1;
        this.pendingSpawns = 0;
        this.nextSpawnDelay = 0;
        markDirty();
    }

    public void setEntityType(EntityType<?> entityType) {
        this.entityType = entityType;
        this.cachedDisplayEntity = null;
        markDirty();

        if (world != null && !world.isClient) {
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
        return createComponentlessNbt(registries);
    }

    @Override
    public void readData(ReadView readView) {
        super.readData(readView);

        readView.getOptionalString("EntityType").ifPresent(entityTypeId -> {
            this.entityType = Registries.ENTITY_TYPE.get(Identifier.of(entityTypeId));
        });

        this.isActive = readView.getBoolean("Active", false);
        this.spawnDelay = readView.getInt("SpawnDelay", 0);
        this.rotation = readView.getDouble("Rotation", 0.0);
        this.lastRotation = readView.getDouble("LastRotation", 0.0);
        this.lastKeyUsageTime = readView.getLong("LastKeyUsageTime", 0);
        this.wasInCooldown = readView.getBoolean("WasInCooldown", false);

        this.isWaveActive = readView.getBoolean("IsWaveActive", false);
        this.currentWaveSize = readView.getInt("CurrentWaveSize", 0);
        this.waveDelayCounter = readView.getInt("WaveDelayCounter", 0);
        this.currentWaveNumber = readView.getInt("CurrentWaveNumber", 1);

        this.pendingSpawns = readView.getInt("PendingSpawns", 0);
        this.nextSpawnDelay = readView.getInt("NextSpawnDelay", 0);

        this.currentWaveMobs.clear();
        int mobCount = readView.getInt("CurrentWaveMobsCount", 0);
        for (int i = 0; i < mobCount; i++) {
            readView.getOptionalString("CurrentWaveMob_" + i).ifPresent(uuidString -> {
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
    public void writeData(WriteView writeView) {
        super.writeData(writeView);

        if (this.entityType != null) {
            Identifier entityTypeId = Registries.ENTITY_TYPE.getId(this.entityType);
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

        writeView.putInt("CurrentWaveMobsCount", this.currentWaveMobs.size());
        int index = 0;
        for (UUID uuid : this.currentWaveMobs) {
            writeView.putString("CurrentWaveMob_" + index, uuid.toString());
            index++;
        }
    }
}