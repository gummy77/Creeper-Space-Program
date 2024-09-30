package org.gum.csp.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
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

public class RocketPartBlockEntity extends BlockEntity {
    private RocketPart rocketPart;

    public RocketPartBlockEntity(BlockPos pos, BlockState state) {
        super(BlockRegistry.ROCKET_PART_BLOCK_ENTITY, pos, state);
        this.rocketPart = new RocketPart(RocketPart.PartType.BODY, 0.5f, 1, 1,0 ,null);
    }
    public RocketPartBlockEntity(BlockPos pos, BlockState state, RocketPart rocketPart) {
        super(BlockRegistry.ROCKET_PART_BLOCK_ENTITY, pos, state);
        this.rocketPart = rocketPart.copy();

        this.rocketPart.Block = state;
    }

    public void AssembleRocket(){

        RocketPartBlockEntity baseBlock = getBase(this.pos);

        ArrayList<RocketPart> parts = new ArrayList<>(baseBlock.getneighbors(baseBlock.pos));

        if(parts.size() < 3){ //TODO have a "isvalid" function that checks if parts arent upside down and stuff
            //yell at players

            for(RocketPart part : parts) {
                world.breakBlock(baseBlock.pos.add(part.offset.getX(), part.offset.getY(), part.offset.getZ()), true);
            }
            return;
        } else {
            for(RocketPart part : parts) {
                world.breakBlock(baseBlock.pos.add(part.offset.getX(), part.offset.getY(), part.offset.getZ()), false);
            }
        }


        RocketSettings settings = new RocketSettings(parts.toArray(new RocketPart[0]));

        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.put("RocketSettings", settings.toNbt());

        RocketEntity entity = new RocketEntity(EntityRegistry.ROCKET_ENTITY, world);
        entity.setPosition(baseBlock.pos.getX()+0.5f, baseBlock.pos.getY(), baseBlock.pos.getZ()+0.5f);
        entity.readCustomDataFromNbt(nbtCompound);

        world.spawnEntity(entity);
    }

    protected RocketPartBlockEntity getBase(BlockPos clickPos) {
        RocketPartBlockEntity entity = this;
        boolean hasNext = true;

        while (hasNext){
            BlockEntity nextBEntity = world.getBlockEntity(clickPos.add(0,-1,0));
            if(nextBEntity instanceof RocketPartBlockEntity){
                entity = (RocketPartBlockEntity) nextBEntity;
                clickPos = clickPos.add(0,-1,0);
            } else {
                hasNext = false;
            }
        }
        return entity;
    }

    protected ArrayList<RocketPart> getneighbors(BlockPos basePos){
        ArrayList<RocketPart> parts = new ArrayList<>();

        BlockEntity entity = world.getBlockEntity(this.getPos().add(0, 1, 0));
        if(entity instanceof RocketPartBlockEntity) {
            parts.addAll(((RocketPartBlockEntity) entity).getneighbors(basePos));
        }

        BlockPos offset = this.pos.add(-basePos.getX(), -basePos.getY(), -basePos.getZ());

        parts.add(this.rocketPart.setBlock(this.getCachedState(), offset.mutableCopy()));


        return parts;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.put("rocketPart", rocketPart.toNbt());

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        if(nbt.contains("rocketPart")) {
            NbtCompound compound = nbt.getCompound("rocketPart");
            this.rocketPart = RocketPart.fromNbt(compound);
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
