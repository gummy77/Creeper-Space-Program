package org.gum.csp.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.gum.csp.datastructs.RocketPart;
import org.gum.csp.registries.BlockRegistry;
import org.jetbrains.annotations.Nullable;

public class RocketPartBlockEntity extends BlockEntity {
    private RocketPart rocketPart;

    public RocketPartBlockEntity(BlockPos pos, BlockState state) {
        super(BlockRegistry.ROCKET_PART_BLOCK_ENTITY, pos, state);
        this.rocketPart = new RocketPart(1, 0 ,0);
    }
    public RocketPartBlockEntity(BlockPos pos, BlockState state, RocketPart rocketPart) {
        super(BlockRegistry.ROCKET_PART_BLOCK_ENTITY, pos, state);
        this.rocketPart = rocketPart;
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
