package potatowolfie.earth_and_water.block.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.MobSpawnerBlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import potatowolfie.earth_and_water.block.entity.custom.ReinforcedSpawnerBlockEntity;

@Environment(EnvType.CLIENT)
public class ReinforcedSpawnerBlockEntityRenderer implements BlockEntityRenderer<ReinforcedSpawnerBlockEntity> {
    private final EntityRenderManager entityRenderDispatcher;

    public ReinforcedSpawnerBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.entityRenderDispatcher = context.getEntityRenderDispatcher();
    }

    @Override
    public void render(ReinforcedSpawnerBlockEntity reinforcedSpawnerBlockEntity, float f, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumerProvider, int i, int j, Vec3d vec3d) {
        World world = reinforcedSpawnerBlockEntity.getWorld();
        if (world != null) {
            Entity entity = reinforcedSpawnerBlockEntity.getDisplayEntity(world);
            if (entity != null) {
                double lastRotation = reinforcedSpawnerBlockEntity.getLastRotation();
                double rotation = reinforcedSpawnerBlockEntity.getRotation();

                MobSpawnerBlockEntityRenderer.renderDisplayEntity(f, matrixStack, vertexConsumerProvider, i, entity,
                        this.entityRenderDispatcher, lastRotation, rotation);
            }
        }
    }
}