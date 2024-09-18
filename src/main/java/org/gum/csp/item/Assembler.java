package org.gum.csp.item;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import org.gum.csp.entity.RocketPartBlockEntity;

public class Assembler extends Item {
    public Assembler(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockEntity blockEntity = context.getWorld().getBlockEntity(context.getBlockPos());

        if(blockEntity instanceof RocketPartBlockEntity rocketPartBlockEntity) {

            rocketPartBlockEntity.AssembleRocket(blockEntity.getPos());

            context.getPlayer().getStackInHand(context.getHand()).damage(1, context.getPlayer(), (e) -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));

            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }
}
