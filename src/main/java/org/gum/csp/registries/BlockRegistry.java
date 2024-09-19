package org.gum.csp.registries;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.gum.csp.CspMain;
import org.gum.csp.block.RocketPartBlock;
import org.gum.csp.datastructs.RocketPart;
import org.gum.csp.entity.RocketPartBlockEntity;

public class BlockRegistry {

    public static BlockEntityType<RocketPartBlockEntity> ROCKET_PART_BLOCK_ENTITY;


    public static RocketPartBlock BASE_NOSE_CONE;
    public static RocketPartBlock BASE_BODY_SEGMENT;
    public static RocketPartBlock BASE_EXHAUST;

    public static RocketPartBlock WOODEN_NOSE_CONE;
    public static RocketPartBlock WOODEN_BODY_SEGMENT;
    public static RocketPartBlock WOODEN_EXHAUST;

    public static void registerBlocks () {

    }

    public static Block register (String path, Block block) {
        return Registry.register(Registry.BLOCK, new Identifier(CspMain.MODID, path), block);
    }
    public static <T extends BlockEntityType<?>> T registerEntity (String path, T type) {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(CspMain.MODID, path), type);
    }

    static {

        BASE_NOSE_CONE = (RocketPartBlock) register("base_nose_cone",new RocketPartBlock(FabricBlockSettings.of(Material.METAL),
                new RocketPart(1, 0, 0)));

        BASE_BODY_SEGMENT = (RocketPartBlock) register("base_body_segment", new RocketPartBlock(FabricBlockSettings.of(Material.METAL),
                new RocketPart(2, 4, 0)));

        BASE_EXHAUST = (RocketPartBlock) register("base_exhaust", new RocketPartBlock(FabricBlockSettings.of(Material.METAL),
                new RocketPart(2, 6, 2)));

        WOODEN_NOSE_CONE = (RocketPartBlock) register("wooden_nose_cone",new RocketPartBlock(FabricBlockSettings.of(Material.WOOD),
                new RocketPart(1, 0, 0)));

        WOODEN_BODY_SEGMENT = (RocketPartBlock) register("wooden_body_segment", new RocketPartBlock(FabricBlockSettings.of(Material.WOOD),
                new RocketPart(2, 4, 0)));

        WOODEN_EXHAUST = (RocketPartBlock) register("wooden_exhaust", new RocketPartBlock(FabricBlockSettings.of(Material.WOOD),
                new RocketPart(2, 6, 2)));

        ROCKET_PART_BLOCK_ENTITY = registerEntity("rocker_part_block_entity", FabricBlockEntityTypeBuilder
                .create(RocketPartBlockEntity::new,
                        WOODEN_NOSE_CONE, WOODEN_BODY_SEGMENT, WOODEN_EXHAUST
                ).build());
    }
}
