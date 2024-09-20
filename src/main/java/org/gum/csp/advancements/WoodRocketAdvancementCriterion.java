package org.gum.csp.advancements;


import com.google.gson.JsonObject;
import net.minecraft.server.network.ServerPlayerEntity;
import org.gum.csp.CspMain;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class WoodRocketAdvancementCriterion extends AbstractCriterion<WoodRocketAdvancementCriterion.Conditions> {
    private static final Identifier ID = new Identifier(CspMain.MODID, "wood_rocket_advancement");

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return new Conditions();
    }

    public static class Conditions extends AbstractCriterionConditions {
        public Conditions() {
            super(ID, EntityPredicate.Extended.EMPTY);
        }

        boolean requirementsMet() {
            return true;
        }
    }

    public void trigger(ServerPlayerEntity player) {
        trigger(player, Conditions::requirementsMet);
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
