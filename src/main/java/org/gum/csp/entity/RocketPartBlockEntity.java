package org.gum.csp.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.gum.csp.datastructs.PartMaterial;
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
        this.rocketPart = new RocketPart.RocketPartBuilder(RocketPart.PartType.BODY, PartMaterial.NONE, 0.5f, 1, 1).build();
    }
    public RocketPartBlockEntity(BlockPos pos, BlockState state, RocketPart rocketPart) {
        super(BlockRegistry.ROCKET_PART_BLOCK_ENTITY, pos, state);
        this.rocketPart = rocketPart.copy();

        this.rocketPart.block = state;
    }

    public void AssembleRocket(PlayerEntity player) {

        RocketPartBlockEntity baseBlock = getBase(this.pos);

        ArrayList<RocketPart> parts = new ArrayList<>(baseBlock.getneighbors(baseBlock.pos));

        boolean validConfig = isValidConfig(parts);

        RocketSettings settings = new RocketSettings(parts.toArray(new RocketPart[0]), false);

        boolean canFly = (settings.Power / settings.Mass) > 1f;

        canFly = RocketEntity.calculateMaxHeight(settings) > 1000 && canFly;

        if(!validConfig || !canFly){
            //yell at players
            for(RocketPart part : parts) {
                world.breakBlock(baseBlock.pos.add(part.offset.getX(), part.offset.getY(), part.offset.getZ()), !player.isCreative());
            }
            return;
        }

        for(RocketPart part : parts) {
            world.breakBlock(baseBlock.pos.add(part.offset.getX(), part.offset.getY(), part.offset.getZ()), false);
        }

        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.put("RocketSettings", settings.toNbt());
        nbtCompound.putDouble("launchDirection",  Random.create().nextFloat() * Math.PI * 4);

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

    private boolean isValidConfig(ArrayList<RocketPart> parts) {
        if (parts.size() < 3) return false;

        if (parts.get(0).partType != RocketPart.PartType.NOSE) return false;
        if (parts.get(parts.size() - 1).partType != RocketPart.PartType.EXHAUST) return false;

        if(parts.size()-2 < parts.get(parts.size() - 1).getMaterial().getMinParts()) return false;
        if(parts.size()-2 > parts.get(parts.size() - 1).getMaterial().getMaxParts()) return false;

        for (int rocketPartIndex = 1; rocketPartIndex < parts.size() - 1; rocketPartIndex++) {
            if (parts.get(rocketPartIndex).partType == RocketPart.PartType.NOSE
            || parts.get(rocketPartIndex).partType == RocketPart.PartType.EXHAUST) {
                return false;
            }
        }
        return true;
    }
}
