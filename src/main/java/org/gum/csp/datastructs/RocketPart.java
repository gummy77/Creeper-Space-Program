package org.gum.csp.datastructs;

import net.minecraft.block.BlockState;

public class RocketPart {
    public float Mass;
    public BlockState Block;
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
}
