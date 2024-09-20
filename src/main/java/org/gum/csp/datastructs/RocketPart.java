package org.gum.csp.datastructs;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Unique;


public class RocketPart {
    public float Mass;
    public BlockState Block;
    public BlockPos offset;
    //public FuelComponent fuelComponent;

    //Calculated
    public float Volatility;
    public float Power;

    public RocketPart(float Mass, float Volatility, float Power) { //setting these manually for now, will be based off of fuel later.
        this.Mass = Mass;
        this.Volatility = Volatility;
        this.Power = Power;
        //this.fuelComponent = fuelComponent;
    }

    public RocketPart(float Mass, float Volatility, float Power, BlockState Block) { //setting these manually for now, will be based off of fuel later.
        this.Mass = Mass;
        this.Volatility = Volatility;
        this.Power = Power;
        this.Block = Block;
        //this.fuelComponent = fuelComponent;
    }

    public RocketPart(float Mass, float Volatility, float Power, BlockState state, BlockPos offset) { //setting these manually for now, will be based off of fuel later.
        this.Mass = Mass;
        this.Volatility = Volatility;
        this.Power = Power;
        this.Block = state;
        this.offset = offset;
        //this.fuelComponent = fuelComponent;
    }

    public RocketPart setBlock(BlockState Block, BlockPos offset) {
        this.Block = Block;
        this.offset = offset;
        return this;
    }

    public RocketPart copy(){
        return new RocketPart(Mass, Volatility, Power, Block, offset);
    }
}
