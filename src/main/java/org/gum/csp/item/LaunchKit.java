package org.gum.csp.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.gum.csp.entity.RocketEntity;
import org.gum.csp.registries.SoundRegistry;

import java.util.Iterator;
import java.util.List;

public class LaunchKit extends Item {
    public LaunchKit(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return this.startLaunch(user, world, hand);
    }


    private TypedActionResult<ItemStack> startLaunch(PlayerEntity player, World world, Hand hand) {
        boolean bl = false;
        double d = 7.0;
        int i = player.getBlockX();
        int j = player.getBlockY();
        int k = player.getBlockZ();

        List<RocketEntity> list = world.getNonSpectatingEntities(RocketEntity.class, new Box((double) i - 25.0, (double) j - 25.0, (double) k - 25.0, (double) i + 25.0, (double) j + 25.0, (double) k + 25.0));

        for (RocketEntity rocketEntity : list) {
            if (rocketEntity.getLinkedEntity() == player) {

                player.getStackInHand(hand).damage(1, player, (e) -> {
                    e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
                });

                rocketEntity.detachFuse(true, false);
                if (!world.isClient && world instanceof ServerWorld) {

                    rocketEntity.networkLaunch();
                }

                return TypedActionResult.success(player.getStackInHand(hand));
            }
        }

        return TypedActionResult.fail(player.getStackInHand(hand));
    }
}
