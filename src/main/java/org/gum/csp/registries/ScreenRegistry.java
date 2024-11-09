package org.gum.csp.registries;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.gum.csp.CspMain;
import org.gum.csp.screenhandler.SolidFuelPressScreenHandler;


import java.awt.*;

public class ScreenRegistry {

    public static final ScreenHandlerType<SolidFuelPressScreenHandler> SOLID_FUEL_PRESS_SCREEN_HANDLER_TYPE;

    public static void registerScreens() {

    }

    public static ScreenHandlerType<? extends ScreenHandler> register (String path, ScreenHandlerType.Factory<?> screen) {
        return Registry.register(Registry.SCREEN_HANDLER, Identifier.of(CspMain.MODID, path), new ScreenHandlerType<>(screen));
    }
    private static <T extends ScreenHandler> ScreenHandlerType<T> registerHandlerType(String id, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(Registry.SCREEN_HANDLER, id, new ScreenHandlerType<T>(factory));
    }


    static {
        SOLID_FUEL_PRESS_SCREEN_HANDLER_TYPE = registerHandlerType("solid_fuel_press", SolidFuelPressScreenHandler::new);
    }
}
