package org.gum.csp.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import org.gum.csp.entity.RocketEntity;
import org.gum.csp.registries.NetworkingConstants;

@Environment(EnvType.CLIENT)
public class ClientNetworkHandler {

    public static void registerPacketHandlers(){
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.ATTACH_FUSE_PACKET_ID, ClientNetworkHandler::onEntityLink);
    }

    public static void onEntityLink(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){

        RocketEntity rocket = (RocketEntity) client.world.getEntityById(buf.getInt(0));
        int otherId = buf.getInt(1);

        client.execute(() -> {
            // Everything in this lambda is run on the render thread
            rocket.setLinkedEntityId(otherId);
        });
    }
}
