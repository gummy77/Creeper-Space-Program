package org.gum.csp.packet;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ClientNetworkHandler implements PacketListener {

    public static void registerPacketHandlers(){

    }

    public void onEntityLink(EntityLinkS2CPacket packet){
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Entity entity = this.world.getEntityById(packet.getAttachedEntityId());
        if (entity instanceof MobEntity) {
            ((MobEntity)entity).setHoldingEntityId(packet.getHoldingEntityId());
        }
    }

    @Override
    public void onDisconnected(Text reason) {
    }

    @Override
    public ClientConnection getConnection() {
        return null; //this.connection;
    }
}
