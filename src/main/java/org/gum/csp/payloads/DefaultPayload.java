package org.gum.csp.payloads;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.gum.csp.datastructs.Payload;
import org.gum.csp.datastructs.PayloadSettings;
import org.gum.csp.entity.PayloadEntity;
import org.gum.csp.entity.RocketEntity;
import org.gum.csp.registries.EntityRegistry;
import org.gum.csp.registries.ItemRegistry;

import java.util.ArrayList;

public class DefaultPayload extends Payload {

    public DefaultPayload(){
        this.Mass = 1;
    }

    @Override
    public boolean Deploy(World world, RocketEntity entity, BlockPos pos, float heightReached) {

        PayloadEntity payloadEntity = EntityRegistry.PAYLOAD_ENTITY.create(world);
        payloadEntity.setPosition(pos.getX(), -50f, pos.getZ());

        PayloadSettings payloadSettings = new PayloadSettings(entity.getRocketSettings());
        payloadSettings.returnItems.add(ItemRegistry.ADRIAN.getDefaultStack());

        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.put("PayloadSettings", payloadSettings.toNbt());
        payloadEntity.readCustomDataFromNbt(nbtCompound);

        System.out.println(nbtCompound);
        System.out.println(payloadEntity.getPayloadSettings().returnItems);

        world.spawnEntity(payloadEntity);
        return true;
    }
}
