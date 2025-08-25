package potatowolfie.earth_and_water.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.AnimationState;

@Environment(EnvType.CLIENT)
public class BrineEntityRenderState extends LivingEntityRenderState {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState underwaterAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();

    public BrineEntityRenderState() {
    }
}