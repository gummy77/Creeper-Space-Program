package org.gum.csp.datastructs;

import net.minecraft.nbt.NbtCompound;
import org.gum.csp.registries.PayloadRegistry;

public class RocketSettings {
    public RocketPart[] blocks;
    public PayloadRegistry.PAYLOADS payload;

    //Calculated from blocks
    public float Mass;
    public float Volatility;
    public float Power;
    FuelComponent fuel;

    //Calculated from Settings
    public float Acceleration;
    public float burnTime;

    public RocketSettings (float Mass, float Volatility, float Power) { //setting these manually for now, will be based off of fuel later.
        this.Mass = Mass;
        this.Volatility = Volatility;
        this.Power = Power;
        this.Acceleration = Power/Mass;
        blocks = new RocketPart[0];
    }

    public RocketSettings (RocketPart[] parts, PayloadRegistry.PAYLOADS payload){
        this(parts);
        this.payload = payload;
        this.Mass += PayloadRegistry.getPayload(payload).Mass;
        this.Acceleration = this.Power/this.Mass;
    }

    public RocketSettings (RocketPart[] parts) { //setting these manually for now, will be based off of fuel later.
        blocks = parts;

        FuelComponent.FuelType type = FuelComponent.FuelType.SOLID;
        float fuelCapacity = 0;
        float fuelAmount = 0;
        float fuelBurnPower = 1;
        float fuelBurnSpeed = 1;

        for (RocketPart block : this.blocks) {
            this.Mass += block.Mass;
            this.Volatility += block.Volatility;
            this.Power += block.Power;

            if(block.fuelComponent != null) {
                type = block.fuelComponent.fuelType;
                fuelCapacity += block.fuelComponent.capacity;
                fuelAmount += block.fuelComponent.amount;
                fuelBurnPower *= block.fuelComponent.burnPower;
                fuelBurnSpeed *= block.fuelComponent.burnSpeed;
            }
        }

        this.fuel = new FuelComponent(type, fuelCapacity, fuelAmount, fuelBurnPower, fuelBurnSpeed);

        this.Power = Power * fuelBurnPower;
        this.burnTime = fuelAmount * fuelBurnSpeed;

        this.Acceleration = this.Power/this.Mass;
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();

        if(payload != null) {
            nbt.putString("Payload", payload.toString());
        }
        nbt.putInt("BlockCount", blocks.length);
        for (int i = 0; i < blocks.length; i++) {
            nbt.put("Block"+i, blocks[i].toNbt());
        }

        return nbt;
    }

    public static RocketSettings fromNbt(NbtCompound nbt) {
        int blockCount = nbt.getInt("BlockCount");
        RocketPart[] blocks = new RocketPart[blockCount];

        PayloadRegistry.PAYLOADS payload = null;
        try {
            payload = PayloadRegistry.PAYLOADS.valueOf(nbt.getString("Payload"));
        } catch (IllegalArgumentException ignored) {}

        for (int i = 0; i < blockCount; i++) {
            NbtCompound blockNbt = nbt.getCompound("Block"+i);

            blocks[i] = RocketPart.fromNbt(blockNbt);;
        }

        if(payload != null) {
            return new RocketSettings(blocks, payload);
        }
        return new RocketSettings(blocks);
    }

    //Mass:         nose cone = 1, body = 2, tail = 2
    //Volatility:   nose cone = 0, body = 4, tail = 6
    public static RocketSettings SIMPLE_ROCKET = new RocketSettings(5f, 10f, 2f);
}

