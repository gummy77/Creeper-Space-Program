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

            //----- WOODEN -----
            if(stack.isOf(ItemRegistry.WOODEN_NOSE_CONE)) registerPart(stack,
                    new RocketPart.RocketPartBuilder(RocketPart.PartType.NOSE, PartMaterial.WOOD, 6f,2f, 0f)
                            .setPayloadCapacity(3f)
                            .build());

            if(stack.isOf(ItemRegistry.WOODEN_BODY_SEGMENT)) registerPart(stack,
                    new RocketPart.RocketPartBuilder(RocketPart.PartType.BODY, PartMaterial.WOOD, 6f,3f, 2.5f)
                            .addFuelComponent(new FuelComponent(FuelComponent.FuelType.SOLID, 10f, 1f, 1f))
                            .build());

            if(stack.isOf(ItemRegistry.WOODEN_EXHAUST)) registerPart(stack,
                    new RocketPart.RocketPartBuilder(RocketPart.PartType.EXHAUST, PartMaterial.WOOD, 6f,3, 10f)
                            .setPower(250f)
                            .build());


            //----- BAMBOO -----
            if(stack.isOf(ItemRegistry.BAMBOO_NOSE_CONE)) registerPart(stack,
                    new RocketPart.RocketPartBuilder(RocketPart.PartType.NOSE, PartMaterial.BAMBOO, 5f,2f, 0f)
                            .setPayloadCapacity(2f)
                            .build());

            if(stack.isOf(ItemRegistry.BAMBOO_BODY_SEGMENT)) registerPart(stack,
                    new RocketPart.RocketPartBuilder(RocketPart.PartType.BODY, PartMaterial.BAMBOO, 4f,2f, 3f)
                            .addFuelComponent(new FuelComponent(FuelComponent.FuelType.SOLID, 9f, 1f, 1f))
                            .build());

            if(stack.isOf(ItemRegistry.BAMBOO_EXHAUST)) registerPart(stack,
                    new RocketPart.RocketPartBuilder(RocketPart.PartType.EXHAUST, PartMaterial.BAMBOO, 5f,2f, 12.5f)
                            .setPower(200f)
                            .build());


            //----- COPPER -----
            if(stack.isOf(ItemRegistry.COPPER_NOSE_CONE)) registerPart(stack,
                    new RocketPart.RocketPartBuilder(RocketPart.PartType.NOSE, PartMaterial.COPPER, 8f,50f, 0f)
                            .setPayloadCapacity(50f)
                            .build());
            if(stack.isOf(ItemRegistry.COPPER_BODY_SEGMENT)) registerPart(stack,
                    new RocketPart.RocketPartBuilder(RocketPart.PartType.BODY, PartMaterial.COPPER, 8f,50f, 2f)
                            .addFuelComponent(new FuelComponent(FuelComponent.FuelType.SOLID, 25f, 1f, 1f))
                            .build());
            if(stack.isOf(ItemRegistry.COPPER_EXHAUST)) registerPart(stack,
                    new RocketPart.RocketPartBuilder(RocketPart.PartType.EXHAUST, PartMaterial.COPPER, 8f, 100f, 7.5f)
                            .setPower(5000f)
                            .build());
        });
    }

    private static void registerPart(ItemStack stack, RocketPart part) {
        NbtCompound nbt = new NbtCompound();
        nbt.put("rocketPart", part.toNbt());
        stack.setSubNbt("BlockEntityTag", nbt);
    }
}