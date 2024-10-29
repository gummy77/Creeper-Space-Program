package org.gum.csp.client.registries;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.gum.csp.registries.FluidRegistry;

public class FluidRenderRegistry {

    public static void registerFluids() {
        FluidRenderHandlerRegistry.INSTANCE.register(FluidRegistry.STILL_OIL, FluidRegistry.FLOWING_OIL,
                new SimpleFluidRenderHandler(
                        new Identifier("minecraft:block/water_still"),
                        new Identifier("minecraft:block/water_flow"),
                        0x4c4c4c
                ));

        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getSolid(), FluidRegistry.STILL_OIL, FluidRegistry.FLOWING_OIL);

        //ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
        //    registry.register(new Identifier("tutorial:block/custom_fluid_still"));
        //    registry.register(new Identifier("tutorial:block/custom_fluid_flowing"));
        //});
    }
}
