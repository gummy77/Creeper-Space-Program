package org.gum.csp.payloads;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.gum.csp.datastructs.Payload;
import org.gum.csp.entity.PayloadEntity;
import org.gum.csp.entity.RocketEntity;
import org.gum.csp.registries.EntityRegistry;

public class DefaultPayload extends Payload {

    public DefaultPayload(){
        this.Mass = 1;
    }

    @Override
    public boolean Deploy(World world, RocketEntity entity, BlockPos pos) {
        PayloadEntity payloadEntity = EntityRegistry.PAYLOAD_ENTITY.create(world);
        payloadEntity.setPosition(pos.getX(), 350f, pos.getZ());

        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.put("PayloadSettings", entity.getRocketSettings().toNbt());
        payloadEntity.readCustomDataFromNbt(nbtCompound);

        world.spawnEntity(payloadEntity);
        return true;
    }
}
