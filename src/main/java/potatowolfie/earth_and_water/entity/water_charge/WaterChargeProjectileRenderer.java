package potatowolfie.earth_and_water.entity.water_charge;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.entity.ModEntityModelLayers;

@Environment(EnvType.CLIENT)
public class WaterChargeProjectileRenderer extends EntityRenderer<WaterChargeProjectileEntity, WaterChargeProjectileRenderState> {
    private static final float field_52258 = Mth.square(3.5F);
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EarthWater.MOD_ID, "textures/entity/water_charge/water_charge.png");
    protected WaterChargeProjectileModel model;

    public WaterChargeProjectileRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        model = new WaterChargeProjectileModel(ctx.bakeLayer(ModEntityModelLayers.WATER_CHARGE));
    }

    @Override
    public void submit(final WaterChargeProjectileRenderState state,
                       final PoseStack poseStack,
                       final SubmitNodeCollector submitNodeCollector,
                       final CameraRenderState camera) {

        if (state.ageInTicks >= 2 || state.distanceFromCamera >= field_52258) {

            poseStack.pushPose();

            poseStack.translate(0.0, 1.525, 0.0);

            if (state.isStuck) {

                if (state.isStuckToEntity) {
                    poseStack.mulPose(new Matrix4f().rotation(Axis.YP.rotationDegrees(state.renderingRotation)));
                } else {
                    poseStack.mulPose(new Matrix4f().rotation(Axis.YP.rotationDegrees(state.yaw)));
                    poseStack.mulPose(new Matrix4f().rotation(Axis.XP.rotationDegrees(state.pitch)));
                }

            } else if (state.isGrounded) {
                poseStack.mulPose(new Matrix4f().rotation(Axis.YP.rotationDegrees(state.yaw)));
                poseStack.mulPose(new Matrix4f().rotation(Axis.XP.rotationDegrees(state.pitch)));
            }
            poseStack.mulPose(new Matrix4f().rotation(Axis.XP.rotationDegrees(180.0F)));

            if (!state.isStuck) {
                poseStack.mulPose(new Matrix4f().rotation(Axis.YP.rotationDegrees(state.renderingRotation)));
            }

            this.model.setAngles(state);

            submitNodeCollector.submitModelPart(
                    this.model.root(),
                    poseStack,
                    this.model.renderType(TEXTURE),
                    state.lightCoords,
                    OverlayTexture.NO_OVERLAY,
                    null
            );

            poseStack.popPose();

            super.submit(state, poseStack, submitNodeCollector, camera);
        }
    }

    public Identifier getTexture(WaterChargeProjectileRenderState waterChargeProjectileRenderState) {
        return TEXTURE;
    }

    public WaterChargeProjectileRenderState createRenderState() {
        return new WaterChargeProjectileRenderState();
    }

    @Override
    public void extractRenderState(final WaterChargeProjectileEntity entity,
                                   final WaterChargeProjectileRenderState state,
                                   final float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);

        state.isStuck = entity.isStuck();
        state.isStuckToEntity = entity.isStuckToEntity();
        state.isGrounded = entity.isGrounded();

        state.renderingRotation = entity.getRenderingRotation();
        state.yaw = entity.getYRot();
        state.pitch = entity.getXRot();

        state.distanceFromCamera = (float) entity.distanceToSqr(
                state.x,
                state.y,
                state.z
        );
    }
}