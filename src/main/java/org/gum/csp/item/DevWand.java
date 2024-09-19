package org.gum.csp.item;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import org.gum.csp.entity.RocketPartBlockEntity;

public class DevWand extends Item {
    public DevWand(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockEntity blockEntity = context.getWorld().getBlockEntity(context.getBlockPos());

        if(blockEntity instanceof RocketPartBlockEntity rocketPartBlockEntity) {

            if (!context.getWorld().isClient && context.getWorld() instanceof ServerWorld) {
                // block on server
            } else {
                // block on client
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }
}