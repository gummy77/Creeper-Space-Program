package org.gum.csp.datastructs;

/**
 * Enum Class to represent the Materials a rocket part can be
 */
public enum PartMaterial {
    NONE(-2, "None", 0, 0),
    BAMBOO(0, "Bamboo", 1, 5),
    WOOD(1, "Wooden", 1, 4),
    COPPER(2, "Copper", 1, 5),
    IRON(3, "Iron", 2, 5);

    private final int value;
    private final String formattedName;
    private final int minParts, maxParts;

    PartMaterial(int value, String formattedName, int minParts, int maxParts) {
        this.value = value;
        this.formattedName = formattedName;
        this.minParts = minParts;
        this.maxParts = maxParts;
    }

    public int getValue()       { return value; }
    public int getMinParts()    { return minParts; }
    public int getMaxParts()    { return maxParts; }

    public String getFormattedName() {
        return formattedName;
    }
}