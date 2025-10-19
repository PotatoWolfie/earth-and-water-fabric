package potatowolfie.earth_and_water.entity.brine;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.entity.client.ModEntityModelLayers;

@Environment(EnvType.CLIENT)
public class BrineEntityRenderer extends MobEntityRenderer<BrineEntity, BrineEntityRenderState, BrineEntityModel> {
    private static final Identifier TEXTURE = Identifier.of(EarthWater.MOD_ID, "textures/entity/brine/brine.png");

    public BrineEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new BrineEntityModel(context.getPart(ModEntityModelLayers.BRINE)), 0.5F);
        this.addFeature(new BrineEntityEyesFeatureRenderer(this));
    }

    public void render(BrineEntityRenderState brineEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue queue, CameraRenderState cameraRenderState) {
        BrineEntityModel brineEntityModel = (BrineEntityModel)this.getModel();
        updatePartVisibility(brineEntityModel, brineEntityModel.getHead(), brineEntityModel.getRodsTop(), brineEntityModel.getRodsBottom());
        super.render(brineEntityRenderState, matrixStack, queue, cameraRenderState);
    }

    public Identifier getTexture(BrineEntityRenderState brineEntityRenderState) {
        return TEXTURE;
    }

    public BrineEntityRenderState createRenderState() {
        return new BrineEntityRenderState();
    }

    public void updateRenderState(BrineEntity brineEntity, BrineEntityRenderState brineEntityRenderState, float f) {
        super.updateRenderState(brineEntity, brineEntityRenderState, f);
        brineEntityRenderState.idleAnimationState.copyFrom(brineEntity.idleAnimationState);
        brineEntityRenderState.underwaterAnimationState.copyFrom(brineEntity.underwaterAnimationState);
        brineEntityRenderState.attackAnimationState.copyFrom(brineEntity.attackAnimationState);
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
}