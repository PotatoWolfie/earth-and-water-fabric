package potatowolfie.earth_and_water.entity.earth_charge;

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
import potatowolfie.earth_and_water.entity.client.ModEntityModelLayers;

public class EarthChargeProjectileRenderer extends EntityRenderer <EarthChargeProjectileEntity> {
    private static final float field_52258 = MathHelper.square(3.5F);
    public static final Identifier TEXTURE = Identifier.of(EarthWater.MOD_ID, "textures/entity/earth_charge/earth_charge.png");
    protected EarthChargeProjectileModel model;

    public EarthChargeProjectileRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        model = new EarthChargeProjectileModel(ctx.getPart(ModEntityModelLayers.EARTH_CHARGE));
    }

    public void render(
            EarthChargeProjectileEntity earthChargeProjectileEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i
    ) {
        if (earthChargeProjectileEntity.age >= 2 || !(this.dispatcher.camera.getFocusedEntity().squaredDistanceTo(earthChargeProjectileEntity) < (double)field_52258)) {
            matrixStack.push();

            matrixStack.translate(0, 1.525, 0);

            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));

            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(earthChargeProjectileEntity.getRenderingRotation()));

            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutout(TEXTURE));
            this.model.setAngles(earthChargeProjectileEntity, 0.0F, 0.0F, 0, 0.0F, 0.0F);
            this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);

            matrixStack.pop();
            super.render(earthChargeProjectileEntity, f, g, matrixStack, vertexConsumerProvider, i);
        }
    }

    protected float getXOffset(float tickDelta) {
        return tickDelta * 0.03F;
    }

    @Override
    public Identifier getTexture(EarthChargeProjectileEntity entity) {
        return TEXTURE;
    }
}