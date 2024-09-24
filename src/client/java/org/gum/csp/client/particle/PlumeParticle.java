package org.gum.csp.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class PlumeParticle extends AbstractSlowingParticle {

    protected PlumeParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        super(clientWorld, d, e, f, g, h, i);
        this.scale = 1.5f;
        this.maxAge = this.getMaxAge();
    }


    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public float getSize(float tickDelta) {
        float age = ((this.age) / (float) this.getMaxAge());
        float newScale = (float) Math.max(Math.min((-Math.pow((age * 0.5)-0.5f,2) + 1f), 1), 0);
        float newAlpha = (float) Math.max(Math.min((-Math.pow((age + 0.25),2) + 1.6f), 1), 0);

        this.setAlpha(newAlpha);
        return newScale;
    }


    @Override
    public int getMaxAge() {
        return 130;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            PlumeParticle plumeParticle = new PlumeParticle(clientWorld, d, e, f, g, h, i);
            plumeParticle.setSprite(this.spriteProvider);
            return plumeParticle;
        }
    }
}
