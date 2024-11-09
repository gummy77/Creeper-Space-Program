package org.gum.csp.item;

import com.google.common.collect.Multimap;
import com.sun.jna.platform.unix.X11;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.gum.csp.datastructs.RocketPart;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class RocketPartItem extends BlockItem {

    public RocketPartItem(Block block, Settings settings) {
        super(block, settings);


    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        return super.getTooltipData(stack);
    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        RocketPart part = null;

        if(stack.hasNbt()) {
            NbtCompound nbtCompound = stack.getSubNbt("BlockEntityTag");
            if(nbtCompound != null) {
                part = RocketPart.fromNbt(nbtCompound.getCompound("rocketPart"));

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
