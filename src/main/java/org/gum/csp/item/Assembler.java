package org.gum.csp.item;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.gum.csp.entity.RocketEntity;
import org.gum.csp.entity.RocketPartBlockEntity;

import java.util.List;

public class Assembler extends Item {
    public Assembler(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockEntity blockEntity = context.getWorld().getBlockEntity(context.getBlockPos());

        if(blockEntity instanceof RocketPartBlockEntity rocketPartBlockEntity) {

            //rocketPartBlockEntity.AssembleRocket();

            context.getPlayer().getStackInHand(context.getHand()).damage(1, context.getPlayer(), (e) -> {
                e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
            });

            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }
}
