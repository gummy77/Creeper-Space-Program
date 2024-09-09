package org.gum.csp.registries;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.gum.csp.CspMain;

public class ItemRegistry {

    public static final Item DEFAULT_ITEM;

    public static final ItemGroup MODGROUP = FabricItemGroupBuilder.create(
                    new Identifier(CspMain.MODID, "cspmodgroup"))
            .icon(() -> new ItemStack(Items.TROPICAL_FISH))
            .build();

    public static void registerItems() {
    }

    public static Item register (String path, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(CspMain.MODID, path), item);
    }

    public static Item.Settings getSettings(){
        return new FabricItemSettings().group(MODGROUP);
    }

    static {
        DEFAULT_ITEM = register("default_item", new Item(getSettings()));
    }
}
