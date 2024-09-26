package org.gum.csp.payloads;

import net.minecraft.util.math.BlockPos;
import org.gum.csp.datastructs.Payload;
import org.gum.csp.entity.RocketEntity;

public class StardustCatcher extends Payload {

    public StardustCatcher(){
        this.Mass = 1;
    }

    @Override
    public boolean onDeploy(RocketEntity entity,  BlockPos pos) {
        System.out.println("StardustCatcher: onDeploy");
        System.out.println("StardustCatcher: "+pos);
        return true;
    }

    @Override
    public void onLand(RocketEntity entity, BlockPos pos) {
        System.out.println("StardustCatcher: onLand");
    }
}
