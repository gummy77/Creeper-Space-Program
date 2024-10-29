package org.gum.csp.registries;

import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreConfiguredFeatures;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class FeatureRegistry {
    private static ConfiguredFeature<?, ?> OVERWORLD_SHALE_OIL_CONFIGURED_FEATURE;


    static {
        OVERWORLD_SHALE_OIL_CONFIGURED_FEATURE = new ConfiguredFeature(
                Feature.ORE, new OreFeatureConfig(
                OreConfiguredFeatures.STONE_ORE_REPLACEABLES,
                BlockRegistry.SHALE_OIL_ORE.getDefaultState(),
                9));
    }
}
