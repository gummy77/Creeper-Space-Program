package org.gum.csp.payloads;

import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.gum.csp.datastructs.Payload;
import org.gum.csp.entity.RocketEntity;

public class StardustCatcher extends Payload {

    public StardustCatcher(){
        this.Mass = 1;
    }

    @Override
    public void onDeploy(RocketEntity entity,  BlockPos pos) {
        System.out.println("StardustCatcher: onDeploy");
        System.out.println("StardustCatcher: "+pos);
    }

    @Override
    public void onLand(BlockPos pos) {
        System.out.println("StardustCatcher: onLand");
    }
}
