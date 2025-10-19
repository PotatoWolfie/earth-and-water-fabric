package potatowolfie.earth_and_water.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.block.ModBlocks;
import potatowolfie.earth_and_water.block.entity.custom.ReinforcedSpawnerBlockEntity;

public class ModBlockEntities {

    public static final BlockEntityType<ReinforcedSpawnerBlockEntity> REINFORCED_SPAWNER_BLOCK_ENTITY =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of("earth-and-water", "reinforced_spawner"),
                    FabricBlockEntityTypeBuilder.create(ReinforcedSpawnerBlockEntity::new, ModBlocks.REINFORCED_SPAWNER).build()
            );

    public static void registerBlockEntities() {
        EarthWater.LOGGER.info("Registering Block Entities for " + EarthWater.MOD_ID);
    }
}