package org.gum.csp.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class OilFluidBlock extends FluidBlock {
    public OilFluidBlock(FlowableFluid fluid, Settings settings) {
        super(fluid, settings.suffocates((state, world, pos) -> true));
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        entity.slowMovement(state, new Vec3d(0.5f, 0.5f, 0.5f));
        entity.setSprinting(false);



        if(entity instanceof LivingEntity) {
            BlockState blockState = world.getBlockState(new BlockPos(entity.getEyePos()));
            if(blockState.getBlock() == this){
                float liquidHeight = (((float)blockState.get(OilFluidBlock.LEVEL)) / 8);

                if(1-liquidHeight > (entity.getEyePos().getY() % 1f)) { //Is entities head under the surface
                    ((LivingEntity) entity).addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 15, 255, false, false, false));
                    ((LivingEntity) entity).addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 15, 255, false, false, false));
                    ((LivingEntity) entity).addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 15, 255, false, false, false));
                    entity.damage(new DamageSource("drown_in_oil").setBypassesArmor(), 1.0f);
                }
            }
        }

        entity.onLanding();
        super.onEntityCollision(state, world, pos, entity);
    }



    @Override
    public void onEntityLand(BlockView world, Entity entity) {
        super.onEntityLand(world, entity);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        super.onSteppedOn(world, pos, state, entity);
    }
}
