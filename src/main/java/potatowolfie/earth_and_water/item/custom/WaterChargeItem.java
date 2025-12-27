package potatowolfie.earth_and_water.item.custom;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import potatowolfie.earth_and_water.entity.water_charge.WaterChargeProjectileEntity;

public class WaterChargeItem extends Item implements ProjectileItem {
    private static final int COOLDOWN = 100;

    public WaterChargeItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            WaterChargeProjectileEntity waterChargeProjectileEntity = new WaterChargeProjectileEntity(world, user);

            Vec3d lookVec = user.getRotationVec(1.0F);

            float speed = 2.0F;
            waterChargeProjectileEntity.setVelocity(
                    lookVec.x * speed,
                    lookVec.y * speed,
                    lookVec.z * speed
            );

            waterChargeProjectileEntity.setNoGravity(false);

            world.spawnEntity(waterChargeProjectileEntity);

            world.playSound(
                    null,
                    user.getX(),
                    user.getY(),
                    user.getZ(),
                    SoundEvents.ENTITY_WIND_CHARGE_THROW,
                    SoundCategory.NEUTRAL,
                    0.5F,
                    0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
            );
        }

        ItemStack itemStack = user.getStackInHand(hand);
        user.getItemCooldownManager().set(this, COOLDOWN);
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        itemStack.decrementUnlessCreative(1, user);
        return TypedActionResult.success(itemStack, world.isClient());
    }

    @Override
    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {

        Vec3d dirVector = new Vec3d(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
        WaterChargeProjectileEntity waterChargeProjectileEntity = new WaterChargeProjectileEntity(
                world, pos.getX(), pos.getY(), pos.getZ(), dirVector);

        float speed = 1.5F;
        waterChargeProjectileEntity.setVelocity(
                direction.getOffsetX() * speed,
                direction.getOffsetY() * speed,
                direction.getOffsetZ() * speed
        );

        waterChargeProjectileEntity.setNoGravity(false);

        return waterChargeProjectileEntity;
    }

    @Override
    public void initializeProjectile(ProjectileEntity entity, double x, double y, double z, float power, float uncertainty) {
        entity.setVelocity(x, y, z, power, 0.1F);

        entity.setNoGravity(false);
    }

    @Override
    public ProjectileItem.Settings getProjectileSettings() {
        return ProjectileItem.Settings.builder()
                .positionFunction((pointer, facing) -> DispenserBlock.getOutputLocation(pointer, 1.0, Vec3d.ZERO))
                .uncertainty(0.1F)
                .power(1.5F)
                .overrideDispenseEvent(1051)
                .build();
    }
}