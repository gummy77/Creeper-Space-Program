package org.gum.csp.datastructs;

/**
 * Enum Class to represent the Materials a rocket part can be
 */
public enum PartMaterial {
    NONE(-2),
    BASE(-1),
    BAMBOO(0),
    WOOD(1),
    COPPER(2),
    IRON(3);

    private final int value;

    PartMaterial(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}