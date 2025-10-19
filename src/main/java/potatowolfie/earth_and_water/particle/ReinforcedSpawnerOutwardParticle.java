package potatowolfie.earth_and_water.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class ReinforcedSpawnerOutwardParticle extends AnimatedParticle {

    ReinforcedSpawnerOutwardParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, spriteProvider, 0.0125F);
        this.velocityX = velocityX;
        this.velocityY = 0.0;
        this.velocityZ = velocityZ;
        this.gravityStrength = 0.0F;
        this.scale *= 0.75F;
        this.maxAge = 19;
        this.setTargetColor(15916745);
        this.updateSprite(spriteProvider);
    }

    public void move(double dx, double dy, double dz) {
        this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
        this.repositionFromBoundingBox();
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;
        private int particleIndex = 0;
        private int totalParticles = 0;
        private static final double SPEED = 0.17;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
            if (particleIndex == 0) {
                totalParticles = 12 + random.nextInt(6);
            }

            double angle = (2 * Math.PI * particleIndex) / totalParticles;
            double velX = Math.cos(angle) * SPEED;
            double velZ = Math.sin(angle) * SPEED;

            particleIndex = (particleIndex + 1) % totalParticles;

            return new ReinforcedSpawnerOutwardParticle(clientWorld, d, e, f, velX, 0, velZ, this.spriteProvider);
        }
    }
}