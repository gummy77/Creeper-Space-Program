package org.gum.csp.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.gum.csp.datastructs.RocketPart;
import org.gum.csp.entity.RocketPartBlockEntity;
import org.jetbrains.annotations.Nullable;



public class RocketPartBlock extends BlockWithEntity {


    public RocketPartBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if(blockEntity instanceof RocketPartBlockEntity) {
            float width = ((RocketPartBlockEntity) blockEntity).getRocketPart().radius;
            float offset = (16 - width) / 2;

            return VoxelShapes.cuboid(offset * 0.0625, 0.0f, offset * 0.0625, ((width + offset) * 0.0625), 1f, ((width + offset) * 0.0625));
        } else {
            return VoxelShapes.empty();
        }
    }

    @Override
    public boolean isCullingShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RocketPartBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
