package org.gum.csp.registries;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.gum.csp.CspMain;

public class BlockRegistry {

    public static Block TEST;

    public static void registerBlocks () {

    }

    public static Block register (String path, Block block) {
        return Registry.register(Registry.BLOCK, new Identifier(CspMain.MODID, path), block);
    }
    public static BlockEntityType<?> registerEntity (String path, BlockEntityType<?> type) {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(CspMain.MODID, path), type);
    }

    static {
        TEST = register("testblock", new Block(FabricBlockSettings.of(Material.METAL)));
    }
}
