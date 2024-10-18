package org.gum.csp.registries;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.gum.csp.datastructs.PayloadSettings;
import org.gum.csp.entity.PayloadEntity;
import org.gum.csp.entity.RocketEntity;

public class PayloadRegistry {
    public static Payloads payloadFromStack(ItemStack stack) {
        if(     stack.getItem() == ItemRegistry.DEFAULT_PAYLOAD_ITEM)   return Payloads.DEFAULT;
        else if(stack.getItem() == ItemRegistry.RAIN_STARTER_ITEM)      return Payloads.RAIN_STARTER;

        return null;
    }

    public static void registerPayloads(){
    }

    public interface PayloadFunctions {
        default void onDeploy(World world, RocketEntity entity, BlockPos pos) {};
        default void onInteract(World world, PayloadEntity entity, BlockPos pos, Entity interactor) {};
    }

    public enum Payloads implements PayloadFunctions {
        DEFAULT (true, 1, 0) {
            @Override
            public void onDeploy(World world, RocketEntity entity, BlockPos pos) {
                this.spawnPayload(world, entity, pos);
            };
        },
        RAIN_STARTER(false, 2.5f, 1000) {
            @Override
            public void onDeploy(World world, RocketEntity entity, BlockPos pos) {
                if(!world.isRaining()){
                    if(world instanceof ServerWorld){
                        ((ServerWorld) world).setWeather(120, 0, false, false);
                    }
                }
            };
        },
        STARDUST(true, 5f, 25000f) {
            @Override
            public void onDeploy(World world, RocketEntity entity, BlockPos pos) {
                this.spawnPayload(world, entity, pos);
            };
            @Override
            public void onInteract(World world, PayloadEntity entity, BlockPos pos, Entity interactor) {
                if(entity.getPayloadSettings().heightReached < 50000f) {
                    entity.dropStack(ItemRegistry.STARDUST_BASIC.getDefaultStack());
                } else if(entity.getPayloadSettings().heightReached < 75000f) {
                    entity.dropStack(ItemRegistry.STARDUST_BASIC.getDefaultStack());
                } else if(entity.getPayloadSettings().heightReached < 100000f) {
                    entity.dropStack(ItemRegistry.STARDUST_BASIC.getDefaultStack());
                }
            };
        };

        private final boolean canBeTracked;
        private final float minHeight;
        private final float mass;

        public boolean canBeTracked() { return canBeTracked; }
        public float minHeight() { return minHeight; }
        public float getMass() { return mass; }

        void spawnPayload(World world, RocketEntity entity, BlockPos pos) {
            PayloadEntity payloadEntity = EntityRegistry.PAYLOAD_ENTITY.create(world);
            if(payloadEntity == null) return;
            payloadEntity.setPosition(pos.getX(), 350f, pos.getZ());

            PayloadSettings payloadSettings = new PayloadSettings(entity.getRocketSettings());

            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.put("PayloadSettings", payloadSettings.toNbt());
            payloadEntity.readCustomDataFromNbt(nbtCompound);

            world.spawnEntity(payloadEntity);
        }


        Payloads(boolean canBeTracked, float mass, float minHeight) {
            this.minHeight = minHeight;
            this.canBeTracked = canBeTracked;
            this.mass = mass;
        }
    }
}
