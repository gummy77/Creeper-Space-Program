package org.gum.csp.payloads;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.gum.csp.datastructs.Payload;
import org.gum.csp.datastructs.PayloadSettings;
import org.gum.csp.entity.PayloadEntity;
import org.gum.csp.entity.RocketEntity;
import org.gum.csp.registries.EntityRegistry;

public class RainStarter extends Payload {

    public RainStarter(){
        this.Mass = 2;
    }

    @Override
    public boolean Deploy(World world, RocketEntity entity, BlockPos pos, float heightReached) {
        if(!world.isRaining()){
            if(world instanceof ServerWorld){
                ((ServerWorld) world).setWeather(120, 0, false, false);
            }
        }
        return true;
    }

    @Override
    public boolean onOpen(World world, BlockPos pos, Entity entity) {
        return false;
    }
}
