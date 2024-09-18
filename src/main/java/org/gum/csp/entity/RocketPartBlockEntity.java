package org.gum.csp.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.gum.csp.datastructs.RocketPart;
import org.gum.csp.datastructs.RocketSettings;
import org.gum.csp.registries.BlockRegistry;
import org.gum.csp.registries.EntityRegistry;
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

    public void AssembleRocket(BlockPos pos){
        RocketEntity entity = EntityRegistry.ROCKET_ENTITY.create(world);

        List<RocketPart> parts = new ArrayList<RocketPart>();
        parts.add(this.rocketPart);


        System.out.println("Created with: " + parts.size());
        entity.setPosition(pos.getX()+0.5f, pos.getY(), pos.getZ()+0.5f);
        entity.rocketSettings = new RocketSettings(parts.toArray(new RocketPart[parts.size()]));

        System.out.println("Ended with: " + entity.rocketSettings.blocks.length);

        world.breakBlock(pos, false);
        world.spawnEntity(entity);

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
