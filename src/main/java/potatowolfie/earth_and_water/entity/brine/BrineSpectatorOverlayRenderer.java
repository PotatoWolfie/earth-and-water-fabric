package potatowolfie.earth_and_water.entity.brine;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

@Environment(EnvType.CLIENT)
public class BrineSpectatorOverlayRenderer {

    public static void renderBrineVision(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (!(client.getCameraEntity() instanceof BrineEntity)) {
            return;
        }

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        int greenStartX = (screenWidth * 3) / 4;

        BrineEntity brineEntity = (BrineEntity) client.getCameraEntity();
        float entityYaw = brineEntity.getYaw();

        BrineSpectatorManager manager = BrineSpectatorManager.getInstance();
        manager.setRenderingLeftQuarter(true);
        manager.setCurrentQuarter(0);

        renderLeftSideVision(context, 0, 0, screenWidth / 4, screenHeight, entityYaw);

        renderKelpCoveredVision(context, greenStartX, 0, screenWidth - greenStartX, screenHeight);

        manager.setRenderingLeftQuarter(false);
    }

    private static void renderLeftSideVision(DrawContext context, int x, int y, int width, int height, float yaw) {
    }

    private static void renderKelpCoveredVision(DrawContext context, int x, int y, int width, int height) {
        int fadeWidth = 20;
        int darkGreenColor = 0x2a4221;

        for (int i = 0; i < Math.min(fadeWidth, width); i++) {
            float alpha = (float) i / fadeWidth;
            int alphaInt = (int) (alpha * 255);
            int gradientColor = (alphaInt << 24) | darkGreenColor;
            context.fill(x + i, y, x + i + 1, y + height, gradientColor);
        }

        context.fill(x + fadeWidth, y, x + width, y + height, 0xFF000000 | darkGreenColor);
    }
}