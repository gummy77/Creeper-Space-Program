package org.gum.csp.datastructs;

import net.minecraft.nbt.NbtCompound;

public class RocketSettings {
    public float Mass;
    RocketPart[] blocks;

    //Calculated
    public float Volatility;
    public float Power;
    public float Acceleration;
    public float burnTime;

    public RocketSettings (float Mass, float Volatility, float Power) { //setting these manually for now, will be based off of fuel later.
        this.Mass = Mass;
        this.Volatility = Volatility;
        this.Power = Power;
        this.Acceleration = Power/Mass;
    }

    public RocketSettings (NbtCompound nbt) {
        this.Mass = nbt.getFloat("Mass");
        this.Volatility = nbt.getFloat("Volatility");
        this.Power = nbt.getFloat("Power");
        this.Acceleration = this.Power/this.Mass;
    }

    public RocketSettings (RocketPart[] blocks) { //setting these manually for now, will be based off of fuel later.
        this.blocks = blocks;
        for (RocketPart block : this.blocks) {
            this.Mass += block.Mass;
            this.Volatility += block.Volatility;
            this.Power += block.Power;
        }
        this.Acceleration = this.Power/this.Mass;
    }



    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();

        //TODO make this good

        return nbt;
    }

    //Mass:         nose cone = 1, body = 2, tail = 2
    //Volatility:   nose cone = 0, body = 4, tail = 6
    public static RocketSettings SIMPLE_ROCKET = new RocketSettings(5f, 10f, 2f);
}

