package potatowolfie.earth_and_water.entity.water_charge;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.entity.client.ModEntityModelLayers;

@Environment(EnvType.CLIENT)
public class WaterChargeProjectileRenderer extends EntityRenderer<WaterChargeProjectileEntity, WaterChargeProjectileRenderState> {
    private static final float field_52258 = MathHelper.square(3.5F);
    public static final Identifier TEXTURE = Identifier.of(EarthWater.MOD_ID, "textures/entity/water_charge/water_charge.png");
    protected WaterChargeProjectileModel model;

    public WaterChargeProjectileRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        model = new WaterChargeProjectileModel(ctx.getPart(ModEntityModelLayers.WATER_CHARGE));
    }

    public void render(WaterChargeProjectileRenderState waterChargeProjectileRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        if (waterChargeProjectileRenderState.age >= 2 || waterChargeProjectileRenderState.distanceFromCamera >= field_52258) {
            matrixStack.push();

            matrixStack.translate(0, 1.525, 0);

            if (waterChargeProjectileRenderState.isStuck) {
                if (waterChargeProjectileRenderState.isStuckToEntity) {
                    matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(waterChargeProjectileRenderState.renderingRotation));
                } else {
                    matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(waterChargeProjectileRenderState.yaw));
                    matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(waterChargeProjectileRenderState.pitch));
                }
            } else if (waterChargeProjectileRenderState.isGrounded) {
                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(waterChargeProjectileRenderState.yaw));
                matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(waterChargeProjectileRenderState.pitch));
            }

            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));

            if (!waterChargeProjectileRenderState.isStuck) {
                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(waterChargeProjectileRenderState.renderingRotation));
            }

            this.model.setAngles(waterChargeProjectileRenderState);
            orderedRenderCommandQueue.submitModelPart(
                    this.model.getRootPart(),
                    matrixStack,
                    this.model.getLayer(TEXTURE),
                    waterChargeProjectileRenderState.light,
                    OverlayTexture.DEFAULT_UV,
                    null,
                    false,
                    false,
                    -1,
                    null,
                    0
            );

            matrixStack.pop();
            super.render(waterChargeProjectileRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
        }
    }

    public Identifier getTexture(WaterChargeProjectileRenderState waterChargeProjectileRenderState) {
        return TEXTURE;
    }

    public WaterChargeProjectileRenderState createRenderState() {
        return new WaterChargeProjectileRenderState();
    }

    public void updateRenderState(WaterChargeProjectileEntity waterChargeProjectileEntity, WaterChargeProjectileRenderState waterChargeProjectileRenderState, float f) {
        super.updateRenderState(waterChargeProjectileEntity, waterChargeProjectileRenderState, f);
        waterChargeProjectileRenderState.isStuck = waterChargeProjectileEntity.isStuck();
        waterChargeProjectileRenderState.isStuckToEntity = waterChargeProjectileEntity.isStuckToEntity();
        waterChargeProjectileRenderState.isGrounded = waterChargeProjectileEntity.isGrounded();
        waterChargeProjectileRenderState.renderingRotation = waterChargeProjectileEntity.getRenderingRotation();
        waterChargeProjectileRenderState.yaw = waterChargeProjectileEntity.getYaw();
        waterChargeProjectileRenderState.pitch = waterChargeProjectileEntity.getPitch();
        waterChargeProjectileRenderState.distanceFromCamera = (float) waterChargeProjectileEntity.squaredDistanceTo(
                waterChargeProjectileRenderState.x,
                waterChargeProjectileRenderState.y,
                waterChargeProjectileRenderState.z
        );
    }
}