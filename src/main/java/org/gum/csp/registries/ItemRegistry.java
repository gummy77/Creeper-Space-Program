package org.gum.csp.registries;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.data.client.ModelProvider;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.registry.Registry;
import org.gum.csp.CspMain;
import org.gum.csp.item.*;

public class ItemRegistry {

    public static final Item ADRIAN;
    public static final Item DEV_WAND;

    public static final LaunchKit LAUNCH_KIT;
    public static final Item FUSE;
    public static final Assembler ASSEMBLER;
    public static final PayloadTrackingCompass PAYLOAD_COMPASS;
    public static final Item ROCKET_INSPECTOR;

    public static SpawnEggItem GNEEP_SPAWN_EGG;

    public static final Item WOODEN_FIN;
    public static final Item IRON_NOZZLE;

    public static final PayloadItem DEFAULT_PAYLOAD_ITEM;

    public static Item BASE_NOSE_CONE, BASE_BODY_SEGMENT, BASE_EXHAUST;

    public static Item WOODEN_NOSE_CONE, WOODEN_BODY_SEGMENT, WOODEN_EXHAUST;
    public static Item BAMBOO_NOSE_CONE, BAMBOO_BODY_SEGMENT, BAMBOO_EXHAUST;
    public static Item COPPER_NOSE_CONE, COPPER_BODY_SEGMENT, COPPER_EXHAUST;

    public static final ItemGroup MODGROUP = FabricItemGroupBuilder.create(
                    new Identifier(CspMain.MODID, "cspmodgroup"))
            .icon(() -> new ItemStack(Items.TROPICAL_FISH))
            .build();

    public static void registerItems() {
        GNEEP_SPAWN_EGG = (SpawnEggItem) register("gneep_spawn_egg", new SpawnEggItem(EntityRegistry.GNEEP_ENTITY, 0xffffff, ColorHelper.Argb.getArgb(1, 0, 1, 0), getSettings()));
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
        ASSEMBLER = (Assembler) register("basic_assembler", new Assembler(getSettings().maxDamage(10)));
        PAYLOAD_COMPASS = (PayloadTrackingCompass) register("payload_compass", new PayloadTrackingCompass(getSettings()));
        ROCKET_INSPECTOR = register("rocket_inspector", new PayloadTrackingCompass(getSettings()));

        WOODEN_FIN = register("wooden_fin", new Item(getSettings()));
        IRON_NOZZLE = register("iron_nozzle", new Item(getSettings()));

        DEFAULT_PAYLOAD_ITEM = (PayloadItem) register("default_payload", new PayloadItem(getSettings(), PayloadRegistry.PAYLOADS.DEFAULT));

        BASE_NOSE_CONE = register("base_nose_cone", new BlockItem(BlockRegistry.BASE_NOSE_CONE, getSettings()));
        BASE_BODY_SEGMENT = register("base_body_segment", new BlockItem(BlockRegistry.BASE_BODY_SEGMENT, getSettings()));
        BASE_EXHAUST = register("base_exhaust", new BlockItem(BlockRegistry.BASE_EXHAUST, getSettings()));

        WOODEN_NOSE_CONE = register("wooden_rocket/nose_cone", new BlockItem(BlockRegistry.WOODEN_NOSE_CONE, getSettings()));
        WOODEN_BODY_SEGMENT = register("wooden_rocket/body_segment", new BlockItem(BlockRegistry.WOODEN_BODY_SEGMENT, getSettings()));
        WOODEN_EXHAUST = register("wooden_rocket/exhaust", new BlockItem(BlockRegistry.WOODEN_EXHAUST, getSettings()));

        BAMBOO_NOSE_CONE = register("bamboo_rocket/nose_cone", new BlockItem(BlockRegistry.BAMBOO_NOSE_CONE, getSettings()));
        BAMBOO_BODY_SEGMENT = register("bamboo_rocket/body_segment", new BlockItem(BlockRegistry.BAMBOO_BODY_SEGMENT, getSettings()));
        BAMBOO_EXHAUST = register("bamboo_rocket/exhaust", new BlockItem(BlockRegistry.BAMBOO_EXHAUST, getSettings()));

        COPPER_NOSE_CONE = register("copper_rocket/nose_cone", new BlockItem(BlockRegistry.COPPER_NOSE_CONE, getSettings()));
        COPPER_BODY_SEGMENT = register("copper_rocket/body_segment", new BlockItem(BlockRegistry.COPPER_BODY_SEGMENT, getSettings()));
        COPPER_EXHAUST = register("copper_rocket/exhaust", new BlockItem(BlockRegistry.COPPER_EXHAUST, getSettings()));

        ADRIAN = register("adrian", new Item(getSettings().food(new FoodComponent.Builder().hunger(10).build())));
        DEV_WAND = register("dev_wand", new Item(getSettings()));
    }
}
