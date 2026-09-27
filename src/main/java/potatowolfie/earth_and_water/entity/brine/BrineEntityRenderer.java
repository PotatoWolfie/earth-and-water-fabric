package potatowolfie.earth_and_water.entity.brine;

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
public class BrineEntityRenderer extends MobRenderer<BrineEntity, BrineEntityRenderState, BrineEntityModel> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EarthWater.MOD_ID, "textures/entity/brine/brine.png");

    public BrineEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new BrineEntityModel(context.bakeLayer(ModEntityModelLayers.BRINE)), 0.5F);
        this.addLayer(new BrineEntityEyesFeatureRenderer(this));
    }

    @Override
    public void submit(final BrineEntityRenderState state,
                       final PoseStack poseStack,
                       final SubmitNodeCollector submitNodeCollector,
                       final CameraRenderState camera) {

        BrineEntityModel model = (BrineEntityModel) this.getModel();

        updatePartVisibility(
                model,
                model.getHead(),
                model.getRodsTop(),
                model.getRodsBottom()
        );

        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    public BrineEntityRenderState createRenderState() {
        return new BrineEntityRenderState();
    }

    @Override
    public void extractRenderState(final BrineEntity entity,
                                   final BrineEntityRenderState state,
                                   final float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);

        state.idleAnimationState.copyFrom(entity.idleAnimationState);
        state.underwaterAnimationState.copyFrom(entity.underwaterAnimationState);
        state.attackAnimationState.copyFrom(entity.attackAnimationState);
    }

    public static BrineEntityModel updatePartVisibility(BrineEntityModel model, ModelPart... modelParts) {
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
    public Identifier getTextureLocation(BrineEntityRenderState state) {
        return TEXTURE;
    }
}