package org.gum.csp.payloads;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.gum.csp.datastructs.Payload;
import org.gum.csp.entity.RocketEntity;

public class StardustCatcher extends Payload {

    public StardustCatcher(){
        this.Mass = 1;
    }

    @Override
    public boolean Deploy(World world, RocketEntity entity, BlockPos pos, float heightReached) {
        System.out.println("StardustCatcher: Deploy");
        System.out.println("StardustCatcher: "+pos);
        return true;
    }
}
