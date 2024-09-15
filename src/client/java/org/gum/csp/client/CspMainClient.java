package org.gum.csp.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.EntityType;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import org.gum.csp.CspMain;
import org.gum.csp.client.model.RocketEntityModel;
import org.gum.csp.client.renderer.RocketEntityRenderer;
import org.gum.csp.registries.EntityRegistry;
import org.gum.csp.registries.ParticleRegistry;

public class CspMainClient implements ClientModInitializer {

    public static final EntityModelLayer ROCKET_MODEL_LAYER = registerModel("rocketmodel", EntityRegistry.ROCKETENTITY, RocketEntityRenderer::new);


    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(ROCKET_MODEL_LAYER, RocketEntityModel::getTexturedModelData);

        registerParticle(ParticleRegistry.EXHAUST);

        //ClientNetworkHandler.registerPacketHandlers();
    }

    private void registerParticle (ParticleType particle) {
        ParticleFactoryRegistry.getInstance().register(particle, FlameParticle.Factory::new);
    }

    private static EntityModelLayer registerModel(String path, EntityType entityType, EntityRendererFactory rendererFactory) {
        EntityModelLayer modelLayer = new EntityModelLayer(new Identifier(CspMain.MODID, path), "main");
        EntityRendererRegistry.register(entityType, rendererFactory);
        return modelLayer;
    }
}
