package org.gum.csp.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.gum.csp.datastructs.RocketPart;
import org.gum.csp.entity.RocketPartBlockEntity;
import org.jetbrains.annotations.Nullable;



public class RocketPartBlock extends BlockWithEntity {
    private final RocketPart rocketPart;

    public RocketPartBlock(Settings settings, RocketPart rocketPart) {
        super(settings);
        this.rocketPart = rocketPart;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return VoxelShapes.cuboid(0.312f, 0.0f, 0.312f, 0.6875f, 1f, 0.6875f);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RocketPartBlockEntity(pos, state, rocketPart);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
