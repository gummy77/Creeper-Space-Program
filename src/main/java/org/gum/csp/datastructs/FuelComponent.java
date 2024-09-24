package org.gum.csp.datastructs;

import net.minecraft.nbt.NbtCompound;

public class FuelComponent {
    public FuelType fuelType;
    public float capacity;
    public float amount;
    public float volatility;
    public float burnPower;
    public float burnSpeed;

    public FuelComponent(FuelType fuelType, float capacity, float amount, float burnPower, float burnSpeed) {
        this.fuelType = fuelType;
        this.capacity = capacity;
        this.amount = amount;
        this.burnPower = burnPower;
        this.burnSpeed = burnSpeed;
    }

    public FuelComponent(FuelType fuelType, float capacity, float burnPower, float burnSpeed) {
        this.fuelType = fuelType;
        this.capacity = capacity;
        this.amount = capacity;
        this.burnPower = burnPower;
        this.burnSpeed = burnSpeed;
    }

    public NbtCompound toNbt(){
        NbtCompound nbt = new NbtCompound();

        nbt.putString("fuelType", fuelType.toString());
        nbt.putFloat("capacity", capacity);
        nbt.putFloat("amount", amount);
        nbt.putFloat("volatility", volatility);
        nbt.putFloat("burnPower", burnPower);
        nbt.putFloat("burnSpeed", burnSpeed);

        return nbt;
    }

    public static FuelComponent fromNbt(NbtCompound nbt){
        FuelType fuelType = FuelType.valueOf(nbt.getString("fuelType"));
        float capacity = nbt.getFloat("capacity");
        float amount = nbt.getFloat("amount");
        float volatility = nbt.getFloat("volatility");
        float burnPower = nbt.getFloat("burnPower");
        float burnSpeed = nbt.getFloat("burnSpeed");
                return new FuelComponent(fuelType, capacity, amount, burnPower, burnSpeed);
    }

    public enum FuelType {
        SOLID,
        KEROLOX

    }
}

