package potatowolfie.earth_and_water.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.entity.brine.BrineEntity;
import potatowolfie.earth_and_water.entity.bore.BoreEntity;
import potatowolfie.earth_and_water.entity.earth_charge.EarthChargeProjectileEntity;
import potatowolfie.earth_and_water.entity.water_charge.WaterChargeProjectileEntity;

public class ModEntities {

    public static final EntityType<EarthChargeProjectileEntity> EARTH_CHARGE = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(EarthWater.MOD_ID, "earth_charge"),
            EntityType.Builder.<EarthChargeProjectileEntity>create(EarthChargeProjectileEntity::new, SpawnGroup.MISC)
                    .dimensions(0.3125F, 0.3125F)
                    .build());

    public static final EntityType<WaterChargeProjectileEntity> WATER_CHARGE = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(EarthWater.MOD_ID, "water_charge"),
            EntityType.Builder.<WaterChargeProjectileEntity>create(WaterChargeProjectileEntity::new, SpawnGroup.MISC)
                    .dimensions(0.3125F, 0.3125F)
                    .build());

    public static final EntityType<BoreEntity> BORE = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(EarthWater.MOD_ID, "bore"),
            EntityType.Builder.create(BoreEntity::new, SpawnGroup.MONSTER)
                    .maxTrackingRange(84).dimensions(0.6F, 1.8F)
                    .build());

    public static final EntityType<BrineEntity> BRINE = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(EarthWater.MOD_ID, "brine"),
            EntityType.Builder.create(BrineEntity::new, SpawnGroup.MONSTER)
                    .maxTrackingRange(64).dimensions(0.8F, 1.8F)
                    .build());

    public static void registerModEntities() {
        EarthWater.LOGGER.info("Registering Mod Entities for " + EarthWater.MOD_ID);
    }
}