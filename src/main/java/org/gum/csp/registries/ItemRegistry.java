package org.gum.csp.registries;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.gum.csp.CspMain;
import org.gum.csp.item.LaunchKit;
import org.gum.csp.item.Rocket;

public class ItemRegistry {

    public static final Item ADRIAN;
    public static final Rocket ROCKET;

    public static final LaunchKit LAUNCH_KIT;
    public static final Item FUSE;

    public static final Item TEST_ITEM;

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
        LAUNCH_KIT = (LaunchKit) register("launchkit", new LaunchKit(getSettings().maxDamage(10)));
        FUSE = register("fuse", new Item(getSettings()));

        ROCKET = (Rocket) register("rocket", new Rocket(getSettings()));
        TEST_ITEM = register("testblock", new BlockItem(BlockRegistry.TEST, getSettings()));
        ADRIAN = register("adrian", new Item(getSettings().food(new FoodComponent.Builder().hunger(10).build())));
    }
}
