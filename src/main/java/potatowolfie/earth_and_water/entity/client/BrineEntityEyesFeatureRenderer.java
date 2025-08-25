package potatowolfie.earth_and_water.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import potatowolfie.earth_and_water.EarthWater;

@Environment(EnvType.CLIENT)
public class BrineEntityEyesFeatureRenderer extends FeatureRenderer<BrineEntityRenderState, BrineEntityModel> {
    private static final RenderLayer TEXTURE = RenderLayer.getEntityTranslucentEmissiveNoOutline(Identifier.of(EarthWater.MOD_ID, "textures/entity/brine/brine_eyes.png"));

    public BrineEntityEyesFeatureRenderer(FeatureRendererContext<BrineEntityRenderState, BrineEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, BrineEntityRenderState brineEntityRenderState, float f, float g) {
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(TEXTURE);
        BrineEntityModel brineEntityModel = (BrineEntityModel)this.getContextModel();
        BrineEntityRenderer.updatePartVisibility(brineEntityModel, new ModelPart[]{brineEntityModel.getHead(), brineEntityModel.getEyes()}).render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
    }
}