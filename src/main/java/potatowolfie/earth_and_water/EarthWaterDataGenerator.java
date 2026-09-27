package potatowolfie.earth_and_water;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import potatowolfie.earth_and_water.datagen.*;
import potatowolfie.earth_and_water.trim.ModTrimMaterials;
import potatowolfie.earth_and_water.trim.ModTrimPatterns;
import potatowolfie.earth_and_water.world.feature.ModConfiguredFeatures;
import potatowolfie.earth_and_water.world.feature.ModPlacedFeatures;

public class EarthWaterDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(ModBlockTagProvider::new);
		pack.addProvider(ModItemTagProvider::new);
		pack.addProvider(ModLootTableGenerator::new);
		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModRecipeGenerator::new);
		pack.addProvider(ModRegistryDataGenerator::new);
		pack.addProvider(ModWorldGenerator::new);
	}

	@Override
	public void buildRegistry(RegistrySetBuilder registryBuilder) {
		registryBuilder.add(Registries.TRIM_MATERIAL, ModTrimMaterials::bootstrap);
		registryBuilder.add(Registries.TRIM_PATTERN, ModTrimPatterns::bootstrap);
		registryBuilder.add(Registries.FEATURE, ModConfiguredFeatures::bootstrap);
		registryBuilder.add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap);
	}
}