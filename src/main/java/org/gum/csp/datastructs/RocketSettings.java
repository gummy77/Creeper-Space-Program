package org.gum.csp.datastructs;


import net.minecraft.nbt.NbtCompound;
import org.gum.csp.registries.PayloadRegistry;

import java.lang.reflect.Array;
import java.util.*;

public class RocketSettings {
    public RocketPart[] blocks;
    public PayloadRegistry.PAYLOADS payload;
    public boolean isFailing = false;
    public int failurePart = 0;

    //Calculated from blocks
    public float Mass;
    public float Volatility;
    public float Power;
    FuelComponent fuel;
    PartMaterial[] primaryMaterials;

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

    public RocketSettings (RocketPart[] parts, PayloadRegistry.PAYLOADS payload, boolean isFailing){
        this(parts, isFailing);
        this.payload = payload;
        this.Mass += PayloadRegistry.getPayload(payload).Mass;
        this.Acceleration = this.Power/this.Mass;
    }

    public RocketSettings (RocketPart[] parts, boolean isFailing) { //setting these manually for now, will be based off of fuel later.
        blocks = parts;

        this.isFailing = isFailing;

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
        this.primaryMaterials = calculatePrimaryMaterials();
    }

    public float getMaxWidth(){
        float mWidth = 0;
        for(RocketPart block : this.blocks){
            if(block.radius > mWidth){
                mWidth = block.radius;
            }
        }
        return mWidth;
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();

        if(payload != null) {
            nbt.putString("Payload", payload.toString());
        }
        nbt.putBoolean("Failing", isFailing);
        nbt.putInt("BlockCount", blocks.length);
        for (int i = 0; i < blocks.length; i++) {
            nbt.put("Block"+i, blocks[i].toNbt());
        }

        return nbt;
    }

    public static RocketSettings fromNbt(NbtCompound nbt) {
        int blockCount = nbt.getInt("BlockCount");
        RocketPart[] blocks = new RocketPart[blockCount];

        boolean isFailing = nbt.getBoolean("Failing");

        PayloadRegistry.PAYLOADS payload = null;
        try {
            payload = PayloadRegistry.PAYLOADS.valueOf(nbt.getString("Payload"));
        } catch (IllegalArgumentException ignored) {}

        for (int i = 0; i < blockCount; i++) {
            NbtCompound blockNbt = nbt.getCompound("Block"+i);

            blocks[i] = RocketPart.fromNbt(blockNbt);;
        }

        if(payload != null) {
            return new RocketSettings(blocks, payload, isFailing);
        }
        return new RocketSettings(blocks, isFailing);
    }

    //Mass:         nose cone = 1, body = 2, tail = 2
    //Volatility:   nose cone = 0, body = 4, tail = 6
    public static RocketSettings SIMPLE_ROCKET = new RocketSettings(5f, 10f, 2f);

    /**
     * Calculates the most common materials among all used in the RocketPart's
     * @return The most common materials by instance count (typically just one, but in the case of a tie there will be multiple)
     */
    private PartMaterial[] calculatePrimaryMaterials() {
        Map<PartMaterial, Integer> materialMap = new HashMap<PartMaterial, Integer>();

        for (RocketPart block : blocks) {
            materialMap.compute(block.getMaterial(), (k, currentCount) -> currentCount == null ? 1 : currentCount + 1);
        }

        List<PartMaterial> mostCommonMaterials = new ArrayList<PartMaterial>();
        int currentMaxCount = 0;

        for (Map.Entry<PartMaterial, Integer> entry : materialMap.entrySet()) {
            PartMaterial material = entry.getKey();
            Integer count = entry.getValue();
            if (currentMaxCount < count) {
                mostCommonMaterials = new ArrayList<>(List.of(material));
                currentMaxCount = count;
            } else if (currentMaxCount == count) {
                mostCommonMaterials.add(material);
            }
        }

        return mostCommonMaterials.toArray(new PartMaterial[0]);
    }

    public String getRocketTitle(){
        return calculatePrimaryMaterials()[0].getFormattedName() + " Rocket";
    }

    /**
     * Checks the Primary Materials of the Rocket for Material
     * @param material The Material to check for
     * @return True if the material is one of the Primary Materials
     */
    public boolean primaryMaterialsContains(PartMaterial material) {
        return Arrays.asList(primaryMaterials).contains(material);
    }
}

