package potatowolfie.earth_and_water.entity.client.spiked_shield;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
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
import potatowolfie.earth_and_water.EarthWaterClient;

import java.util.Objects;
import java.util.Set;

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
                RenderLayer.getEntitySolid(spriteIdentifier.getAtlasId()),
                i,
                j,
                this.spriteHolder.getSprite(spriteIdentifier),
                false,
                false,
                -1,
                null,
                k
        );

        if (bl2) {
            BannerBlockEntityRenderer.renderCanvas(
                    this.spriteHolder,
                    matrixStack,
                    orderedRenderCommandQueue,
                    i,
                    j,
                    this.model,
                    Unit.INSTANCE,
                    spriteIdentifier,
                    false,
                    (DyeColor) Objects.requireNonNullElse(dyeColor, DyeColor.WHITE),
                    bannerPatternsComponent,
                    bl,
                    null,
                    k
            );
        } else {
            orderedRenderCommandQueue.submitModelPart(
                    this.model.getPlate(),
                    matrixStack,
                    this.model.getLayer(spriteIdentifier.getAtlasId()),
                    i,
                    j,
                    this.spriteHolder.getSprite(spriteIdentifier),
                    false,
                    bl,
                    -1,
                    null,
                    k
            );
        }

        orderedRenderCommandQueue.submitModelPart(
                this.model.getSpikes(),
                matrixStack,
                this.model.getLayer(spriteIdentifier.getAtlasId()),
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