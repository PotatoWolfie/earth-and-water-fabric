package potatowolfie.earth_and_water.block.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.MobSpawnerBlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import potatowolfie.earth_and_water.block.entity.custom.ReinforcedSpawnerBlockEntity;

@Environment(EnvType.CLIENT)
public class ReinforcedSpawnerBlockEntityRenderer implements BlockEntityRenderer<ReinforcedSpawnerBlockEntity> {
    private final EntityRenderDispatcher entityRenderDispatcher;

    public ReinforcedSpawnerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.entityRenderDispatcher = ctx.getEntityRenderDispatcher();
    }

    @Override
    public void render(ReinforcedSpawnerBlockEntity blockEntity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = blockEntity.getWorld();
        if (world != null) {
            Entity entity = blockEntity.getDisplayEntity(world);
            if (entity != null) {
                float rotation = blockEntity.getDisplayRotation(tickDelta) * 10.0F;
                float scale = 0.53125F;
                float maxDimension = Math.max(entity.getWidth(), entity.getHeight());
                if (maxDimension > 1.0F) {
                    scale /= maxDimension;
                }

                matrices.push();
                matrices.translate(0.5F, 0.0F, 0.5F);
                matrices.translate(0.0F, 0.4F, 0.0F);
                matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
                matrices.translate(0.0F, -0.2F, 0.0F);
                matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_X.rotationDegrees(-30.0F));
                matrices.scale(scale, scale, scale);
                this.entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, tickDelta, matrices, vertexConsumers, light);
                matrices.pop();
            }
        }
    }
}