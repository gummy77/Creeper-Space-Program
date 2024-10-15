package org.gum.csp.advancements;


import com.google.gson.JsonObject;
import net.minecraft.server.network.ServerPlayerEntity;
import org.gum.csp.CspMain;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.util.Identifier;
import org.gum.csp.datastructs.PartMaterial;
import org.gum.csp.datastructs.RocketSettings;

public class RocketAdvancementCriterion extends AbstractCriterion<RocketAdvancementCriterion.Conditions> {
    private final Identifier ID;

    private final PartMaterial material;

    public RocketAdvancementCriterion(PartMaterial materialTrigger, String name) {
        material = materialTrigger;
        ID = new Identifier(CspMain.MODID, name);
    }

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return new Conditions(playerPredicate, this);
    }

    public static class Conditions extends AbstractCriterionConditions {
        private final PartMaterial material;
        public Conditions(EntityPredicate.Extended playerPredicate, RocketAdvancementCriterion criterionInstance) {
            super(criterionInstance.ID, playerPredicate);
            material = criterionInstance.material;
        }

        boolean test(RocketSettings rocketSettings) {
            if (material == PartMaterial.NONE) {
                // THE ONLY TIME NONE SHOULD BE USED IS IF WE DON'T CARE ABOUT MATERIAL
                return true;
            }
            return rocketSettings.primaryMaterialsContains(material);
        }
    }

    public void trigger(ServerPlayerEntity player, RocketSettings settings) {
        trigger(player, conditions -> conditions.test(settings));
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
