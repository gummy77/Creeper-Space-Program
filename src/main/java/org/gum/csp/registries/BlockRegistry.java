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
import org.gum.csp.datastructs.FuelComponent;
import org.gum.csp.datastructs.PartMaterial;
import org.gum.csp.datastructs.RocketPart;
import org.gum.csp.entity.RocketPartBlockEntity;
import org.spongepowered.asm.mixin.Mutable;

public class BlockRegistry {

    public static BlockEntityType<RocketPartBlockEntity> ROCKET_PART_BLOCK_ENTITY;


    public static RocketPartBlock BASE_NOSE_CONE;
    public static RocketPartBlock BASE_BODY_SEGMENT;
    public static RocketPartBlock BASE_EXHAUST;

    public static RocketPartBlock WOODEN_NOSE_CONE, WOODEN_BODY_SEGMENT, WOODEN_EXHAUST;

    public static RocketPartBlock BAMBOO_NOSE_CONE, BAMBOO_BODY_SEGMENT, BAMBOO_EXHAUST;

    public static RocketPartBlock COPPER_NOSE_CONE, COPPER_BODY_SEGMENT, COPPER_EXHAUST;

    public static void registerBlocks () {

    }

    public static Block register (String path, Block block) {
        return Registry.register(Registry.BLOCK, new Identifier(CspMain.MODID, path), block);
    }
    public static <T extends BlockEntityType<?>> T registerEntity (String path, T type) {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(CspMain.MODID, path), type);
    }

    static {
        //Basic

        BASE_NOSE_CONE = (RocketPartBlock) register("base_nose_cone",new RocketPartBlock(FabricBlockSettings.of(Material.METAL),
                new RocketPart(RocketPart.PartType.NOSE, 6f, 1, 1, 0, null)));

        BASE_BODY_SEGMENT = (RocketPartBlock) register("base_body_segment", new RocketPartBlock(FabricBlockSettings.of(Material.METAL),
                new RocketPart(
                        RocketPart.PartType.BODY, 6f,2, 2,0,
                        new FuelComponent(FuelComponent.FuelType.SOLID, 2, 1, 1)
                )));
        BASE_EXHAUST = (RocketPartBlock) register("base_exhaust", new RocketPartBlock(FabricBlockSettings.of(Material.METAL),
                new RocketPart(
                        RocketPart.PartType.EXHAUST, 6f,2, 2,3, null)));

        //Bamboo

        BAMBOO_NOSE_CONE = (RocketPartBlock) register("bamboo_rocket/nose_cone",new RocketPartBlock(FabricBlockSettings.of(Material.BAMBOO),
                new RocketPart(RocketPart.PartType.NOSE, PartMaterial.WOOD, 5f,0.5f, 0, 0,null)));

        BAMBOO_BODY_SEGMENT = (RocketPartBlock) register("bamboo_rocket/body_segment", new RocketPartBlock(FabricBlockSettings.of(Material.BAMBOO),
                new RocketPart(RocketPart.PartType.BODY, PartMaterial.WOOD, 4f,0.5f, 7.5f, 0,
                        new FuelComponent(FuelComponent.FuelType.SOLID, 1.5f, 1, 1))));

        BAMBOO_EXHAUST = (RocketPartBlock) register("bamboo_rocket/exhaust", new RocketPartBlock(FabricBlockSettings.of(Material.BAMBOO),
                new RocketPart(RocketPart.PartType.EXHAUST, PartMaterial.WOOD, 5f,0.5f, 25, 1, null)));

        //Wooden

        WOODEN_NOSE_CONE = (RocketPartBlock) register("wooden_rocket/nose_cone",new RocketPartBlock(FabricBlockSettings.of(Material.WOOD),
                new RocketPart(RocketPart.PartType.NOSE, PartMaterial.WOOD, 6f,1, 0, 0,null)));

        WOODEN_BODY_SEGMENT = (RocketPartBlock) register("wooden_rocket/body_segment", new RocketPartBlock(FabricBlockSettings.of(Material.WOOD),
                new RocketPart(RocketPart.PartType.BODY, PartMaterial.WOOD, 6f,2, 7.5f, 0,
                        new FuelComponent(FuelComponent.FuelType.SOLID, 2, 1, 1))));

        WOODEN_EXHAUST = (RocketPartBlock) register("wooden_rocket/exhaust", new RocketPartBlock(FabricBlockSettings.of(Material.WOOD),
                new RocketPart(RocketPart.PartType.EXHAUST, PartMaterial.WOOD, 6f,2, 25, 2, null)));

        //Wooden

        COPPER_NOSE_CONE = (RocketPartBlock) register("copper_rocket/nose_cone",new RocketPartBlock(FabricBlockSettings.of(Material.METAL),
                new RocketPart(RocketPart.PartType.NOSE, PartMaterial.WOOD, 8f,2, 0, 0,null)));

        COPPER_BODY_SEGMENT = (RocketPartBlock) register("copper_rocket/body_segment", new RocketPartBlock(FabricBlockSettings.of(Material.METAL),
                new RocketPart(RocketPart.PartType.BODY, PartMaterial.WOOD, 8f,3, 7.5f, 0,
                        new FuelComponent(FuelComponent.FuelType.SOLID, 3, 1, 1))));

        COPPER_EXHAUST = (RocketPartBlock) register("copper_rocket/exhaust", new RocketPartBlock(FabricBlockSettings.of(Material.METAL),
                new RocketPart(RocketPart.PartType.EXHAUST, PartMaterial.WOOD, 8f,3, 25, 4, null)));



        ROCKET_PART_BLOCK_ENTITY = registerEntity("rocket_part_block_entity", FabricBlockEntityTypeBuilder
                .create(RocketPartBlockEntity::new,
                        WOODEN_NOSE_CONE, WOODEN_BODY_SEGMENT, WOODEN_EXHAUST
                ).build());
    }
}
