package potatowolfie.earth_and_water.mixin;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Function;

@Mixin(ShieldEntityModel.class)
public class ShieldEntityModelMixin {

    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/Model;<init>(Lnet/minecraft/client/model/ModelPart;Ljava/util/function/Function;)V"
            ),
            index = 1
    )
    private static Function<Identifier, RenderLayer> useEntityCutoutForSpikedShield(Function<Identifier, RenderLayer> layerFactory) {
        return RenderLayers::entityCutoutNoCull;
    }
}