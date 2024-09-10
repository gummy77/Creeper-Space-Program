package org.gum.csp.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.gum.csp.registries.ItemRegistry;

public class RocketEntity extends Entity {

    public static final EntitySettings settings = new EntitySettings(
            "rocketentity",
            SpawnGroup.MISC,
            0.6f, 2f
    );

    public RocketEntity(EntityType<? extends Entity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {

        //CHECK IF ALREADY ATTACHED
        if(false) {

        }

        ItemStack itemStack = new ItemStack(ItemRegistry.FUSE);
        if (player.isHolding(ItemRegistry.LAUNCH_KIT)) {
            if(player.getInventory().contains(itemStack)){
                player.getInventory().removeStack(player.getInventory().getSlotWithStack(itemStack), 1);


                // ATTACH LASSO HERE

                player.getStackInHand(hand).finishUsing(world, player);

                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return new DefaultAttributeContainer.Builder()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 6.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2f);
    }
}
