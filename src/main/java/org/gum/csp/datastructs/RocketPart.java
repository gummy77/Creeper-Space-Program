package org.gum.csp.datastructs;

import com.ibm.icu.text.MessagePattern;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Unique;


public class RocketPart {
    public PartType partType;
    public float Mass;
    public FuelComponent fuelComponent;

    //set at assemble
    public BlockState Block;
    public BlockPos offset;

    //Calculated
    public float Volatility;
    public float Power;

    public RocketPart(PartType type, float Mass, float Volatility, float Power, FuelComponent fuelComponent) { //setting these manually for now, will be based off of fuel later.
        this.partType = type;
        this.Mass = Mass;
        this.Volatility = Volatility;
        this.Power = Power;
        this.fuelComponent = fuelComponent;
    }

    public RocketPart(PartType type, float Mass, float Volatility, float Power, FuelComponent fuelComponent, BlockState state, BlockPos offset) { //setting these manually for now, will be based off of fuel later.
        this.partType = type;
        this.Mass = Mass;
        this.Volatility = Volatility;
        this.Power = Power;
        this.Block = state;
        this.offset = offset;
        this.fuelComponent = fuelComponent;
    }

    public RocketPart setBlock(BlockState Block, BlockPos offset) {
        this.Block = Block;
        this.offset = offset;
        return this;
    }

    public NbtCompound toNbt(){
        NbtCompound nbt = new NbtCompound();

        nbt.putString("Type", partType.toString());
        nbt.putFloat("Mass", Mass);
        nbt.putFloat("Volatility", Volatility);
        nbt.putFloat("Power", Power);

        if(fuelComponent != null){
            nbt.put("fuelComponent", fuelComponent.toNbt());
        }

        if(offset != null){
            nbt.putLong("Offset", offset.asLong());
        }

        if(Block != null){
            DataResult<NbtElement> encodedState = BlockState.CODEC.encodeStart(NbtOps.INSTANCE, Block);
            NbtElement stateNbt = encodedState.result().get();
            nbt.put("BlockState", stateNbt);
        }

        return nbt;
    }

    public static RocketPart fromNbt(NbtCompound nbt){
        PartType Type = PartType.valueOf(nbt.getString("Type"));
        float Mass = nbt.getFloat("Mass");
        float Volatility = nbt.getFloat("Volatility");
        float Power = nbt.getFloat("Power");

        FuelComponent fuelComponent = nbt.contains("fuelComponent") ? FuelComponent.fromNbt(nbt.getCompound("fuelComponent")) : null;

        BlockPos Offset = null;
        if(nbt.contains("Offset")){
            Offset = BlockPos.fromLong(nbt.getLong("Offset"));
        }

        BlockState state = null;
        if(nbt.contains("BlockState")){
            DataResult<Pair<BlockState, NbtElement>> stateNbt = BlockState.CODEC.decode(NbtOps.INSTANCE, nbt.get("BlockState"));
            Pair<BlockState, NbtElement> statePair = stateNbt.result().get();
            state = statePair.getFirst();
        }

        if(Offset != null && state != null) {
            return new RocketPart(Type, Mass, Volatility, Power, fuelComponent, state, Offset);
        } else {
            return new RocketPart(Type, Mass, Volatility, Power, fuelComponent);
        }
    }

    public RocketPart copy(){
        return new RocketPart(partType, Mass, Volatility, Power, fuelComponent, Block, offset);
    }

    public enum PartType {
        NOSE,
        BODY,
        EXHAUST
    }
}
