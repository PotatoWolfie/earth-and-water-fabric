package potatowolfie.earth_and_water.block.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.MobSpawnerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.TrialSpawnerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.MobSpawnerBlockEntityRenderState;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import potatowolfie.earth_and_water.block.entity.custom.ReinforcedSpawnerBlockEntity;

@Environment(EnvType.CLIENT)
public class ReinforcedSpawnerBlockEntityRenderer implements BlockEntityRenderer<ReinforcedSpawnerBlockEntity, ReinforcedSpawnerBlockEntityRenderer.SpawnerRenderState> {
    private final EntityRenderManager entityRenderDispatcher;

    public ReinforcedSpawnerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.entityRenderDispatcher = ctx.entityRenderDispatcher();
    }

    @Override
    public SpawnerRenderState createRenderState() {
        return new SpawnerRenderState();
    }

    @Override
    public void updateRenderState(ReinforcedSpawnerBlockEntity blockEntity, SpawnerRenderState state, float tickProgress, Vec3d cameraPos, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        BlockEntityRenderState.updateBlockEntityRenderState(blockEntity, state, crumblingOverlay);

        World world = blockEntity.getWorld();
        if (world != null) {
            Entity entity = blockEntity.getDisplayEntity(world);

            if (entity != null) {
                state.mobSpawnerRenderState.displayEntityRenderState = entityRenderDispatcher.getAndUpdateRenderState(entity, tickProgress);
                state.mobSpawnerRenderState.displayEntityRenderState.light = state.lightmapCoordinates;

                double lastRotation = blockEntity.getLastRotation();
                double rotation = blockEntity.getRotation();
                state.mobSpawnerRenderState.displayEntityRotation = (float)net.minecraft.util.math.MathHelper.lerp(tickProgress, lastRotation, rotation) * 10.0F;

                state.mobSpawnerRenderState.displayEntityScale = 0.53125F;
                float maxDimension = Math.max(entity.getWidth(), entity.getHeight());
                if (maxDimension > 1.0) {
                    state.mobSpawnerRenderState.displayEntityScale /= maxDimension;
                }
            } else {
                state.mobSpawnerRenderState.displayEntityRenderState = null;
            }
        }
    }

    @Override
    public void render(SpawnerRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        if (state.mobSpawnerRenderState.displayEntityRenderState != null) {
            MobSpawnerBlockEntityRenderer.renderDisplayEntity(
                    matrices,
                    queue,
                    state.mobSpawnerRenderState.displayEntityRenderState,
                    this.entityRenderDispatcher,
                    state.mobSpawnerRenderState.displayEntityRotation,
                    state.mobSpawnerRenderState.displayEntityScale,
                    cameraState
            );
        }
    }

    public static class SpawnerRenderState extends BlockEntityRenderState {
        public final MobSpawnerBlockEntityRenderState mobSpawnerRenderState = new MobSpawnerBlockEntityRenderState();
    }
}