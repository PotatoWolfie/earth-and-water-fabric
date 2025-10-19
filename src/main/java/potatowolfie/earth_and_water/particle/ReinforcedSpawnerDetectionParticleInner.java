package potatowolfie.earth_and_water.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class ReinforcedSpawnerDetectionParticleInner extends BillboardParticle {
    private final SpriteProvider spriteProvider;
    private static final int field_47460 = 8;

    protected ReinforcedSpawnerDetectionParticleInner(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float scale, SpriteProvider spriteProvider) {
        super(world, x, y, z, 0.0, 0.0, 0.0, spriteProvider.getFirst());
        this.spriteProvider = spriteProvider;
        this.velocityMultiplier = 0.96F;
        this.gravityStrength = 0.0F;
        this.ascending = velocityY > 0;
        this.velocityX *= 0.0;
        this.velocityY = -0.09;
        this.velocityZ *= 0.0;
        this.velocityX += velocityX;
        this.velocityY += velocityY;
        this.velocityZ += velocityZ;
        this.scale *= 0.75F * scale;
        this.maxAge = 35;
        this.updateSprite(spriteProvider);
        this.collidesWithWorld = false;
    }

    public RenderType getRenderType() {
        return RenderType.PARTICLE_ATLAS_OPAQUE;
    }

    public int getBrightness(float tint) {
        return 240;
    }

    public Rotator getRotator() {
        return Rotator.Y_AND_W_ONLY;
    }

    public void tick() {
        super.tick();
        this.updateSprite(this.spriteProvider);
    }

    public float getSize(float tickProgress) {
        return this.scale * MathHelper.clamp(((float)this.age + tickProgress) / (float)this.maxAge * 32.0F, 0.0F, 1.0F);
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
            return new ReinforcedSpawnerDetectionParticleInner(clientWorld, d, e, f, 0.0, 0.0, 0.0, 1.5F, this.spriteProvider);
        }
    }
}