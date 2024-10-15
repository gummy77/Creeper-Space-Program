package org.gum.csp.registries;

import net.minecraft.advancement.criterion.Criteria;
import org.gum.csp.advancements.RocketAdvancementCriterion;
import org.gum.csp.datastructs.PartMaterial;

public class AdvancementRegistry {

    public static RocketAdvancementCriterion BAMBOO_ROCKET_LAUNCHED;
    public static RocketAdvancementCriterion WOOD_ROCKET_LAUNCHED;
//    public static RocketAdvancementCriterion ANY_ROCKET_LAUNCHED;

    /**
     * Static method that exists only to initialise static values on the class
     */
    public static void registerAdvancements() {}

    static {
        BAMBOO_ROCKET_LAUNCHED = Criteria.register(new RocketAdvancementCriterion(PartMaterial.BAMBOO, "bamboo_rocket_advancement"));
        WOOD_ROCKET_LAUNCHED = Criteria.register(new RocketAdvancementCriterion(PartMaterial.WOOD, "wood_rocket_advancement"));
//        ANY_ROCKET_LAUNCHED = Criteria.register(new RocketAdvancementCriterion(PartMaterial.NONE, "any_rocket_advancement"));
    }
}
