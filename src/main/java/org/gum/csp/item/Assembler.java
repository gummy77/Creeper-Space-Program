package org.gum.csp.item;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import org.gum.csp.entity.RocketPartBlockEntity;
import org.gum.csp.registries.NetworkingConstants;
import org.gum.csp.registries.SoundRegistry;

public class Assembler extends Item {
    public Assembler(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockEntity blockEntity = context.getWorld().getBlockEntity(context.getBlockPos());

        if(blockEntity instanceof RocketPartBlockEntity rocketPartBlockEntity) {

            if (!context.getWorld().isClient && context.getWorld() instanceof ServerWorld) {
                rocketPartBlockEntity.AssembleRocket(context.getPlayer());

            }
            context.getWorld().playSound(context.getPlayer(), blockEntity.getPos(), SoundRegistry.BASIC_ASSEMBLE, SoundCategory.BLOCKS, 1, 1);

            context.getPlayer().getStackInHand(context.getHand()).damage(1, context.getPlayer(), (e) -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));

            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }
}
