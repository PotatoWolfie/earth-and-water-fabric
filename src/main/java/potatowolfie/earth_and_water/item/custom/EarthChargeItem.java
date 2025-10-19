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
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import potatowolfie.earth_and_water.entity.earth_charge.EarthChargeProjectileEntity;

public class EarthChargeItem extends Item implements ProjectileItem {
    private static final int COOLDOWN = 100;

    public EarthChargeItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            EarthChargeProjectileEntity earthChargeProjectileEntity = new EarthChargeProjectileEntity(world, user);

            Vec3d lookVec = user.getRotationVec(1.0F);

            float speed = 1.6F;
            earthChargeProjectileEntity.setVelocity(
                    lookVec.x * speed,
                    lookVec.y * speed,
                    lookVec.z * speed
            );

            earthChargeProjectileEntity.setNoGravity(false);

            world.spawnEntity(earthChargeProjectileEntity);
        }

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
        ItemStack itemStack = user.getStackInHand(hand);
        user.getItemCooldownManager().set(itemStack, COOLDOWN);
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        itemStack.decrementUnlessCreative(1, user);
        return ActionResult.SUCCESS;
    }

    @Override
    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        Vec3d dirVector = new Vec3d(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
        EarthChargeProjectileEntity earthChargeProjectileEntity = new EarthChargeProjectileEntity(
                world, pos.getX(), pos.getY(), pos.getZ(), dirVector);

        float speed = 1.2F;
        earthChargeProjectileEntity.setVelocity(
                direction.getOffsetX() * speed,
                direction.getOffsetY() * speed,
                direction.getOffsetZ() * speed
        );

        earthChargeProjectileEntity.setNoGravity(false);

        return earthChargeProjectileEntity;
    }

    @Override
    public void initializeProjectile(ProjectileEntity entity, double x, double y, double z, float power, float uncertainty) {
        entity.setVelocity(x, y, z, power, 0.0F);

        entity.setNoGravity(false);
    }

    @Override
    public ProjectileItem.Settings getProjectileSettings() {
        return ProjectileItem.Settings.builder()
                .positionFunction((pointer, facing) -> DispenserBlock.getOutputLocation(pointer, 1.0, Vec3d.ZERO))
                .uncertainty(0.0F)
                .power(1.3F)
                .overrideDispenseEvent(1051)
                .build();
    }
}