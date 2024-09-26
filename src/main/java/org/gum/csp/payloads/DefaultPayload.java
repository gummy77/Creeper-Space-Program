package org.gum.csp.payloads;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.util.math.BlockPos;
import org.gum.csp.datastructs.Payload;
import org.gum.csp.entity.RocketEntity;

public class DefaultPayload extends Payload {

    public DefaultPayload(){
        this.Mass = 1;
    }

    @Override
    public boolean onDeploy(RocketEntity entity,  BlockPos pos) {
        System.out.println("Payload: onDeploy");
        System.out.println("Payload: "+pos);
        return true;
    }

    @Override
    public void onLand(RocketEntity entity, BlockPos pos) {
        System.out.println("Payload: onLand");
        entity.world.spawnEntity(EntityType.BOAT.create(entity.world));
    }
}
