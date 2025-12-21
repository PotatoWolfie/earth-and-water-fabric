package potatowolfie.earth_and_water.entity.client.spiked_shield;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import potatowolfie.earth_and_water.EarthWaterClient;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class SpikedShieldModelRenderer implements SpecialModelRenderer<ComponentMap> {
    private final SpriteHolder spriteHolder;
    private final SpikedShieldEntityModel model;

    public SpikedShieldModelRenderer(SpriteHolder spriteHolder, SpikedShieldEntityModel model) {
        this.spriteHolder = spriteHolder;
        this.model = model;
    }

    @Nullable
    public ComponentMap getData(ItemStack itemStack) {
        return itemStack.getImmutableComponents();
    }

    public void render(@Nullable ComponentMap componentMap, ItemDisplayContext itemDisplayContext, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, int j, boolean bl, int k) {
        BannerPatternsComponent bannerPatternsComponent = componentMap != null ?
                (BannerPatternsComponent)componentMap.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT) :
                BannerPatternsComponent.DEFAULT;
        DyeColor dyeColor = componentMap != null ? (DyeColor)componentMap.get(DataComponentTypes.BASE_COLOR) : null;
        boolean bl2 = !bannerPatternsComponent.layers().isEmpty() || dyeColor != null;

        matrixStack.push();
        matrixStack.scale(1.0F, -1.0F, -1.0F);

        SpriteIdentifier spriteIdentifier = bl2 ?
                EarthWaterClient.SPIKED_SHIELD_BASE :
                EarthWaterClient.SPIKED_SHIELD_BASE_NO_PATTERN;

        orderedRenderCommandQueue.submitModelPart(
                this.model.getHandle(),
                matrixStack,
                RenderLayers.entityCutoutNoCull(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE),
                i,
                j,
                this.spriteHolder.getSprite(spriteIdentifier),
                false,
                false,
                -1,
                null,
                k
        );

        orderedRenderCommandQueue.submitModelPart(
                this.model.getPlate(),
                matrixStack,
                RenderLayers.entityCutoutNoCull(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE),
                i,
                j,
                this.spriteHolder.getSprite(spriteIdentifier),
                false,
                bl,
                -1,
                null,
                k
        );

        if (bl2) {
            SpriteIdentifier baseLayerSprite = TexturedRenderLayers.SHIELD_BASE;
            DyeColor baseColor = Objects.requireNonNullElse(dyeColor, DyeColor.WHITE);

            orderedRenderCommandQueue.submitModelPart(
                    this.model.getPlate(),
                    matrixStack,
                    RenderLayers.entityCutoutNoCull(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE),
                    i,
                    j,
                    this.spriteHolder.getSprite(baseLayerSprite),
                    false,
                    false,
                    baseColor.getEntityColor(),
                    null,
                    0
            );

            for (int layerIndex = 0; layerIndex < 16 && layerIndex < bannerPatternsComponent.layers().size(); ++layerIndex) {
                BannerPatternsComponent.Layer layer = bannerPatternsComponent.layers().get(layerIndex);
                SpriteIdentifier patternSprite = TexturedRenderLayers.getShieldPatternTextureId(layer.pattern());

                orderedRenderCommandQueue.submitModelPart(
                        this.model.getPlate(),
                        matrixStack,
                        RenderLayers.entityCutoutNoCull(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE),
                        i,
                        j,
                        this.spriteHolder.getSprite(patternSprite),
                        false,
                        false,
                        layer.color().getEntityColor(),
                        null,
                        0
                );
            }

            if (bl) {
                orderedRenderCommandQueue.submitModelPart(
                        this.model.getPlate(),
                        matrixStack,
                        RenderLayers.entityGlint(),
                        i,
                        j,
                        this.spriteHolder.getSprite(spriteIdentifier),
                        false,
                        false,
                        -1,
                        null,
                        0
                );
            }
        }

        orderedRenderCommandQueue.submitModelPart(
                this.model.getSpikes(),
                matrixStack,
                RenderLayers.entityCutoutNoCull(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE),
                i,
                j,
                this.spriteHolder.getSprite(spriteIdentifier),
                false,
                false,
                -1,
                null,
                k
        );

        matrixStack.pop();
    }

    @Override
    public void collectVertices(Consumer<Vector3fc> consumer) {
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.scale(1.0F, -1.0F, -1.0F);
        this.model.getRootPart().collectVertices(matrixStack, consumer);
    }

    @Environment(EnvType.CLIENT)
    public static record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final SpikedShieldModelRenderer.Unbaked INSTANCE = new SpikedShieldModelRenderer.Unbaked();
        public static final MapCodec<SpikedShieldModelRenderer.Unbaked> CODEC;

        public Unbaked() {
        }

        public MapCodec<SpikedShieldModelRenderer.Unbaked> getCodec() {
            return CODEC;
        }

        public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakeContext context) {
            return new SpikedShieldModelRenderer(
                    context.spriteHolder(),
                    new SpikedShieldEntityModel(
                            context.entityModelSet().getModelPart(EarthWaterClient.SPIKED_SHIELD_MODEL_LAYER)
                    )
            );
        }

        static {
            CODEC = MapCodec.unit(INSTANCE);
        }
    }
}