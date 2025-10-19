package potatowolfie.earth_and_water.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BrineSpectatorManager {
    private static BrineSpectatorManager instance = new BrineSpectatorManager();

    private boolean renderingLeftQuarter = false;
    private int currentQuarter = 0;

    private BrineSpectatorManager() {}

    public static BrineSpectatorManager getInstance() {
        return instance;
    }

    public boolean isRenderingLeftQuarter() {
        return renderingLeftQuarter;
    }

    public void setRenderingLeftQuarter(boolean rendering) {
        this.renderingLeftQuarter = rendering;
    }

    public int getCurrentQuarter() {
        return currentQuarter;
    }

    public void setCurrentQuarter(int quarter) {
        this.currentQuarter = quarter;
    }
}
