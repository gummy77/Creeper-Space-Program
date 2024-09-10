package org.gum.csp.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.gum.csp.entity.RocketEntity;

public class LaunchKit extends Item {
    public LaunchKit(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        stack.setDamage(stack.getDamage() + 1);
        if(stack.getDamage() > stack.getMaxDamage()){
            stack.decrement(1);
        }

        return super.finishUsing(stack, world, user);
    }
}
