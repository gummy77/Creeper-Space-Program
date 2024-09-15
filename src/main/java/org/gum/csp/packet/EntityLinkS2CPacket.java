package org.gum.csp.packet;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

public class EntityLinkS2CPacket implements Packet<ClientNetworkHandler> {
    private final int attachedId;
    private final int linkedId;

    public EntityLinkS2CPacket(Entity attachedEntity, @Nullable Entity holdingEntity) {
        this.attachedId = attachedEntity.getId();
        this.linkedId = holdingEntity != null ? holdingEntity.getId() : 0;
    }

    public EntityLinkS2CPacket(PacketByteBuf buf) {
        this.attachedId = buf.readInt();
        this.linkedId = buf.readInt();
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(this.attachedId);
        buf.writeInt(this.linkedId);
    }

    @Override
    public void apply(ClientNetworkHandler listener) {
        listener.onEntityLink(this);
    }

}
