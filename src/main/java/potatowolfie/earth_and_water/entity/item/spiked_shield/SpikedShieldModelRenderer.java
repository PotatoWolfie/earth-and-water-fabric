package potatowolfie.earth_and_water.entity.item.spiked_shield;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;
import potatowolfie.earth_and_water.EarthWaterClient;

import java.util.Objects;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class SpikedShieldModelRenderer implements SpecialModelRenderer<DataComponentMap> {
    private final SpriteGetter spriteHolder;
    private final SpikedShieldEntityModel model;

    public SpikedShieldModelRenderer(SpriteGetter spriteHolder, SpikedShieldEntityModel model) {
        this.spriteHolder = spriteHolder;
        this.model = model;
    }

    @Nullable
    public DataComponentMap extractArgument(ItemStack itemStack) {
        return itemStack.immutableComponents();
    }

    @Override
    public void submit(@org.jspecify.annotations.Nullable DataComponentMap argument, PoseStack poseStack,
                       SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {

        BannerPatternLayers bannerPatternsComponent = argument != null ?
                (BannerPatternLayers)argument.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY) :
                BannerPatternLayers.EMPTY;
        DyeColor dyeColor = argument != null ? (DyeColor)argument.get(DataComponents.BASE_COLOR) : null;
        boolean bl2 = !bannerPatternsComponent.layers().isEmpty() || dyeColor != null;

        poseStack.pushPose();
        poseStack.scale(1.0F, -1.0F, -1.0F);

        SpriteId spriteIdentifier = bl2 ?
                EarthWaterClient.SPIKED_SHIELD_BASE :
                EarthWaterClient.SPIKED_SHIELD_BASE_NO_PATTERN;

        submitNodeCollector.submitModelPart(
                this.model.handle(),
                poseStack,
                RenderTypes.armorCutoutNoCull(Sheets.SHIELD_SHEET),
                lightCoords,
                overlayCoords,
                this.spriteHolder.get(spriteIdentifier)
        );

        submitNodeCollector.submitModelPart(
                this.model.plate(),
                poseStack,
                RenderTypes.armorCutoutNoCull(Sheets.SHIELD_SHEET),
                lightCoords,
                overlayCoords,
                this.spriteHolder.get(EarthWaterClient.SPIKED_SHIELD_BASE_NO_PATTERN)
        );

        if (bl2) {
            SpriteId baseLayerSprite = EarthWaterClient.SPIKED_SHIELD_BASE;
            DyeColor baseColor = Objects.requireNonNullElse(dyeColor, DyeColor.WHITE);

            submitNodeCollector.order(1).submitModelPart(
                    this.model.plate(),
                    poseStack,
                    RenderTypes.armorCutoutNoCull(Sheets.SHIELD_SHEET),
                    lightCoords,
                    overlayCoords,
                    this.spriteHolder.get(baseLayerSprite),
                    baseColor.getTextureDiffuseColor()
            );

            for (int layerIndex = 0; layerIndex < 16 && layerIndex < bannerPatternsComponent.layers().size(); ++layerIndex) {
                BannerPatternLayers.Layer layer = bannerPatternsComponent.layers().get(layerIndex);
                SpriteId patternSprite = Sheets.getShieldSprite(layer.pattern());

                submitNodeCollector.order(layerIndex + 2).submitModelPart(
                        this.model.plate(),
                        poseStack,
                        RenderTypes.armorCutoutNoCull(Sheets.SHIELD_SHEET),
                        lightCoords,
                        overlayCoords,
                        this.spriteHolder.get(patternSprite),
                        layer.color().getTextureDiffuseColor()
                );
            }

            if (hasFoil) {
                submitNodeCollector.submitModelPart(
                        this.model.plate(),
                        poseStack,
                        RenderTypes.patternedShieldGlint(),
                        lightCoords,
                        overlayCoords,
                        this.spriteHolder.get(spriteIdentifier)
                );
            }
        }

        submitNodeCollector.submitModelPart(
                this.model.getSpikes(),
                poseStack,
                RenderTypes.armorCutoutNoCull(Sheets.SHIELD_SHEET),
                lightCoords,
                overlayCoords,
                this.spriteHolder.get(spriteIdentifier)
        );

        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> consumer) {
        PoseStack poseStack = new PoseStack();
        poseStack.scale(1.0F, -1.0F, -1.0F);
        this.model.root().getExtentsForGui(poseStack, consumer);
    }

    @Environment(EnvType.CLIENT)
    public static record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final Unbaked INSTANCE = new Unbaked();
        public static final MapCodec<Unbaked> CODEC;

        public Unbaked() {
        }

        public MapCodec<Unbaked> type() {
            return CODEC;
        }

        public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakingContext context) {
            return new SpikedShieldModelRenderer(
                    context.sprites(),
                    new SpikedShieldEntityModel(
                            context.entityModelSet().bakeLayer(EarthWaterClient.SPIKED_SHIELD_MODEL_LAYER)
                    )
            );
        }

        static {
            CODEC = MapCodec.unit(INSTANCE);
        }
    }
}