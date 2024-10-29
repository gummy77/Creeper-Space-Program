package org.gum.csp.fluid;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.gum.csp.registries.FluidRegistry;
import org.gum.csp.registries.ItemRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


public abstract class OilFluid extends FlowableFluid {
    @Override
    public Fluid getStill() {
        return FluidRegistry.STILL_OIL;
    }

    @Override
    public Fluid getFlowing() {
        return FluidRegistry.FLOWING_OIL;
    }

    @Override
    public Item getBucketItem() {
        return ItemRegistry.OIL_BUCKET;
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return FluidRegistry.OIL.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(state));
    }

    public boolean matchesType(Fluid fluid) {
        return fluid == getStill() || fluid == getFlowing();
    }

    @Override
    protected boolean isInfinite() { return false; }

    @Override
    protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
        final BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropStacks(state, world, pos, blockEntity);
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) { return false; }

    @Override
    protected int getFlowSpeed(WorldView world) { return 1; }

    @Override
    protected int getLevelDecreasePerBlock(WorldView world) { return 3; }

    @Override
    public int getTickRate(WorldView world) { return 30; }

    @Override
    protected float getBlastResistance() { return 100f; }

    @Override
    protected @Nullable ParticleEffect getParticle() {
        return ParticleTypes.FLAME;
    }

    public Optional<SoundEvent> getBucketFillSound() {
        return Optional.of(SoundEvents.ITEM_BUCKET_FILL_LAVA);
    }

    public static class Flowing extends OilFluid {
        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getLevel(FluidState state) {
            return state.get(LEVEL);
        }

        @Override
        public boolean isStill(FluidState state) {
            return false;
        }
    }

    public static class Still extends OilFluid {
        @Override
        public int getLevel(FluidState state) {
            return 8;
        }

        @Override
        public boolean isStill(FluidState state) {
            return true;
        }
    }
}
