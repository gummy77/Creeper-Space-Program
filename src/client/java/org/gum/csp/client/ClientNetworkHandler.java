package org.gum.csp.client;

import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import org.gum.csp.entity.RocketEntity;
import org.gum.csp.registries.NetworkingConstants;
import org.gum.csp.registries.SoundRegistry;

@Environment(EnvType.CLIENT)
public class ClientNetworkHandler {

    public static void registerPacketHandlers(){
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.ATTACH_FUSE_PACKET_ID, ClientNetworkHandler::onEntityLink);
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.LAUNCH_ROCKET_PACKET_ID, ClientNetworkHandler::onRocketLaunch);
    }

    public static void onEntityLink(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        IntList list = buf.readIntList();
        int rocketId = list.getInt(0);
        int otherId = list.getInt(1);
        RocketEntity rocket = (RocketEntity) client.world.getEntityById(rocketId);

        client.execute(() -> {
            rocket.setLinkedEntityId(otherId);
        });
    }

    public static void onRocketLaunch(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        IntList list = buf.readIntList();
        int rocketId = list.getInt(0);
        double launchDirection = buf.readDouble();
        RocketEntity rocket = (RocketEntity) client.world.getEntityById(rocketId);

        client.execute(() -> {
            rocket.Launch(launchDirection);
        });
    }


}
