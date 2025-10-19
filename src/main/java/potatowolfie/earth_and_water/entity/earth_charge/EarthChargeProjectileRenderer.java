package potatowolfie.earth_and_water.entity.earth_charge;

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
public class EarthChargeProjectileRenderer extends EntityRenderer<EarthChargeProjectileEntity, EarthChargeProjectileRenderState> {
    private static final float field_52258 = MathHelper.square(3.5F);
    public static final Identifier TEXTURE = Identifier.of(EarthWater.MOD_ID, "textures/entity/earth_charge/earth_charge.png");
    protected EarthChargeProjectileModel model;

    public EarthChargeProjectileRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        model = new EarthChargeProjectileModel(ctx.getPart(ModEntityModelLayers.EARTH_CHARGE));
    }

    public void render(EarthChargeProjectileRenderState earthChargeProjectileRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        if (earthChargeProjectileRenderState.age >= 2 || earthChargeProjectileRenderState.distanceFromCamera >= field_52258) {
            matrixStack.push();

            matrixStack.translate(0, 1.525, 0);

            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));

            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(earthChargeProjectileRenderState.renderingRotation));

            this.model.setAngles(earthChargeProjectileRenderState);
            orderedRenderCommandQueue.submitModelPart(
                    this.model.getRootPart(),
                    matrixStack,
                    this.model.getLayer(TEXTURE),
                    earthChargeProjectileRenderState.light,
                    OverlayTexture.DEFAULT_UV,
                    null,
                    false,
                    false,
                    -1,
                    null,
                    0
            );

            matrixStack.pop();
            super.render(earthChargeProjectileRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
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
        earthChargeProjectileRenderState.distanceFromCamera = (float) earthChargeProjectileEntity.squaredDistanceTo(
                earthChargeProjectileRenderState.x,
                earthChargeProjectileRenderState.y,
                earthChargeProjectileRenderState.z
        );
    }
}