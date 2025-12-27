package potatowolfie.earth_and_water.entity.client.spiked_shield;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class SpikedShieldItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    private final SpikedShieldRenderer renderer;

    public SpikedShieldItemRenderer(SpikedShieldRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        this.renderer.render(stack, mode, matrices, vertexConsumers, light, overlay);
    }
}