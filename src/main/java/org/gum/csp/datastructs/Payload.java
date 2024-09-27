package org.gum.csp.datastructs;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.gum.csp.entity.RocketEntity;

public abstract class Payload {
    public float Mass;

    public abstract boolean Deploy(World world, RocketEntity entity, BlockPos pos);
}
