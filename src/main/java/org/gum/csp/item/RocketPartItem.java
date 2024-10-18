package org.gum.csp.item;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.gum.csp.datastructs.RocketPart;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RocketPartItem extends BlockItem {

    public RocketPartItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        RocketPart part = null;

        if(stack.hasNbt()) {
            NbtCompound nbtCompound = stack.getSubNbt("BlockEntityTag");
            if(nbtCompound != null) {
                part = RocketPart.fromNbt(nbtCompound.getCompound("rocketPart"));

                //tooltip.add(Text.of("§7Type: §2" + part.getMaterial().getFormattedName()));
                tooltip.add(Text.of("§7Mass: §2" + part.mass + "kg"));

                switch (part.partType){
                    case NOSE:
                        tooltip.add(Text.of("§7Max Payload: §2" + part.maxPayloadCapacity + "kg"));
                        break;
                    case BODY:
                        tooltip.add(Text.of("§7Burn Time: §2" + (part.fuelComponent.capacity * part.fuelComponent.burnSpeed) + "s"));
                        tooltip.add(Text.of("§7Fuel Type: §2" + part.fuelComponent.fuelType.name()));
                        if(part.fuelComponent.burnPower != 1f)
                            tooltip.add(Text.of("§7§o  Power Mod: §2x" + part.fuelComponent.burnPower));

                        if(part.fuelComponent.burnSpeed != 1f)
                            tooltip.add(Text.of("§7§o  Speed Mod: §2x" + part.fuelComponent.burnSpeed));

                        break;
                    case EXHAUST:
                        tooltip.add(Text.of("§7Thrust: §2" + (part.power) + "N"));
                        break;
                }
            }
        }
    }
}
