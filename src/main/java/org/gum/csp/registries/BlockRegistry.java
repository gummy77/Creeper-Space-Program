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

    public static final Block SHALE_OIL_ORE;

    public static final Block BLAST_RESISTANT_STONE, HEAVY_BLAST_RESISTANT_STONE;

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
        BLAST_RESISTANT_STONE = register("blast_resistant_stone", new Block(FabricBlockSettings.of(Material.STONE).resistance(9).hardness(5)));
        HEAVY_BLAST_RESISTANT_STONE = register("heavy_blast_resistant_stone", new Block(FabricBlockSettings.of(Material.STONE).resistance(15).hardness(5)));

        SHALE_OIL_ORE = register("shale_oil_ore", new Block(FabricBlockSettings.of(Material.STONE).hardness(5)));

        //Wooden
        WOODEN_NOSE_CONE = (RocketPartBlock) register("rocket/wooden/nose_cone",new RocketPartBlock(FabricBlockSettings.of(Material.WOOD).hardness(2)));
        WOODEN_BODY_SEGMENT = (RocketPartBlock) register("rocket/wooden/body_segment", new RocketPartBlock(FabricBlockSettings.of(Material.WOOD).hardness(2)));
        WOODEN_EXHAUST = (RocketPartBlock) register("rocket/wooden/exhaust", new RocketPartBlock(FabricBlockSettings.of(Material.WOOD).hardness(2)));

        //Bamboo
        BAMBOO_NOSE_CONE = (RocketPartBlock) register("rocket/bamboo/nose_cone",new RocketPartBlock(FabricBlockSettings.of(Material.BAMBOO).hardness(1)));
        BAMBOO_BODY_SEGMENT = (RocketPartBlock) register("rocket/bamboo/body_segment", new RocketPartBlock(FabricBlockSettings.of(Material.BAMBOO).hardness(1)));
        BAMBOO_EXHAUST = (RocketPartBlock) register("rocket/bamboo/exhaust", new RocketPartBlock(FabricBlockSettings.of(Material.BAMBOO).hardness(1)));

        //Copper
        COPPER_NOSE_CONE = (RocketPartBlock) register("rocket/copper/nose_cone",new RocketPartBlock(FabricBlockSettings.of(Material.METAL).hardness(3)));
        COPPER_BODY_SEGMENT = (RocketPartBlock) register("rocket/copper/body_segment", new RocketPartBlock(FabricBlockSettings.of(Material.METAL).hardness(3)));
        COPPER_EXHAUST = (RocketPartBlock) register("rocket/copper/exhaust", new RocketPartBlock(FabricBlockSettings.of(Material.METAL).hardness(3)));

        ROCKET_PART_BLOCK_ENTITY = registerEntity("rocket_part_block_entity", FabricBlockEntityTypeBuilder
                .create(RocketPartBlockEntity::new,
                        WOODEN_NOSE_CONE, WOODEN_BODY_SEGMENT, WOODEN_EXHAUST,
                        BAMBOO_NOSE_CONE, BAMBOO_BODY_SEGMENT, BAMBOO_EXHAUST,
                        COPPER_NOSE_CONE, COPPER_BODY_SEGMENT, COPPER_EXHAUST
                ).build());
    }
}
