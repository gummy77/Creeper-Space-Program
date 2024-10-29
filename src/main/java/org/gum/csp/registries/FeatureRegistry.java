package org.gum.csp.registries;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import org.gum.csp.CspMain;
import org.gum.csp.feature.OilPoolFeature;

import java.util.List;

public class FeatureRegistry {
    private static final ConfiguredFeature<?, ?> OVERWORLD_SHALE_OIL_CONFIGURED_FEATURE;
    private static final PlacedFeature OVERWORLD_SHALE_OIL_PLACED_FEATURE;

    private static final OilPoolFeature OIL_POOL_FEATURE;
    private static final ConfiguredFeature<?, ?> OIL_POOL_CONFIGURED_FEATURE;
    private static final PlacedFeature OIL_POOL_PLACED_FEATURE;

    public static void registerFeatures() {
        registerFeature("overworld_shale_oil_ore",
                OVERWORLD_SHALE_OIL_CONFIGURED_FEATURE,
                OVERWORLD_SHALE_OIL_PLACED_FEATURE,
                GenerationStep.Feature.UNDERGROUND_ORES
        );

        registerFeature("overworld_shale_oil_pool",
                OIL_POOL_FEATURE,
                OIL_POOL_CONFIGURED_FEATURE,
                OIL_POOL_PLACED_FEATURE,
                GenerationStep.Feature.RAW_GENERATION);
    }

    public static void registerFeature(String path, ConfiguredFeature<?, ?> configuredFeature, PlacedFeature placedFeature, GenerationStep.Feature step) {
        Identifier id = new Identifier(CspMain.MODID, path);

        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, id, configuredFeature);
        Registry.register(BuiltinRegistries.PLACED_FEATURE, id, placedFeature);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), step, RegistryKey.of(Registry.PLACED_FEATURE_KEY, id));
    }
    public static void registerFeature(String path, Feature<?> feature, ConfiguredFeature<?, ?> configuredFeature, PlacedFeature placedFeature, GenerationStep.Feature step) {
        Identifier id = new Identifier(CspMain.MODID, path);

        Registry.register(Registry.FEATURE, id, feature);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, id, configuredFeature);
        Registry.register(BuiltinRegistries.PLACED_FEATURE, id, placedFeature);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), step, RegistryKey.of(Registry.PLACED_FEATURE_KEY, id));
    }

    static {
        OVERWORLD_SHALE_OIL_CONFIGURED_FEATURE = new ConfiguredFeature<>(
                Feature.ORE, new OreFeatureConfig(
                        OreConfiguredFeatures.STONE_ORE_REPLACEABLES,
                BlockRegistry.SHALE_OIL_ORE.getDefaultState(),
                9));

        OVERWORLD_SHALE_OIL_PLACED_FEATURE = new PlacedFeature(
                RegistryEntry.of(OVERWORLD_SHALE_OIL_CONFIGURED_FEATURE),
                List.of(
                        CountPlacementModifier.of(20),
                        SquarePlacementModifier.of(),
                        HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(64))
                ));

        OIL_POOL_FEATURE = new OilPoolFeature(OilPoolFeature.OilPoolFeatureConfig.CODEC);

        OIL_POOL_CONFIGURED_FEATURE = new ConfiguredFeature<>(
                OIL_POOL_FEATURE,
                new OilPoolFeature.OilPoolFeatureConfig(
                        8, Identifier.of(CspMain.MODID, "oil")
                )
        );

        OIL_POOL_PLACED_FEATURE = new PlacedFeature(
                RegistryEntry.of(OIL_POOL_CONFIGURED_FEATURE),
                List.of(
                        SquarePlacementModifier.of()
                )
        );
    }
}
