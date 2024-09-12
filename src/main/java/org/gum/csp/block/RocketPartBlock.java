package org.gum.csp.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.gum.csp.datastructs.RocketPart;

public class RocketPartBlock extends Block {
    RocketPart rocketPart; //TODO move to NBTData
    //TODO add blockEntity

    public RocketPartBlock(Settings settings, RocketPart rocketPart) {
        super(settings);
        this.rocketPart = rocketPart;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return VoxelShapes.cuboid(0.312f, 0.0f, 0.312f, 0.6875f, 1f, 0.6875f);
    }


}
