package potatowolfie.earth_and_water.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import potatowolfie.earth_and_water.entity.custom.BrineEntity;

@Mixin(GameRenderer.class)
public class BrineCameraMixin {

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void modifyFovForBrineSpectator(Camera camera, float tickProgress, boolean changingFov, CallbackInfoReturnable<Float> cir) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.getCameraEntity() instanceof BrineEntity) {
            float originalFov = cir.getReturnValue();
            cir.setReturnValue(originalFov * 2f);

        }
    }
}