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

    public WoodRocketAdvancementCriterion() {
    }

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return new Conditions(playerPredicate);
    }

    public static class Conditions extends AbstractCriterionConditions {
        public Conditions(EntityPredicate.Extended playerPredicate) {
            super(ID, playerPredicate);
        }

        boolean test() { return true; }
    }

    public void trigger(ServerPlayerEntity player) {
        trigger(player, conditions -> conditions.test());
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
