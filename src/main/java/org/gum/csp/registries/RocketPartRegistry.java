package org.gum.csp.registries;

import net.fabricmc.fabric.api.item.v1.ModifyItemAttributeModifiersCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.gum.csp.datastructs.FuelComponent;
import org.gum.csp.datastructs.PartMaterial;
import org.gum.csp.datastructs.RocketPart;

public class RocketPartRegistry {
    public static void registerParts(){
        ModifyItemAttributeModifiersCallback.EVENT.register((stack, slot, attributeModifiers) -> {

            //----- BASE -----
            if(stack.isOf(ItemRegistry.BASE_NOSE_CONE)) registerPart(stack,
                    new RocketPart.RocketPartBuilder(RocketPart.PartType.NOSE, PartMaterial.BASE, 6f, 2f, 0f)
                            .build());
            if(stack.isOf(ItemRegistry.BASE_BODY_SEGMENT)) registerPart(stack,
                    new RocketPart.RocketPartBuilder(RocketPart.PartType.BODY, PartMaterial.BASE, 6f,3f, 0f)
                            .addFuelComponent(new FuelComponent(FuelComponent.FuelType.SOLID, 100f, 1.5f, 1f))
                            .build());
            if(stack.isOf(ItemRegistry.BASE_EXHAUST)) registerPart(stack,
                    new RocketPart.RocketPartBuilder(RocketPart.PartType.EXHAUST, PartMaterial.BASE, 6f,5f, 0f)
                            .setPower(45f)
                            .build());


            //----- WOODEN -----
            if(stack.isOf(ItemRegistry.WOODEN_NOSE_CONE)) registerPart(stack,
                    new RocketPart.RocketPartBuilder(RocketPart.PartType.NOSE, PartMaterial.WOOD, 6f,4f, 0f)
                            .setPayloadCapacity(3f)
                            .build());

            if(stack.isOf(ItemRegistry.WOODEN_BODY_SEGMENT)) registerPart(stack,
                    new RocketPart.RocketPartBuilder(RocketPart.PartType.BODY, PartMaterial.WOOD, 6f,5f, 2.5f)
                            .addFuelComponent(new FuelComponent(FuelComponent.FuelType.SOLID, 80f, 1f, 1f))
                            .build());

            if(stack.isOf(ItemRegistry.WOODEN_EXHAUST)) registerPart(stack,
                    new RocketPart.RocketPartBuilder(RocketPart.PartType.EXHAUST, PartMaterial.WOOD, 6f,6, 10f)
                            .setPower(32.5f)
                            .build());


            //----- BAMBOO -----
            if(stack.isOf(ItemRegistry.BAMBOO_NOSE_CONE)) registerPart(stack,
                    new RocketPart.RocketPartBuilder(RocketPart.PartType.NOSE, PartMaterial.BAMBOO, 5f,1.5f, 0f)
                            .setPayloadCapacity(3f)
                            .build());

            if(stack.isOf(ItemRegistry.BAMBOO_BODY_SEGMENT)) registerPart(stack,
                    new RocketPart.RocketPartBuilder(RocketPart.PartType.BODY, PartMaterial.BAMBOO, 4f,3f, 3f)
                            .addFuelComponent(new FuelComponent(FuelComponent.FuelType.SOLID, 75f, 1f, 1f))
                            .build());

            if(stack.isOf(ItemRegistry.BAMBOO_EXHAUST)) registerPart(stack,
                    new RocketPart.RocketPartBuilder(RocketPart.PartType.EXHAUST, PartMaterial.BAMBOO, 5f,3.5f, 12.5f)
                            .setPower(22.5f)
                            .build());


            //----- COPPER -----
            if(stack.isOf(ItemRegistry.COPPER_NOSE_CONE)) registerPart(stack,
                    new RocketPart.RocketPartBuilder(RocketPart.PartType.NOSE, PartMaterial.COPPER, 8f,6f, 0f)
                            .setPayloadCapacity(12.5f)
                            .build());
            if(stack.isOf(ItemRegistry.COPPER_BODY_SEGMENT)) registerPart(stack,
                    new RocketPart.RocketPartBuilder(RocketPart.PartType.BODY, PartMaterial.COPPER, 8f,7f, 2f)
                            .addFuelComponent(new FuelComponent(FuelComponent.FuelType.SOLID, 170f, 1f, 1f))
                            .build());
            if(stack.isOf(ItemRegistry.COPPER_EXHAUST)) registerPart(stack,
                    new RocketPart.RocketPartBuilder(RocketPart.PartType.EXHAUST, PartMaterial.COPPER, 8f, 8f, 7.5f)
                            .setPower(50f)
                            .build());
        });
    }

    private static void registerPart(ItemStack stack, RocketPart part) {
        NbtCompound nbt = new NbtCompound();
        nbt.put("rocketPart", part.toNbt());
        stack.setSubNbt("BlockEntityTag", nbt);
    }
}