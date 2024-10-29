package org.gum.csp.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;
import org.gum.csp.registries.BlockRegistry;
import org.gum.csp.registries.FluidRegistry;

public class OilPoolFeature extends Feature<OilPoolFeature.OilPoolFeatureConfig> {

    public OilPoolFeature(Codec<OilPoolFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<OilPoolFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        Random random = context.getRandom();
        OilPoolFeature.OilPoolFeatureConfig config = context.getConfig();

        if(random.nextFloat() < 0.9) return false;

        int radius = random.nextBetween(config.radius()/4, config.radius());
        Identifier blockId = config.fluid();

        BlockState state = Registry.BLOCK.get(blockId).getDefaultState();
        if(state == null) throw new IllegalStateException(blockId + " could not be parsed to a valid block identifier.");

        BlockPos topPos = new BlockPos(origin.getX(), world.getTopY(), origin.getZ());

        while (true) {
            BlockState checkState = world.getBlockState(topPos);
            if(checkState.isAir()) {
                topPos = topPos.down();
            } else {
                break;
            }
        }

        topPos = topPos.add(0, -random.nextBetween(0, topPos.getY()), 0);

        for (int x = -radius; x < radius; x++) {
            for (int y = -radius/2; y < radius/2; y++) {
                for (int z = -radius; z < radius; z++) {

                    float radii = x*x + z*z;
                    if (radii > radius*radius) continue;
                    if((radii + y*y) > radius*radius/2f) continue;

                    if(world.getBlockState(topPos.add(x, y, z)).isAir()) continue;
                    if(world.getBlockState(topPos.add(x, y, z)).isIn(BlockTags.LEAVES)) continue;
                    if(world.getBlockState(topPos.add(x, y, z)).isIn(BlockTags.REPLACEABLE_PLANTS)) continue;
                    if(world.getBlockState(topPos.add(x, y, z)).isIn(BlockTags.LOGS)) continue;

                    if(y > radius/8) {
                        world.setBlockState(topPos.add(x, y, z), Blocks.AIR.getDefaultState(), 0x10);
                        world.updateNeighbors(topPos.add(x,y,z), Blocks.AIR);

                    }else {
                        world.setBlockState(topPos.add(x, y, z), state, 0x10);
                        world.updateNeighbors(topPos.add(x,y,z), FluidRegistry.OIL);
                    }

                    this.markBlocksAboveForPostProcessing(world, topPos.add(x, y, z));
                }
            }
        }

        return true;
    }

    public record OilPoolFeatureConfig(int radius, Identifier fluid) implements FeatureConfig {
        public static final Codec<OilPoolFeatureConfig> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                // you can add as many of these as you want, one for each parameter
                                Codecs.POSITIVE_INT.fieldOf("radius").forGetter(OilPoolFeatureConfig::radius),
                                Identifier.CODEC.fieldOf("fluid").forGetter(OilPoolFeatureConfig::fluid))
                        .apply(instance, OilPoolFeatureConfig::new));
    }
}
