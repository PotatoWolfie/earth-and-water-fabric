package potatowolfie.earth_and_water.entity.brine;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;

public class BrineSpectatorCameraEntity extends Entity {
    private final BrineEntity parentBrine;
    private int activeQuarter = 0;

    public BrineSpectatorCameraEntity(EntityType<?> type, World world, BrineEntity parent) {
        super(type, world);
        this.parentBrine = parent;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    public void tick() {
        super.tick();

        if (parentBrine != null && !parentBrine.isRemoved()) {
            this.setPosition(parentBrine.getX(), parentBrine.getEyeY(), parentBrine.getZ());

            switch (activeQuarter) {
                case 0:
                    this.setYaw(parentBrine.getYaw() - 90.0F);
                    break;
                case 1, 2:
                    this.setYaw(parentBrine.getYaw());
                    break;
                case 3:
                    this.setYaw(parentBrine.getYaw());
                    break;
            }
        }
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void readCustomData(ReadView view) {

    }

    @Override
    protected void writeCustomData(WriteView view) {

    }

    public void setActiveQuarter(int quarter) {
        this.activeQuarter = Math.max(0, Math.min(3, quarter));
    }

    public int getActiveQuarter() {
        return activeQuarter;
    }
}