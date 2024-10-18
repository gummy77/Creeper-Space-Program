package org.gum.csp.datastructs;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.math.BlockPos;

public class RocketPart {
    public PartType partType;
    PartMaterial material;
    public float radius;
    public float mass;
    public float volatility;

    //Optional
    public float maxPayloadCapacity;
    public FuelComponent fuelComponent;
    public float power;

    //For when I'm in a RocketEntity
    public BlockState block;
    public BlockPos offset;

    private RocketPart(RocketPartBuilder builder) {
        this.partType = builder.partType;
        this.material = builder.material;
        this.radius = builder.radius;
        this.mass = builder.mass;
        this.volatility = builder.volatility;

        this.maxPayloadCapacity = builder.maxPayloadCapacity;
        this.fuelComponent = builder.fuelComponent;
        this.power = builder.power;
    }

    public RocketPart(PartType partType, PartMaterial material, float radius, float mass, float volatility, float maxPayloadCapacity, FuelComponent fuelComponent, float power) {
        this.partType = partType;
        this.material = material;
        this.radius = radius;
        this.mass = mass;
        this.volatility = volatility;

        this.maxPayloadCapacity = maxPayloadCapacity;
        this.fuelComponent = fuelComponent;
        this.power = power;
    }

    public RocketPart(PartType partType, PartMaterial material, float radius, float mass, float volatility, float maxPayloadCapacity, FuelComponent fuelComponent, float power, BlockState state, BlockPos offset) {
        this(partType, material, radius, mass, volatility, maxPayloadCapacity, fuelComponent, power);
        this.block = state;
        this.offset = offset;
    }

    public RocketPart setBlock(BlockState Block, BlockPos offset) {
        this.block = Block;
        this.offset = offset;
        return this;
    }

    public NbtCompound toNbt(){
        NbtCompound nbt = new NbtCompound();

        nbt.putString("Type", partType.toString());
        nbt.putString("PartMaterial", material.toString());
        nbt.putFloat("Radius", radius);
        nbt.putFloat("Mass", mass);
        nbt.putFloat("Volatility", volatility);

        nbt.putFloat("MaxPayloadCapacity", maxPayloadCapacity);
        if(fuelComponent != null){
            nbt.put("fuelComponent", fuelComponent.toNbt());
        }
        nbt.putFloat("Power", power);

        if(offset != null){
            nbt.putLong("Offset", offset.asLong());
        }

        if(block != null){
            DataResult<NbtElement> encodedState = BlockState.CODEC.encodeStart(NbtOps.INSTANCE, block);
            NbtElement stateNbt = encodedState.result().get();
            nbt.put("BlockState", stateNbt);
        }

        return nbt;
    }

    public static RocketPart fromNbt(NbtCompound nbt){
        PartType partType = PartType.valueOf(nbt.getString("Type"));
        PartMaterial material = PartMaterial.valueOf(nbt.getString("PartMaterial"));
        float radius = nbt.getFloat("Radius");
        float mass = nbt.getFloat("Mass");
        float volatility = nbt.getFloat("Volatility");

        float maxPayloadCapacity = nbt.getFloat("MaxPayloadCapacity");
        FuelComponent fuelComponent = nbt.contains("fuelComponent") ? FuelComponent.fromNbt(nbt.getCompound("fuelComponent")) : null;
        float power = nbt.getFloat("Power");

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
            return new RocketPart(partType, material, radius, mass, volatility, maxPayloadCapacity, fuelComponent, power, state, Offset);
        } else {
            return new RocketPart(partType, material, radius, mass, volatility, maxPayloadCapacity, fuelComponent, power);
        }
    }

    public RocketPart copy(){
        return new RocketPart(partType, material, radius, mass, volatility, maxPayloadCapacity, fuelComponent, power, block, offset);
    }

    public enum PartType {
        NOSE,
        BODY,
        EXHAUST
    }

    /**
     * Gets the material that this part is made of
     * @return This Rocket Parts 'material' value
     */
    public PartMaterial getMaterial() {
        return material;
    }

    public static class RocketPartBuilder {
        private final PartType partType;
        private final PartMaterial material;
        private final float radius;
        private final float mass;
        private final float volatility;

        private float maxPayloadCapacity;
        private FuelComponent fuelComponent;
        private float power;

        public RocketPartBuilder(PartType type, PartMaterial material, float radius, float mass, float volatility) { //setting these manually for now, will be based off of fuel later.
            this.partType = type;
            this.radius = radius;
            this.mass = mass;
            this.volatility = volatility;
            this.material = material;

            this.maxPayloadCapacity = 0;
            this.fuelComponent = null;
            this.power = 0;
        }

        public RocketPartBuilder setPayloadCapacity(float capacity) {
            this.maxPayloadCapacity = capacity;
            return this;
        }

        public RocketPartBuilder addFuelComponent(FuelComponent fuelComponent) {
            this.fuelComponent = fuelComponent;
            return this;
        }

        public RocketPartBuilder setPower(float power) {
            this.power = power;
            return this;
        }

        public RocketPart build() {
            return new RocketPart(this);
        }
    }
}
