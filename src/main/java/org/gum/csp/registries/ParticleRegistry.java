package org.gum.csp.registries;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.item.*;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.gum.csp.CspMain;
import org.gum.csp.item.LaunchKit;
import org.gum.csp.item.Rocket;

public class ParticleRegistry {

    //public static final ParticleType EXHAUST;
    public static final DefaultParticleType EXHAUST;

    public static void registerItems() {
    }

    public static DefaultParticleType register (String path, ParticleType<?> particle) {
        return (DefaultParticleType) Registry.register(Registry.PARTICLE_TYPE, new Identifier(CspMain.MODID, path), particle);
    }

    static {
        EXHAUST = register("exhaust", FabricParticleTypes.simple());;
    }
}
