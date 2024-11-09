package org.gum.csp;

import net.fabricmc.api.ModInitializer;
import org.gum.csp.registries.*;

public class CspMain implements ModInitializer {
    public static final String MODID = "csp";

    @Override
    public void onInitialize() {
        FluidRegistry.registerFluids();
        BlockRegistry.registerBlocks();
        EntityRegistry.registerEntities();
        EntityRegistry.registerEntityAttributes();
        ItemRegistry.registerItems();
        PayloadRegistry.registerPayloads();
        ScreenRegistry.registerScreens();
        AdvancementRegistry.registerAdvancements();
        SoundRegistry.registerSounds();
        FeatureRegistry.registerFeatures();
        RocketPartRegistry.registerParts();
        RecipeRegistry.registerRecipes();
    }
}
