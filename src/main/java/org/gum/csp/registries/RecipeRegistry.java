package org.gum.csp.registries;


import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.gum.csp.CspMain;
import org.gum.csp.recipe.SolidFuelPressRecipe;

public class RecipeRegistry {

    public static final RecipeType<SolidFuelPressRecipe> FUEL_PRESS_RECIPE_TYPE;
    public static final RecipeSerializer<SolidFuelPressRecipe> FUEL_PRESS_SERIALIZER;

    public static void registerRecipes() {

    }

    static {
        FUEL_PRESS_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, SolidFuelPressRecipe.Serializer.ID, SolidFuelPressRecipe.Serializer.INSTANCE);
        FUEL_PRESS_RECIPE_TYPE = Registry.register(Registry.RECIPE_TYPE, new Identifier(CspMain.MODID, SolidFuelPressRecipe.Type.ID), SolidFuelPressRecipe.Type.INSTANCE);
    }
}
