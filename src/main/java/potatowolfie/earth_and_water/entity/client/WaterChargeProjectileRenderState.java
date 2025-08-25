package potatowolfie.earth_and_water.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.EntityRenderState;

@Environment(EnvType.CLIENT)
public class WaterChargeProjectileRenderState extends EntityRenderState {
    public boolean isStuck;
    public boolean isStuckToEntity;
    public boolean isGrounded;
    public float renderingRotation;
    public float yaw;
    public float pitch;

    public WaterChargeProjectileRenderState() {
    }
}