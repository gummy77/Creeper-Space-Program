package org.gum.csp.datastructs;

/**
 * Enum Class to represent the Materials a rocket part can be
 */
public enum PartMaterial {
    NONE(-2, "None"),
    BASE(-1, "Base"),
    BAMBOO(0, "Bamboo"),
    WOOD(1, "Wooden"),
    COPPER(2, "Copper"),
    IRON(3, "Iron");

    private final int value;
    private final String formattedName;

    PartMaterial(int value, String formattedName) {
        this.value = value;
        this.formattedName = formattedName;
    }

    public int getValue() {
        return value;
    }

    public String getFormattedName() {
        return formattedName;
    }
}