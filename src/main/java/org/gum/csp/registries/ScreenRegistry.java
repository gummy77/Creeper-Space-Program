package org.gum.csp.registries;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.gum.csp.CspMain;


import java.awt.*;

public class ScreenRegistry {



    public static void registerScreens() {
    }

    public static ScreenHandlerType<? extends ScreenHandler> register (String path, ScreenHandlerType.Factory<?> screen) {
        return Registry.register(Registry.SCREEN_HANDLER, Identifier.of(CspMain.MODID, path), new ScreenHandlerType<>(screen));
    }

    static {

    }
}
