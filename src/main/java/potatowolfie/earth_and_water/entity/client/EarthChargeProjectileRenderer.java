package potatowolfie.earth_and_water.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.entity.custom.EarthChargeProjectileEntity;

@Environment(EnvType.CLIENT)
public class EarthChargeProjectileRenderer extends EntityRenderer<EarthChargeProjectileEntity, EarthChargeProjectileRenderState> {
    private static final float field_52258 = MathHelper.square(3.5F);
    public static final Identifier TEXTURE = Identifier.of(EarthWater.MOD_ID, "textures/entity/earth_charge/earth_charge.png");
    protected EarthChargeProjectileModel model;

    public EarthChargeProjectileRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        model = new EarthChargeProjectileModel(ctx.getPart(ModEntityModelLayers.EARTH_CHARGE));
    }

    public void render(EarthChargeProjectileRenderState earthChargeProjectileRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if (earthChargeProjectileRenderState.age >= 2 || !(this.dispatcher.camera.getFocusedEntity().squaredDistanceTo(earthChargeProjectileRenderState.x, earthChargeProjectileRenderState.y, earthChargeProjectileRenderState.z) < (double) field_52258)) {
            matrixStack.push();

            matrixStack.translate(0, 1.525, 0);

            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));

            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(earthChargeProjectileRenderState.renderingRotation));

            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutout(TEXTURE));
            this.model.setAngles(earthChargeProjectileRenderState);
            this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);

            matrixStack.pop();
            super.render(earthChargeProjectileRenderState, matrixStack, vertexConsumerProvider, i);
        }
    }

    public Identifier getTexture(EarthChargeProjectileRenderState earthChargeProjectileRenderState) {
        return TEXTURE;
    }

    public EarthChargeProjectileRenderState createRenderState() {
        return new EarthChargeProjectileRenderState();
    }

    public void updateRenderState(EarthChargeProjectileEntity earthChargeProjectileEntity, EarthChargeProjectileRenderState earthChargeProjectileRenderState, float f) {
        super.updateRenderState(earthChargeProjectileEntity, earthChargeProjectileRenderState, f);
        earthChargeProjectileRenderState.renderingRotation = earthChargeProjectileEntity.getRenderingRotation();
    }
}