package potatowolfie.earth_and_water.entity.water_charge;

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

public class WaterChargeProjectileRenderer extends EntityRenderer <WaterChargeProjectileEntity> {
    private static final float field_52258 = MathHelper.square(3.5F);
    public static final Identifier TEXTURE = Identifier.of(EarthWater.MOD_ID, "textures/entity/water_charge/water_charge.png");
    protected WaterChargeProjectileModel model;

    public WaterChargeProjectileRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        model = new WaterChargeProjectileModel(ctx.getPart(ModEntityModelLayers.WATER_CHARGE));
    }

    public void render(
            WaterChargeProjectileEntity waterChargeProjectileEntity, float f, float g, MatrixStack matrixStack,
            VertexConsumerProvider vertexConsumerProvider, int i) {
        if (waterChargeProjectileEntity.age >= 2 || !(this.dispatcher.camera.getFocusedEntity().squaredDistanceTo(waterChargeProjectileEntity) < (double) field_52258)) {
            matrixStack.push();

            matrixStack.translate(0, 1.525, 0);

            if (waterChargeProjectileEntity.isStuck()) {
                if (waterChargeProjectileEntity.isStuckToEntity()) {
                    matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(waterChargeProjectileEntity.getRenderingRotation()));
                } else {
                    matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(waterChargeProjectileEntity.getYaw()));
                    matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(waterChargeProjectileEntity.getPitch()));
                }
            } else if (waterChargeProjectileEntity.isGrounded()) {
                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(waterChargeProjectileEntity.getYaw()));
                matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(waterChargeProjectileEntity.getPitch()));
            }

            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));

            if (!waterChargeProjectileEntity.isStuck()) {
                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(waterChargeProjectileEntity.getRenderingRotation()));
            }

            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutout(TEXTURE));
            this.model.setAngles(waterChargeProjectileEntity, 0.0F, 0.0F, 0, 0.0F, 0.0F);
            this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);

            matrixStack.pop();
            super.render(waterChargeProjectileEntity, f, g, matrixStack, vertexConsumerProvider, i);
        }
    }

    @Override
    public Identifier getTexture(WaterChargeProjectileEntity entity) {
        return TEXTURE;
    }
}