package org.gum.csp.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class PlumeParticle extends AbstractSlowingParticle {

    protected PlumeParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        super(clientWorld, d, e, f, g, h, i);
        this.scale = 1f;
        this.maxAge = getMaxAge();

    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
    }

    @Override
    public float getSize(float tickDelta) {
        float f = ((float)this.age + tickDelta) / (float)this.maxAge;
        return this.scale * (float) (-Math.pow((f * 1.75f)-0.75f,2) + 1.1f);
    }

    @Override
    public int getMaxAge() {
        return 100;
    }

    public int getBrightness(float tint) {
        return 255; //j | k << 16;
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
