package potatowolfie.earth_and_water.entity.client;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import potatowolfie.earth_and_water.EarthWaterClient;

import java.util.Objects;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class SpikedShieldModelRenderer implements SpecialModelRenderer<ComponentMap> {
    private final SpikedShieldEntityModel model;

    public SpikedShieldModelRenderer(SpikedShieldEntityModel model) {
        this.model = model;
    }

    @Nullable
    public ComponentMap getData(ItemStack itemStack) {
        return itemStack.getImmutableComponents();
    }

    public void render(@Nullable ComponentMap componentMap, ItemDisplayContext itemDisplayContext, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, boolean bl) {
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

        // Use EntityCutout render layer for proper transparency handling
        VertexConsumer vertexConsumer = spriteIdentifier.getSprite().getTextureSpecificVertexConsumer(
                ItemRenderer.getItemGlintConsumer(
                        vertexConsumerProvider,
                        RenderLayer.getEntityCutout(spriteIdentifier.getAtlasId()),
                        itemDisplayContext == ItemDisplayContext.GUI,
                        bl
                )
        );

        // Render handle first
        this.model.getHandle().render(matrixStack, vertexConsumer, i, j);

        if (bl2) {
            // Render banner patterns only on the main shield plate (not spikes)
            BannerBlockEntityRenderer.renderCanvas(
                    matrixStack,
                    vertexConsumerProvider,
                    i,
                    j,
                    this.model.getPlate(),
                    spriteIdentifier,
                    false,
                    (DyeColor) Objects.requireNonNullElse(dyeColor, DyeColor.WHITE),
                    bannerPatternsComponent,
                    bl,
                    false
            );
        } else {
            // Render the main plate without banner
            this.model.getPlate().render(matrixStack, vertexConsumer, i, j);
        }

        // Always render spikes separately with proper transparency
        // Create a fresh vertex consumer specifically for spikes to avoid buffer issues
        VertexConsumer spikeVertexConsumer = spriteIdentifier.getSprite().getTextureSpecificVertexConsumer(
                vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutout(spriteIdentifier.getAtlasId()))
        );
        this.model.getSpikes().render(matrixStack, spikeVertexConsumer, i, j);

        matrixStack.pop();
    }

    public void collectVertices(Set<Vector3f> vertices) {
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.scale(1.0F, -1.0F, -1.0F);
        this.model.getRootPart().collectVertices(matrixStack, vertices);
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

        public SpecialModelRenderer<?> bake(LoadedEntityModels entityModels) {
            return new SpikedShieldModelRenderer(
                    new SpikedShieldEntityModel(
                            entityModels.getModelPart(EarthWaterClient.SPIKED_SHIELD_MODEL_LAYER)
                    )
            );
        }

        static {
            CODEC = MapCodec.unit(INSTANCE);
        }
    }
}