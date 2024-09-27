package org.gum.csp.entity;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.gum.csp.datastructs.Payload;
import org.gum.csp.datastructs.PayloadSettings;
import org.gum.csp.item.PayloadItem;
import org.gum.csp.registries.ItemRegistry;
import org.gum.csp.registries.NetworkingConstants;
import org.gum.csp.registries.PayloadRegistry;

public class PayloadEntity extends Entity {

    public static final EntitySettings settings = new EntitySettings(
            "payload_entity",
            SpawnGroup.MISC,
            2f, 1f,
            true
    );

    private PayloadSettings payloadSettings;

    private boolean hasNetworked = false;

    public PayloadEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();

        if(!world.isClient) {
            if(!hasNetworked && payloadSettings != null) {
                PacketByteBuf buf = PacketByteBufs.create(); //TODO move to its own function and update to work with saving
                buf.writeInt(this.getId());
                buf.writeNbt(payloadSettings.toNbt());
                for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, this.getBlockPos())) {
                    ServerPlayNetworking.send(player, NetworkingConstants.DEPLOY_PAYLOAD_PACKET_ID, buf);
                }
                hasNetworked = true;
            }
        }

        this.addVelocity(0, -0.02, 0);
        this.move(MovementType.SELF, this.getVelocity());
    }

    public PayloadSettings getPayloadSettings(){
        return this.payloadSettings;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        ActionResult actionResult = this.interactWithItem(player, hand);
        if (actionResult.isAccepted()) {
            return actionResult;
        } else {
            return super.interact(player, hand);
        }
    }

    @Override
    public boolean handleAttack(Entity attacker) {
        if(attacker instanceof PlayerEntity) {
            if(((PlayerEntity) attacker).getStackInHand(((PlayerEntity) attacker).getActiveHand()).isOf(ItemRegistry.DEV_WAND)){
                kill();
            }
        }
        return false;
    }

    private ActionResult interactWithItem(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isOf(ItemRegistry.DEV_WAND)) {
            String out = this.world.isClient ? "Client: " : "Server: ";

            if(this.payloadSettings != null) {
                System.out.println(out + this.payloadSettings.payload.toString());
            } else {
                System.out.println(out + "NULL");
            }

        }
        return ActionResult.PASS;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        if(nbt.contains("PayloadSettings")) {
            this.payloadSettings = PayloadSettings.fromNbt(nbt.getCompound("PayloadSettings"));
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if(this.payloadSettings != null) {
            nbt.put("PayloadSettings", this.payloadSettings.toNbt());
        }
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
