package potatowolfie.earth_and_water.entity.client;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import potatowolfie.earth_and_water.EarthWater;

public class ModEntityModelLayers {

    public static final EntityModelLayer EARTH_CHARGE =
            new EntityModelLayer(Identifier.of(EarthWater.MOD_ID, "earth_charge"), "main");

    public static final EntityModelLayer WATER_CHARGE =
            new EntityModelLayer(Identifier.of(EarthWater.MOD_ID, "water_charge"), "main");

    public static final EntityModelLayer BORE = new EntityModelLayer(
            Identifier.of(EarthWater.MOD_ID, "bore"), "main"
    );

    public static final EntityModelLayer BRINE = new EntityModelLayer(
            Identifier.of(EarthWater.MOD_ID, "brine"), "main"
    );

    public static final EntityModelLayer SPIKED_SHIELD = new EntityModelLayer(
            Identifier.of("earth-and-water", "spiked_shield"), "main");
}