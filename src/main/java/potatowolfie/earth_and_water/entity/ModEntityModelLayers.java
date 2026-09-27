package potatowolfie.earth_and_water.entity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;
import potatowolfie.earth_and_water.EarthWater;

public class ModEntityModelLayers {

    public static final ModelLayerLocation EARTH_CHARGE =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(EarthWater.MOD_ID, "earth_charge"), "main");

    public static final ModelLayerLocation WATER_CHARGE =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(EarthWater.MOD_ID, "water_charge"), "main");

    public static final ModelLayerLocation BORE = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(EarthWater.MOD_ID, "bore"), "main"
    );

    public static final ModelLayerLocation BRINE = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(EarthWater.MOD_ID, "brine"), "main"
    );

    public static final ModelLayerLocation SPIKED_SHIELD = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath("earth-and-water", "spiked_shield"), "main");
}