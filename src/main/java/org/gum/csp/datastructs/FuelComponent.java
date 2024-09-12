package org.gum.csp.datastructs;

public class FuelComponent {
    public FuelType fuelType;
    public float capacity;
    public float amount;
    public float burnSpeed;

    public FuelComponent(FuelType fuelType, float capacity, float amount, float burnSpeed) {
        this.fuelType = fuelType;
        this.capacity = capacity;
        this.amount = amount;
        this.burnSpeed = burnSpeed;
    }

    public enum FuelType {
        SOLID,
        LIQUID
    }
}

