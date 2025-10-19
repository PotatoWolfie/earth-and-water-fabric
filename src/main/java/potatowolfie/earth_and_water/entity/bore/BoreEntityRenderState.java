package potatowolfie.earth_and_water.entity.bore;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.AnimationState;

@Environment(EnvType.CLIENT)
public class BoreEntityRenderState extends LivingEntityRenderState {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState shootingAnimationState = new AnimationState();
    public final AnimationState burrowingAnimationState = new AnimationState();
    public final AnimationState unburrowingAnimationState = new AnimationState();
    public final AnimationState whileburrowingAnimationState = new AnimationState();
    public BoreEntity.BoreVariant variant = BoreEntity.BoreVariant.NORMAL;

    public BoreEntityRenderState() {
    }
}