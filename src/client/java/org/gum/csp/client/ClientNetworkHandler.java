package org.gum.csp.client;

import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import org.gum.csp.entity.RocketEntity;
import org.gum.csp.registries.NetworkingConstants;

@Environment(EnvType.CLIENT)
public class ClientNetworkHandler {

    public static void registerPacketHandlers(){
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.ATTACH_FUSE_PACKET_ID, ClientNetworkHandler::onEntityLink);
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.LAUNCH_ROCKET_PACKET_ID, ClientNetworkHandler::onRocketLaunch);
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.ASSEMBLE_ROCKET_PACKET_ID, ClientNetworkHandler::onAssembleRocket);
    }

    public static void onEntityLink(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        IntList list = buf.readIntList();
        int rocketId = list.getInt(0);
        int otherId = list.getInt(1);

        client.execute(() -> {
            Entity rocket = client.world.getEntityById(rocketId);
            if(rocket instanceof RocketEntity){
                ((RocketEntity) rocket).setLinkedEntityId(otherId);
            }
        });
    }

    public static void onRocketLaunch(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        IntList list = buf.readIntList();
        int rocketId = list.getInt(0);
        double launchDirection = buf.readDouble();
        client.execute(() -> {
            Entity rocket = client.world.getEntityById(rocketId);
            if(rocket instanceof RocketEntity){
                ((RocketEntity) rocket).Launch(launchDirection);
            }
        });
    }

    public static void onAssembleRocket(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        int rocketId = buf.readInt();
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.put("RocketSettings", buf.readNbt());

        client.execute(() -> {
            Entity entity = client.world.getEntityById(rocketId);
            if(entity instanceof RocketEntity){
                ((RocketEntity) entity).readCustomDataFromNbt(nbtCompound);
            }
        });
    }


}
