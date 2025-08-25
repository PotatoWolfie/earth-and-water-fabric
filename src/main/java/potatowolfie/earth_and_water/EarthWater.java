package potatowolfie.earth_and_water;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ProjectileItem;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import potatowolfie.earth_and_water.block.ModBlocks;
import potatowolfie.earth_and_water.datagen.ModLootTableModifier;
import potatowolfie.earth_and_water.effect.ModEffects;
import potatowolfie.earth_and_water.entity.ModEntities;
import potatowolfie.earth_and_water.entity.custom.BrineEntity;
import potatowolfie.earth_and_water.entity.custom.BoreEntity;
import potatowolfie.earth_and_water.item.ModItems;
import potatowolfie.earth_and_water.sound.ModSounds;
import potatowolfie.earth_and_water.world.biome.ModBiomes;
import potatowolfie.earth_and_water.world.biome.ModMaterialRules;
import potatowolfie.earth_and_water.world.feature.ModFeatures;
import potatowolfie.earth_and_water.world.gen.ModWorldGeneration;
import terrablender.api.SurfaceRuleManager;
import terrablender.api.TerraBlenderApi;

import static net.minecraft.component.DataComponentTypes.PROVIDES_TRIM_MATERIAL;

public class EarthWater implements ModInitializer, TerraBlenderApi {
	public static final String MOD_ID = "earth-and-water";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final SimpleParticleType LIGHT_UP = FabricParticleTypes.simple();

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModEffects.registerEffects();
		ModEntities.registerModEntities();
		ModSounds.registerSounds();
		ModFeatures.registerModFeatures();
		ModWorldGeneration.init();
		ModLootTableModifier.modifyLootTables();

		Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "light_up"), LIGHT_UP);

		registerDispenserBehaviors();

		FabricDefaultAttributeRegistry.register(ModEntities.BORE, BoreEntity.createBoreAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.BRINE, BrineEntity.createBrineAttributes());

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

	@Override
	public void onTerraBlenderInitialized() {
		ModBiomes.registerBiomes();
		SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, MOD_ID, ModMaterialRules.makeDarkDripstoneCavesRules());
	}
}