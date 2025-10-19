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
public class BoreEntityEyesFeatureRenderer extends FeatureRenderer<BoreEntityRenderState, BoreEntityModel> {
    private static final RenderLayer TEXTURE = RenderLayer.getEntityTranslucentEmissiveNoOutline(
            Identifier.of(EarthWater.MOD_ID, "textures/entity/bore/bore_eyes.png")
    );

    public BoreEntityEyesFeatureRenderer(FeatureRendererContext<BoreEntityRenderState, BoreEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, BoreEntityRenderState boreEntityRenderState, float f, float g) {
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(TEXTURE);
        BoreEntityModel boreEntityModel = (BoreEntityModel)this.getContextModel();
        BoreEntityRenderer.updatePartVisibility(boreEntityModel, new ModelPart[]{boreEntityModel.getHead(), boreEntityModel.getEyes()}).render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
    }
}