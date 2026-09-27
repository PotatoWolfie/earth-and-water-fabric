package potatowolfie.earth_and_water.entity.bore;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.entity.ModEntityModelLayers;

@Environment(EnvType.CLIENT)
public class BoreEntityRenderer extends MobRenderer<BoreEntity, BoreEntityRenderState, BoreEntityModel> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EarthWater.MOD_ID, "textures/entity/bore/bore.png");
    private static final Identifier DARK_TEXTURE = Identifier.fromNamespaceAndPath(EarthWater.MOD_ID, "textures/entity/bore/dark_bore.png");

    public BoreEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new BoreEntityModel(context.bakeLayer(ModEntityModelLayers.BORE)), 0.3f);
        this.addLayer(new BoreEntityEyesFeatureRenderer(this));
    }

    @Override
    public void submit(final BoreEntityRenderState state,
                       final PoseStack poseStack,
                       final SubmitNodeCollector submitNodeCollector,
                       final CameraRenderState camera) {

        BoreEntityModel model = (BoreEntityModel) this.getModel();

        updatePartVisibility(
                model,
                model.getHead(),
                model.getRodsTop(),
                model.getRodsBottom()
        );

        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    public BoreEntityRenderState createRenderState() {
        return new BoreEntityRenderState();
    }

    @Override
    public void extractRenderState(final BoreEntity entity,
                                   final BoreEntityRenderState state,
                                   final float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);

        state.idleAnimationState.copyFrom(entity.idleAnimationState);
        state.shootingAnimationState.copyFrom(entity.shootingAnimationState);
        state.burrowingAnimationState.copyFrom(entity.burrowingAnimationState);
        state.unburrowingAnimationState.copyFrom(entity.unburrowingAnimationState);
        state.whileburrowingAnimationState.copyFrom(entity.whileburrowingAnimationState);

        state.variant = entity.getVariant();
    }

    public static BoreEntityModel updatePartVisibility(BoreEntityModel model, ModelPart... modelParts) {
        model.getHead().visible = false;
        model.getEyes().visible = false;
        model.getRodsTop().visible = false;
        model.getRodsBottom().visible = false;
        ModelPart[] var2 = modelParts;
        int var3 = modelParts.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            ModelPart modelPart = var2[var4];
            modelPart.visible = true;
        }

        return model;
    }

    @Override
    public Identifier getTextureLocation(BoreEntityRenderState state) {
        return switch (state.variant) {
            case NORMAL -> TEXTURE;
            case DARK -> DARK_TEXTURE;
        };
    }
}