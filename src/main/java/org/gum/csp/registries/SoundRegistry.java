package org.gum.csp.registries;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.gum.csp.CspMain;

public class SoundRegistry {

    public static SoundEvent WOODEN_ROCKET_LAUNCH;
    public static SoundEvent BASIC_ASSEMBLE;

    public static SoundEvent GNEEP_DEATH, GNEEP_HURT, GNEEP_IDLE;

    public static void registerSounds() {
    }

    private static SoundEvent registerSound(String id) {
        Identifier identifier = Identifier.of(CspMain.MODID, id);
        return Registry.register(Registry.SOUND_EVENT, identifier, new SoundEvent(identifier));
    }

    static {
        WOODEN_ROCKET_LAUNCH = registerSound("wooden_rocket_launch");
        BASIC_ASSEMBLE = registerSound("basic_assemble");

        GNEEP_DEATH = registerSound("gneep_death");
        GNEEP_HURT = registerSound("gneep_hurt");
        GNEEP_IDLE = registerSound("gneep_idle");
    }
}
