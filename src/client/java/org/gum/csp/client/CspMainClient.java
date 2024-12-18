package org.gum.csp.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import org.gum.csp.client.item.PayloadCompassAnglePredicateProvider;
import org.gum.csp.client.registries.FluidRenderRegistry;
import org.gum.csp.client.registries.ModelRegistry;
import org.gum.csp.client.particle.PlumeParticle;
import org.gum.csp.client.screen.SolidFuelPressScreen;
import org.gum.csp.item.PayloadTrackingCompass;
import org.gum.csp.registries.ItemRegistry;
import org.gum.csp.registries.ParticleRegistry;
import org.gum.csp.registries.ScreenRegistry;

public class CspMainClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModelRegistry.registerModels();
        ClientNetworkHandler.registerPacketHandlers();

        registerParticle(ParticleRegistry.EXHAUST, PlumeParticle.Factory::new);

        HandledScreens.register(ScreenRegistry.SOLID_FUEL_PRESS_SCREEN_HANDLER_TYPE, SolidFuelPressScreen::new);

        FluidRenderRegistry.registerFluids();
        registerModelPredicateProviders();
    }

    private <T extends ParticleEffect> void registerParticle (ParticleType<T> particle, ParticleFactoryRegistry.PendingParticleFactory<T> factory) {
        ParticleFactoryRegistry.getInstance().register(particle, factory);
    }

    public static void registerModelPredicateProviders() {
        ModelPredicateProviderRegistry.register(ItemRegistry.PAYLOAD_COMPASS, new Identifier("angle"), new PayloadCompassAnglePredicateProvider((world, stack, entity) -> {
            return PayloadTrackingCompass.createPayloadPos(stack.getOrCreateNbt(), world);
        }));
    }
}
