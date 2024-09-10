package org.gum.csp;

import net.fabricmc.api.ModInitializer;
import org.gum.csp.registries.*;

public class CspMain implements ModInitializer {
    public static final String MODID = "csp";

    @Override
    public void onInitialize() {
        ItemRegistry.registerItems();
        EntityRegistry.registerEntities();
        EntityRegistry.registerEntityAttributes();

        BlockRegistry.registerBlocks();
    }
}
