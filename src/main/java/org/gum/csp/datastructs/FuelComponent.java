package org.gum.csp.datastructs;

public class FuelComponent {
    public FuelType fuelType;
    public float amount;
    public float burnSpeed;

    public FuelComponent(FuelType fuelType, float amount, float burnSpeed) {
        this.fuelType = fuelType;
        this.amount = amount;
        this.burnSpeed = burnSpeed;
    }

    public enum FuelType {
        SOLID,
        LIQUID
    }
}

