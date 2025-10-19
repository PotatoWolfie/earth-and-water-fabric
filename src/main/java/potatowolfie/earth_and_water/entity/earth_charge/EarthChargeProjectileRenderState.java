package potatowolfie.earth_and_water.entity.earth_charge;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.EntityRenderState;

@Environment(EnvType.CLIENT)
public class EarthChargeProjectileRenderState extends EntityRenderState {
    public float renderingRotation;
    public float distanceFromCamera;

    public EarthChargeProjectileRenderState() {
    }
}