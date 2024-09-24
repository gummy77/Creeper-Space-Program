package org.gum.csp;

import net.fabricmc.api.ModInitializer;
import org.gum.csp.registries.*;

public class CspMain implements ModInitializer {
    public static final String MODID = "csp";

    @Override
    public void onInitialize() {
        ItemRegistry.registerItems();
        BlockRegistry.registerBlocks();
        EntityRegistry.registerEntities();
        EntityRegistry.registerEntityAttributes();
        AdvancementRegistry.registerAdvancements();
        SoundRegistry.registerSounds();
    }
}
