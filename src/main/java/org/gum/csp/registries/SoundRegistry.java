package org.gum.csp.registries;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.gum.csp.CspMain;
import org.gum.csp.item.Assembler;
import org.gum.csp.item.LaunchKit;
import org.gum.csp.item.Rocket;

public class SoundRegistry {

    public static SoundEvent WOODEN_ROCKET_LAUNCH;

    public static void registerSounds() {
    }

    private static SoundEvent registerSound(String id) {
        Identifier identifier = Identifier.of(CspMain.MODID, id);
        return Registry.register(Registry.SOUND_EVENT, identifier, new SoundEvent(identifier));
    }

    static {
        WOODEN_ROCKET_LAUNCH = registerSound("wooden_rocket_launch");
    }
}
