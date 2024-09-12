package org.gum.csp.registries;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.gum.csp.CspMain;
import org.gum.csp.block.RocketPartBlock;
import org.gum.csp.datastructs.FuelComponent;
import org.gum.csp.datastructs.RocketPart;

public class BlockRegistry {

    public static RocketPartBlock WOODEN_NOSE_CONE;
    public static RocketPartBlock WOODEN_BODY_SEGMENT;
    public static RocketPartBlock WOODEN_EXHAUST;

    public static void registerBlocks () {

    }

    public static Block register (String path, Block block) {
        return Registry.register(Registry.BLOCK, new Identifier(CspMain.MODID, path), block);
    }
    public static BlockEntityType<?> registerEntity (String path, BlockEntityType<?> type) {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(CspMain.MODID, path), type);
    }

    static {
        WOODEN_NOSE_CONE = (RocketPartBlock) register("wooden_nose_cone",new RocketPartBlock(FabricBlockSettings.of(Material.WOOD),
                new RocketPart(1, 0, 0)));
        WOODEN_BODY_SEGMENT = (RocketPartBlock) register("wooden_body_segment", new RocketPartBlock(FabricBlockSettings.of(Material.WOOD),
                new RocketPart(2, 4, 0)));
        WOODEN_EXHAUST = (RocketPartBlock) register("wooden_exhaust", new RocketPartBlock(FabricBlockSettings.of(Material.WOOD),
                new RocketPart(2, 6, 2)));
    }
}
