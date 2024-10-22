package org.gum.csp.registries;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
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
    public static Item AMOEBA;

    public static final Item STARDUST_STEEL;
    public static final Item ELECTRONIC_CIRCUIT;
    public static final Item WOODEN_FIN, BAMBOO_FIN, COPPER_FIN;
    public static final Item IRON_NOZZLE;

    public static final PayloadItem DEFAULT_PAYLOAD_ITEM, RAIN_STARTER_ITEM, STARDUST_COLLECTOR_ITEM, SPECIMEN_RETURN_CAPSULE;

    public static Item WOODEN_NOSE_CONE, WOODEN_BODY_SEGMENT, WOODEN_EXHAUST;
    public static Item BAMBOO_NOSE_CONE, BAMBOO_BODY_SEGMENT, BAMBOO_EXHAUST;
    public static Item COPPER_NOSE_CONE, COPPER_BODY_SEGMENT, COPPER_EXHAUST;

    public static Item STARDUST_BASIC, STARDUST_LEVEL_2, STARDUST_LEVEL_3, STARDUST_LEVEL_4, STARDUST_LEVEL_5;

    public static final ItemGroup MODGROUP = FabricItemGroupBuilder.create(
                    new Identifier(CspMain.MODID, "cspmodgroup"))
            .icon(() -> new ItemStack(Items.FIREWORK_ROCKET))
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
        ADRIAN = register("adrian", new Item(new FabricItemSettings().food(new FoodComponent.Builder().hunger(10).build())));
        DEV_WAND = register("dev_wand", new Item(new FabricItemSettings()));

        LAUNCH_KIT = (LaunchKit) register("launchkit", new LaunchKit(getSettings().maxDamage(10)));
        FUSE = register("fuse", new Item(getSettings()));
        ASSEMBLER = (Assembler) register("basic_assembler", new Assembler(getSettings().maxDamage(10)));
        PAYLOAD_COMPASS = (PayloadTrackingCompass) register("payload_compass", new PayloadTrackingCompass(getSettings()));
        ROCKET_INSPECTOR = register("rocket_inspector", new PayloadTrackingCompass(getSettings()));

        AMOEBA = register("amoeba", new Item(getSettings().food(new FoodComponent.Builder().hunger(0).build())));

        ELECTRONIC_CIRCUIT = register("electronic_circuit", new Item(getSettings()));
        STARDUST_STEEL = register("stardust_steel", new Item(getSettings()));

        WOODEN_FIN = register("wooden_fin", new Item(getSettings()));
        BAMBOO_FIN = register("bamboo_fin", new Item(getSettings()));
        COPPER_FIN = register("copper_fin", new Item(getSettings()));

        IRON_NOZZLE = register("iron_nozzle", new Item(getSettings()));

        DEFAULT_PAYLOAD_ITEM = (PayloadItem) register("payloads/default_payload", new PayloadItem(getSettings(), PayloadRegistry.Payloads.DEFAULT));
        RAIN_STARTER_ITEM = (PayloadItem) register("payloads/rain_starter_payload", new PayloadItem(getSettings(), PayloadRegistry.Payloads.RAIN_STARTER));
        STARDUST_COLLECTOR_ITEM = (PayloadItem) register("payloads/stardust_collector_payload", new PayloadItem(getSettings(), PayloadRegistry.Payloads.STARDUST));
        SPECIMEN_RETURN_CAPSULE = (PayloadItem) register("payloads/specimen_return_capsule_payload", new PayloadItem(getSettings(), PayloadRegistry.Payloads.SPECIMEN_RETURN_CAPSULE));

        WOODEN_NOSE_CONE = register("rocket/wooden/nose_cone", new RocketPartItem(BlockRegistry.WOODEN_NOSE_CONE, getSettings()));
        WOODEN_BODY_SEGMENT = register("rocket/wooden/body_segment", new RocketPartItem(BlockRegistry.WOODEN_BODY_SEGMENT, getSettings()));
        WOODEN_EXHAUST = register("rocket/wooden/exhaust", new RocketPartItem(BlockRegistry.WOODEN_EXHAUST, getSettings()));

        BAMBOO_NOSE_CONE = register("rocket/bamboo/nose_cone", new RocketPartItem(BlockRegistry.BAMBOO_NOSE_CONE, getSettings()));
        BAMBOO_BODY_SEGMENT = register("rocket/bamboo/body_segment", new RocketPartItem(BlockRegistry.BAMBOO_BODY_SEGMENT, getSettings()));
        BAMBOO_EXHAUST = register("rocket/bamboo/exhaust", new RocketPartItem(BlockRegistry.BAMBOO_EXHAUST, getSettings()));

        COPPER_NOSE_CONE = register("rocket/copper/nose_cone", new RocketPartItem(BlockRegistry.COPPER_NOSE_CONE, getSettings()));
        COPPER_BODY_SEGMENT = register("rocket/copper/body_segment", new RocketPartItem(BlockRegistry.COPPER_BODY_SEGMENT, getSettings()));
        COPPER_EXHAUST = register("rocket/copper/exhaust", new RocketPartItem(BlockRegistry.COPPER_EXHAUST, getSettings()));

        STARDUST_BASIC = register("stardust", new Item(getSettings()));
//        STARDUST_LEVEL_2 = register("stardust_purple", new Item(getSettings()));
//        STARDUST_LEVEL_3 = register("stardust_red", new Item(getSettings()));
//        STARDUST_LEVEL_4 = register("stardust_green_pink", new Item(getSettings()));
//        STARDUST_LEVEL_5 = register("stardust_rainbow", new Item(getSettings()));
    }
}
