package org.gum.csp.datastructs;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import org.slf4j.Logger;

public class RocketSettings {
    public RocketPart[] blocks;

    //Calculated
    public float Mass;
    public float Volatility;
    public float Power;
    public float Acceleration;
    public float burnTime;

    public RocketSettings (float Mass, float Volatility, float Power) { //setting these manually for now, will be based off of fuel later.
        this.Mass = Mass;
        this.Volatility = Volatility;
        this.Power = Power;
        this.Acceleration = Power/Mass;
        blocks = new RocketPart[0];
    }

    public RocketSettings (float Mass) { //setting these manually for now, will be based off of fuel later.
        this.Mass = Mass;
    }

    public RocketSettings (RocketPart[] parts) { //setting these manually for now, will be based off of fuel later.
        blocks = parts;

        for (RocketPart block : this.blocks) {
            this.Mass += block.Mass;
            this.Volatility += block.Volatility;
            this.Power += block.Power;
        }
        this.Acceleration = this.Power/this.Mass;
    }

    private void buildStats(){

    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();

        nbt.putInt("BlockCount", blocks.length);
        for (int i = 0; i < blocks.length; i++) {
            NbtCompound blockNbt = new NbtCompound();

            DataResult<NbtElement> encodedState = BlockState.CODEC.encodeStart(NbtOps.INSTANCE, blocks[i].Block);
            NbtElement stateNbt = encodedState.result().get();

            blockNbt.put("BlockState", stateNbt);
            blockNbt.putFloat("Mass", blocks[i].Mass);
            blockNbt.putFloat("Volatility", blocks[i].Volatility);
            blockNbt.putFloat("Power", blocks[i].Power);

            nbt.put("Block"+i, blockNbt);
        }

        return nbt;
    }

    public static RocketSettings fromNbt(NbtCompound nbt) {
        int blockCount = nbt.getInt("BlockCount");
        RocketPart[] blocks = new RocketPart[blockCount];

        for (int i = 0; i < blockCount; i++) {
            NbtCompound blockNbt = nbt.getCompound("Block"+i);
            DataResult<Pair<BlockState, NbtElement>> stateNbt = BlockState.CODEC.decode(NbtOps.INSTANCE, blockNbt.get("BlockState"));
            Pair<BlockState, NbtElement> statePair = stateNbt.result().get();
            BlockState state = statePair.getFirst();

            float Mass = blockNbt.getFloat("Mass");
            float Volatility = blockNbt.getFloat("Volatility");
            float Power = blockNbt.getFloat("Power");

            RocketPart part = new RocketPart(Mass, Volatility, Power);
            part.Block = state;

            blocks[i] = part;
        }

        return new RocketSettings(blocks);
    }

    //Mass:         nose cone = 1, body = 2, tail = 2
    //Volatility:   nose cone = 0, body = 4, tail = 6
    public static RocketSettings SIMPLE_ROCKET = new RocketSettings(5f, 10f, 2f);
}

