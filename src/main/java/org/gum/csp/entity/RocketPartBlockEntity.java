package org.gum.csp.entity;

import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.gum.csp.datastructs.RocketPart;
import org.gum.csp.datastructs.RocketSettings;
import org.gum.csp.registries.BlockRegistry;
import org.gum.csp.registries.EntityRegistry;
import org.gum.csp.registries.NetworkingConstants;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RocketPartBlockEntity extends BlockEntity {
    private RocketPart rocketPart;

    public RocketPartBlockEntity(BlockPos pos, BlockState state) {
        super(BlockRegistry.ROCKET_PART_BLOCK_ENTITY, pos, state);
        this.rocketPart = new RocketPart(1, 0 ,0);
    }
    public RocketPartBlockEntity(BlockPos pos, BlockState state, RocketPart rocketPart) {
        super(BlockRegistry.ROCKET_PART_BLOCK_ENTITY, pos, state);
        this.rocketPart = rocketPart;
        this.rocketPart.Block = state;
    }

    public void AssembleRocket(){
        RocketPart[] parts = {this.rocketPart.setBlock(this.getCachedState())};
        RocketSettings settings = new RocketSettings(parts);

        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.put("RocketSettings", settings.toNbt());


        RocketEntity entity = new RocketEntity(EntityRegistry.ROCKET_ENTITY, world);
        entity.setPosition(pos.getX()+0.5f, pos.getY(), pos.getZ()+0.5f);

        entity.readCustomDataFromNbt(nbtCompound);

        world.spawnEntity(entity);

        if(world.isClient()){
            System.out.println("Rocket assembled Client: " + entity.getRocketSettings().blocks.length);
        } else{
            System.out.println("Rocket assembled Server: " + entity.getRocketSettings().blocks.length);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        NbtCompound element = new NbtCompound();
        element.putFloat("mass", rocketPart.Mass);
        element.putFloat("volatility", rocketPart.Volatility);
        element.putFloat("power", rocketPart.Power);
        nbt.put("rocketPart", element);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        if(nbt.contains("rocketPart")) {
            NbtCompound compound = nbt.getCompound("rocketPart");
            this.rocketPart = new RocketPart(
                    compound.getFloat("mass"),
                    compound.getFloat("volatility"),
                    compound.getFloat("power")
            );
        }

    }

    public RocketPart getRocketPart() {
        return rocketPart;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

}
