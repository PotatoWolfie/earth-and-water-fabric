package potatowolfie.earth_and_water.entity.bore;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.util.Identifier;
import potatowolfie.earth_and_water.EarthWater;

@Environment(EnvType.CLIENT)
public class BoreEntityEyesFeatureRenderer<T extends BoreEntity> extends EyesFeatureRenderer<T, BoreEntityModel<T>> {
    private static final RenderLayer SKIN = RenderLayer.getEyes(Identifier.of(EarthWater.MOD_ID, "textures/entity/bore/bore_eyes.png"));

    public BoreEntityEyesFeatureRenderer(FeatureRendererContext<T, BoreEntityModel<T>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public RenderLayer getEyesTexture() {
        return SKIN;
    }
}