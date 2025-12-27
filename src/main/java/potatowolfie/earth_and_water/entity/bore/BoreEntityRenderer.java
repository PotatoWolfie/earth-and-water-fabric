package potatowolfie.earth_and_water.entity.bore;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;
import potatowolfie.earth_and_water.EarthWater;
import potatowolfie.earth_and_water.entity.client.ModEntityModelLayers;

@Environment(EnvType.CLIENT)
public class BoreEntityRenderer extends MobEntityRenderer<BoreEntity, BoreEntityModel<BoreEntity>> {
    private static final Identifier TEXTURE = Identifier.of(EarthWater.MOD_ID, "textures/entity/bore/bore.png");
    private static final Identifier DARK_TEXTURE = Identifier.of(EarthWater.MOD_ID, "textures/entity/bore/dark_bore.png");

    public BoreEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new BoreEntityModel<>(context.getPart(ModEntityModelLayers.BORE)), 0.3f);
        this.addFeature(new BoreEntityEyesFeatureRenderer<>(this));
    }

    @Override
    public Identifier getTexture(BoreEntity entity) {
        return switch (entity.getVariant()) {
            case NORMAL -> TEXTURE;
            case DARK -> DARK_TEXTURE;
        };
    }

    public static BoreEntityModel<?> updatePartVisibility(BoreEntityModel<?> model, ModelPart... modelParts) {
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