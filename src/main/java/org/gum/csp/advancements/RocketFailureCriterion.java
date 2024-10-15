package org.gum.csp.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.gum.csp.CspMain;
import org.gum.csp.entity.RocketEntity;

public class RocketFailureCriterion extends AbstractCriterion<RocketFailureCriterion.Conditions> {
    private final Identifier ID;
    private ServerPlayerEntity playerLaunching;
    public RocketFailureCriterion(String name) {
        this.ID = new Identifier(CspMain.MODID, name);
    }

    @Override
    protected RocketFailureCriterion.Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return new Conditions(playerPredicate, this);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public static class Conditions extends AbstractCriterionConditions {
        public Conditions(EntityPredicate.Extended player, RocketFailureCriterion criterion) {
            super(criterion.ID, player);
        }

        boolean test(boolean launchWillFail) {
            return launchWillFail;
        }
    }

    public void trigger(boolean failedOnLaunch) {
        trigger(playerLaunching, conditions -> conditions.test(failedOnLaunch));
    }

    public void setPlayerLaunching(ServerPlayerEntity player) {
        this.playerLaunching = player;
    }
}
