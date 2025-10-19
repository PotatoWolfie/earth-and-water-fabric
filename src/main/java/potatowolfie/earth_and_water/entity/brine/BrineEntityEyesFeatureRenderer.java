package potatowolfie.earth_and_water.entity.brine;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.util.Identifier;
import potatowolfie.earth_and_water.EarthWater;

@Environment(EnvType.CLIENT)
public class BrineEntityEyesFeatureRenderer extends EyesFeatureRenderer<BrineEntityRenderState, BrineEntityModel> {
    private static final RenderLayer SKIN = RenderLayer.getEyes(Identifier.of(EarthWater.MOD_ID, "textures/entity/brine/brine_eyes.png"));

    public BrineEntityEyesFeatureRenderer(FeatureRendererContext<BrineEntityRenderState, BrineEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public RenderLayer getEyesTexture() {
        return SKIN;
    }
}