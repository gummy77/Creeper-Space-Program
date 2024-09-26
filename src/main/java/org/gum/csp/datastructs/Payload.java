package org.gum.csp.datastructs;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.gum.csp.entity.RocketEntity;

public abstract class Payload {
    public float Mass;

    public abstract boolean onDeploy(RocketEntity entity, BlockPos pos);

    public abstract void onLand(RocketEntity entity, BlockPos pos);

}
