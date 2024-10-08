package org.gum.csp.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import org.gum.csp.CspMain;
import org.gum.csp.client.item.PayloadCompassAnglePredicateProvider;
import org.gum.csp.client.model.GneepEntityModel;
import org.gum.csp.client.model.ParachuteModel;
import org.gum.csp.client.model.PayloadEntityModel;
import org.gum.csp.client.model.RocketEntityModel;
import org.gum.csp.client.particle.PlumeParticle;
import org.gum.csp.client.renderer.GneepEntityRenderer;
import org.gum.csp.client.renderer.ParachuteRenderer;
import org.gum.csp.client.renderer.PayloadEntityRenderer;
import org.gum.csp.client.renderer.RocketEntityRenderer;
import org.gum.csp.item.PayloadTrackingCompass;
import org.gum.csp.registries.EntityRegistry;
import org.gum.csp.registries.ItemRegistry;
import org.gum.csp.registries.ParticleRegistry;
import org.gum.csp.registries.ScreenRegistry;

public class CspMainClient implements ClientModInitializer {

    public static final EntityModelLayer ROCKET_MODEL_LAYER = registerModel("rocket_model", EntityRegistry.ROCKET_ENTITY, RocketEntityRenderer::new);

    public static final EntityModelLayer PARACHUTE_MODEL_LAYER = registerModel("parachute_model", EntityRegistry.PAYLOAD_ENTITY, ParachuteRenderer::new);
    public static final EntityModelLayer PAYLOAD_MODEL_LAYER = registerModel("payload_model", EntityRegistry.PAYLOAD_ENTITY, PayloadEntityRenderer::new);

    public static final EntityModelLayer GNEEP_MODEL_LAYER = registerModel("gneep_model", EntityRegistry.GNEEP_ENTITY, GneepEntityRenderer::new);


    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(ROCKET_MODEL_LAYER, RocketEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(PAYLOAD_MODEL_LAYER, PayloadEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(PARACHUTE_MODEL_LAYER, ParachuteModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(GNEEP_MODEL_LAYER, GneepEntityModel::getTexturedModelData);


        registerParticle(ParticleRegistry.EXHAUST, PlumeParticle.Factory::new);

        registerModelPredicateProviders();
        ClientNetworkHandler.registerPacketHandlers();
    }

    private <T extends ParticleEffect> void registerParticle (ParticleType<T> particle, ParticleFactoryRegistry.PendingParticleFactory<T> factory) {
        ParticleFactoryRegistry.getInstance().register(particle, factory);
    }

    private static <E extends Entity> EntityModelLayer registerModel(String path, EntityType<E> entityType, EntityRendererFactory<E> rendererFactory) {
        EntityModelLayer modelLayer = new EntityModelLayer(new Identifier(CspMain.MODID, path), "main");
        EntityRendererRegistry.register(entityType, rendererFactory);
        return modelLayer;
    }

    public static void registerModelPredicateProviders() {
        // For versions before 1.21, replace 'Identifier.ofVanilla' with 'new Identifier'.
        ModelPredicateProviderRegistry.register(ItemRegistry.PAYLOAD_COMPASS, new Identifier("angle"), new PayloadCompassAnglePredicateProvider((world, stack, entity) -> {
            return PayloadTrackingCompass.createPayloadPos(stack.getOrCreateNbt(), world);
        }));
    }
}
