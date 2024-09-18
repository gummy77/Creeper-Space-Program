package org.gum.csp.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

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
//        float f = ((float)this.age + tint) / (float)this.maxAge;
//        f = MathHelper.clamp(f, 0.0F, 1.0F);
//        int i = super.getBrightness(tint);
//        int j = i & 255;
//        int k = i >> 16 & 255;
//        j += (int)(f * 15.0F * 16.0F);
//        if (j > 240) {
//            j = 240;
//        }
//
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

    public static ParticleTextureSheet PLUME_TEXTURE_SHEET = new ParticleTextureSheet() {
        public void begin(BufferBuilder builder, TextureManager textureManager) {
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE);
            builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
        }

        public void draw(Tessellator tessellator) {
            tessellator.draw();
        }

        public String toString() {
            return "PLUME_TEXTURE_SHEET";
        }
    };
}
