package potatowolfie.earth_and_water.entity.bore;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.util.Identifier;
import potatowolfie.earth_and_water.EarthWater;

@Environment(EnvType.CLIENT)
public class BoreEntityEyesFeatureRenderer extends EyesFeatureRenderer<BoreEntityRenderState, BoreEntityModel> {
    private static final RenderLayer SKIN = RenderLayers.eyes(Identifier.of(EarthWater.MOD_ID, "textures/entity/bore/bore_eyes.png"));

    public BoreEntityEyesFeatureRenderer(FeatureRendererContext<BoreEntityRenderState, BoreEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public RenderLayer getEyesTexture() {
        return SKIN;
    }
}