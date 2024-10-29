package org.gum.csp.registries;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.gum.csp.CspMain;
import org.gum.csp.fluid.OilFluid;
import org.gum.csp.fluid.OilFluidBlock;


public class FluidRegistry {
    public static final FlowableFluid STILL_OIL;
    public static final FlowableFluid FLOWING_OIL;

    public static final Block OIL;
    public static final Material OIL_MATERIAL;

    public static void registerFluids() {

    }

    public static FlowableFluid registerFluid(String path, FlowableFluid fluid) {
        return Registry.register(Registry.FLUID, new Identifier(CspMain.MODID, path), fluid);
    }

    public static Block registerFluidBlock(String path, FluidBlock block) {
        return Registry.register(Registry.BLOCK, new Identifier(CspMain.MODID, path), block);
    }

    static {
        OIL_MATERIAL = new Material.Builder(MapColor.BLACK).destroyedByPiston().replaceable().build();

        STILL_OIL = registerFluid("oil", new OilFluid.Still());
        FLOWING_OIL = registerFluid("flowing_oil", new OilFluid.Flowing());

        OIL = registerFluidBlock("oil", new OilFluidBlock(STILL_OIL, FabricBlockSettings.of(OIL_MATERIAL).strength(100.0F).dropsNothing()));

    }

}
