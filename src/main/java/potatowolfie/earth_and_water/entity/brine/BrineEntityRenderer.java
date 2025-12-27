package potatowolfie.earth_and_water.entity.brine;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.entity.client.ModEntityModelLayers;

@Environment(EnvType.CLIENT)
public class BrineEntityRenderer extends MobEntityRenderer<BrineEntity, BrineEntityModel<BrineEntity>> {
    private static final Identifier TEXTURE = Identifier.of(EarthWater.MOD_ID, "textures/entity/brine/brine.png");

    public BrineEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new BrineEntityModel<>(context.getPart(ModEntityModelLayers.BRINE)), 0.5F);
        this.addFeature(new BrineEntityEyesFeatureRenderer<>(this));
    }

    @Override
    public Identifier getTexture(BrineEntity entity) {
        return TEXTURE;
    }

    public static BrineEntityModel<?> updatePartVisibility(BrineEntityModel<?> model, ModelPart... modelParts) {
        model.getHead().visible = false;
        model.getEyes().visible = false;
        model.getRodsTop().visible = false;
        model.getRodsBottom().visible = false;

        for (ModelPart modelPart : modelParts) {
            modelPart.visible = true;
        }

        return model;
    }
}