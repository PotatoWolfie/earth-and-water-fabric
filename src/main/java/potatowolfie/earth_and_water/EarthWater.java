package potatowolfie.earth_and_water;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ProjectileItem;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import potatowolfie.earth_and_water.block.ModBlocks;
import potatowolfie.earth_and_water.block.entity.ModBlockEntities;
import potatowolfie.earth_and_water.datagen.ModLootTableModifier;
import potatowolfie.earth_and_water.effect.ModEffects;
import potatowolfie.earth_and_water.entity.ModEntities;
import potatowolfie.earth_and_water.entity.brine.BrineEntity;
import potatowolfie.earth_and_water.entity.bore.BoreEntity;
import potatowolfie.earth_and_water.item.ModItems;
import potatowolfie.earth_and_water.sound.ModSounds;
import potatowolfie.earth_and_water.structure.ModStructurePieceTypes;
import potatowolfie.earth_and_water.structure.ModStructureTypes;
import potatowolfie.earth_and_water.world.feature.ModFeatures;
import potatowolfie.earth_and_water.world.gen.ModWorldGeneration;

public class EarthWater implements ModInitializer {
	public static final String MOD_ID = "earth-and-water";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final SimpleParticleType LIGHT_UP = FabricParticleTypes.simple();
	public static final SimpleParticleType REINFORCED_SPAWNER_DETECTION = FabricParticleTypes.simple();
	public static final SimpleParticleType REINFORCED_SPAWNER_DETECTION_OUTWARD = FabricParticleTypes.simple();
	public static final SimpleParticleType REINFORCED_SPAWNER_DETECTION_INNER = FabricParticleTypes.simple();

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModEffects.registerEffects();
		ModEntities.registerModEntities();
		ModBlockEntities.registerBlockEntities();
		ModSounds.registerSounds();
		ModFeatures.registerModFeatures();
		ModWorldGeneration.init();
		ModStructureTypes.registerStructureTypes();
		ModStructurePieceTypes.registerStructurePieceTypes();
		ModLootTableModifier.modifyLootTables();
		registerChunkLoadEvent();

		Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "light_up"), LIGHT_UP);
		Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "reinforced_spawner_detection"), REINFORCED_SPAWNER_DETECTION);
		Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "reinforced_spawner_detection_outward"), REINFORCED_SPAWNER_DETECTION_OUTWARD);
		Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "reinforced_spawner_detection_inner"), REINFORCED_SPAWNER_DETECTION_INNER);

		registerDispenserBehaviors();

		FabricDefaultAttributeRegistry.register(ModEntities.BORE, BoreEntity.createBoreAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.BRINE, BrineEntity.createBrineAttributes());

		SpawnRestriction.register(
				ModEntities.BRINE,
				SpawnLocationTypes.IN_WATER,
				Heightmap.Type.OCEAN_FLOOR,
				BrineEntity::canSpawn
		);

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			server.getOverworld().scheduleBlockTick(BlockPos.ORIGIN, Blocks.CAULDRON, 1);
			server.getOverworld().scheduleBlockTick(BlockPos.ORIGIN, Blocks.LAVA_CAULDRON, 1);
			server.getOverworld().scheduleBlockTick(BlockPos.ORIGIN, Blocks.WATER_CAULDRON, 1);
			server.getOverworld().scheduleBlockTick(BlockPos.ORIGIN, Blocks.POWDER_SNOW_CAULDRON, 1);
		});

		LOGGER.info("Earth and Water mod initialized!");
	}

	private void registerDispenserBehaviors() {
		registerProjectileDispenserBehavior(ModItems.WATER_CHARGE);
		registerProjectileDispenserBehavior(ModItems.EARTH_CHARGE);
	}

	private void registerChunkLoadEvent() {
		ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
			scheduleBlockTicksForChunk(world, chunk);
		});
	}

	private void scheduleBlockTicksForChunk(ServerWorld world, WorldChunk chunk) {
		BlockPos.Mutable pos = new BlockPos.Mutable();

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int minY = world.getBottomY();
				int maxY = minY + world.getHeight();

				for (int y = minY; y < maxY; y++) {
					pos.set(chunk.getPos().getStartX() + x, y, chunk.getPos().getStartZ() + z);
					var state = chunk.getBlockState(pos);
					var block = state.getBlock();

					if (block == ModBlocks.CHISELED_DARK_PRISMARINE ||
							block == ModBlocks.CHISELED_PRISMARINE_BRICKS ||
							block == ModBlocks.CHISELED_DARK_DRIPSTONE_BRICKS ||
							block == ModBlocks.CHISELED_DRIPSTONE_BRICKS) {

						world.scheduleBlockTick(pos.toImmutable(), block, 2);
					}
				}
			}
		}
	}

	private void registerProjectileDispenserBehavior(Item item) {
		DispenserBlock.registerBehavior(
				item,
				(pointer, stack) -> {
					World world = pointer.world();
					Position position = DispenserBlock.getOutputLocation(pointer);
					Direction direction = pointer.state().get(DispenserBlock.FACING);

					ProjectileItem projectileItem = (ProjectileItem) stack.getItem();
					ProjectileEntity projectileEntity = projectileItem.createEntity(world, position, stack, direction);

					projectileEntity.setVelocity(
							direction.getOffsetX(),
							direction.getOffsetY() + 0.1F,
							direction.getOffsetZ(),
							1.5F,
							0.1F
					);

					world.spawnEntity(projectileEntity);
					stack.decrement(1);
					return stack;
				}
		);
	}
}