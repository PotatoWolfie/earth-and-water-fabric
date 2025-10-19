package potatowolfie.earth_and_water.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import potatowolfie.earth_and_water.entity.custom.BrineEntity;

@Environment(EnvType.CLIENT)
public class BrineSpectatorOverlayRenderer {

    public static void renderBrineVision(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (!(client.getCameraEntity() instanceof BrineEntity)) {
            return;
        }

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        // Simpler approach: just use 3/4 of screen for the start of the green area
        int greenStartX = (screenWidth * 3) / 4;

        BrineEntity brineEntity = (BrineEntity) client.getCameraEntity();
        float entityYaw = brineEntity.getYaw();

        // Get the manager and set which quarter we're rendering
        BrineSpectatorManager manager = BrineSpectatorManager.getInstance();
        manager.setRenderingLeftQuarter(true);
        manager.setCurrentQuarter(0);

        // Render left side vision (first quarter)
        renderLeftSideVision(context, 0, 0, screenWidth / 4, screenHeight, entityYaw);

        // Render kelp vision from 3/4 screen to the end
        renderKelpCoveredVision(context, greenStartX, 0, screenWidth - greenStartX, screenHeight);

        manager.setRenderingLeftQuarter(false);
    }

    private static void renderLeftSideVision(DrawContext context, int x, int y, int width, int height, float yaw) {
        // Add some visual indication that this is the left side vision
    }

    private static void renderKelpCoveredVision(DrawContext context, int x, int y, int width, int height) {
        int fadeWidth = 20;
        int darkGreenColor = 0x2a4221;

        // Render the fade-in gradient
        for (int i = 0; i < Math.min(fadeWidth, width); i++) {
            float alpha = (float) i / fadeWidth;
            int alphaInt = (int) (alpha * 255);
            int gradientColor = (alphaInt << 24) | darkGreenColor;
            context.fill(x + i, y, x + i + 1, y + height, gradientColor);
        }

        // Fill the rest with solid green - this will automatically go to the screen edge
        context.fill(x + fadeWidth, y, x + width, y + height, 0xFF000000 | darkGreenColor);
    }
}