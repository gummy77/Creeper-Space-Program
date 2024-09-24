package org.gum.csp.registries;

import net.minecraft.advancement.criterion.Criteria;
import org.gum.csp.advancements.WoodRocketAdvancementCriterion;

import static org.gum.csp.CspMain.MODID;

public class AdvancementRegistry {

    public static WoodRocketAdvancementCriterion WOOD_ROCKET_LAUNCHED;

    /**
     * Static method that exists only to initialise static values on the class
     */
    public static void registerAdvancements() {}

    static {
        WOOD_ROCKET_LAUNCHED = Criteria.register(new WoodRocketAdvancementCriterion());
    }
}
