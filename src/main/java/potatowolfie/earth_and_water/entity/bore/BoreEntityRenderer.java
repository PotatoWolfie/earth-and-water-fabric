package potatowolfie.earth_and_water.entity.bore;

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
public class BoreEntityRenderer extends MobEntityRenderer<BoreEntity, BoreEntityRenderState, BoreEntityModel> {
    private static final Identifier TEXTURE = Identifier.of(EarthWater.MOD_ID, "textures/entity/bore/bore.png");
    private static final Identifier DARK_TEXTURE = Identifier.of(EarthWater.MOD_ID, "textures/entity/bore/dark_bore.png");

    public BoreEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new BoreEntityModel(context.getPart(ModEntityModelLayers.BORE)), 0.3f);
        this.addFeature(new BoreEntityEyesFeatureRenderer(this));
    }

    public void render(BoreEntityRenderState boreEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue queue, CameraRenderState cameraRenderState) {
        BoreEntityModel boreEntityModel = (BoreEntityModel)this.getModel();
        updatePartVisibility(boreEntityModel, boreEntityModel.getHead(), boreEntityModel.getRodsTop(), boreEntityModel.getRodsBottom());
        super.render(boreEntityRenderState, matrixStack, queue, cameraRenderState);
    }

    public Identifier getTexture(BoreEntityRenderState boreEntityRenderState) {
        return switch (boreEntityRenderState.variant) {
            case NORMAL -> TEXTURE;
            case DARK -> DARK_TEXTURE;
        };
    }

    public BoreEntityRenderState createRenderState() {
        return new BoreEntityRenderState();
    }

    public void updateRenderState(BoreEntity boreEntity, BoreEntityRenderState boreEntityRenderState, float f) {
        super.updateRenderState(boreEntity, boreEntityRenderState, f);
        boreEntityRenderState.idleAnimationState.copyFrom(boreEntity.idleAnimationState);
        boreEntityRenderState.shootingAnimationState.copyFrom(boreEntity.shootingAnimationState);
        boreEntityRenderState.burrowingAnimationState.copyFrom(boreEntity.burrowingAnimationState);
        boreEntityRenderState.unburrowingAnimationState.copyFrom(boreEntity.unburrowingAnimationState);
        boreEntityRenderState.whileburrowingAnimationState.copyFrom(boreEntity.whileburrowingAnimationState);
        boreEntityRenderState.variant = boreEntity.getVariant();
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
}