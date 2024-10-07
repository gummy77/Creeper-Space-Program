package org.gum.csp.item;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.gum.csp.entity.RocketEntity;
import org.gum.csp.entity.RocketPartBlockEntity;
import org.gum.csp.registries.ItemRegistry;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

public class PayloadTrackingCompass extends Item {

    private static final Logger LOGGER = LogUtils.getLogger();

    public PayloadTrackingCompass(Settings settings) {
        super(settings);
    }

    public static boolean hasPayload(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        return nbtCompound != null && (nbtCompound.contains("PayloadDimension") || nbtCompound.contains("PayloadPos"));
    }

    @Nullable
    public static GlobalPos createPayloadPos(NbtCompound nbt, World world) {
        boolean bl = nbt.contains("PayloadPos");
        boolean bl2 = nbt.contains("PayloadDimension");


        if (bl && bl2) {
            Optional<RegistryKey<World>> optional = getPayloadDimension(nbt);
            if (optional.isPresent()) {

                if(nbt.contains("RocketId")) {
                    int id = nbt.getInt("RocketId");
                    Entity entity = world.getEntityById(id);

                    if(entity instanceof RocketEntity) {
                        BlockPos blockPos = entity.getBlockPos();
                        return GlobalPos.create(optional.get(), blockPos);
                    }
                }

                BlockPos blockPos = NbtHelper.toBlockPos(nbt.getCompound("PayloadPos"));
                return GlobalPos.create(optional.get(), blockPos);
            }
        }

        return null;
    }

    private static Optional<RegistryKey<World>> getPayloadDimension(NbtCompound nbt) {
        return World.CODEC.parse(NbtOps.INSTANCE, nbt.get("PayloadDimension")).result();
    }

    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient) {
            NbtCompound nbtCompound = stack.getOrCreateNbt();
            if (hasPayload(stack)) {
                Optional<RegistryKey<World>> optional = getPayloadDimension(nbtCompound);
                if (optional.isPresent() && optional.get() == world.getRegistryKey() && nbtCompound.contains("PayloadPos")) {
                    BlockPos blockPos = NbtHelper.toBlockPos(nbtCompound.getCompound("PayloadPos"));
                    if (!world.isInBuildLimit(blockPos)) {
                        nbtCompound.remove("PayloadPos");
                    }
                }

                if(nbtCompound.contains("RocketId")) {
                    int id = nbtCompound.getInt("RocketId");
                    Entity entity2 = world.getEntityById(id);
                    if(entity2 == null || !entity2.isAlive() || entity2.isRemoved()) {
                        nbtCompound.remove("RocketId");
                    }
                }
            }
        }
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        return super.useOnEntity(stack, user, entity, hand);
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        return super.useOnBlock(context);
    }

    public static NbtCompound writeNbt(RegistryKey<World> worldKey, int entityID, BlockPos pos, NbtCompound nbt) {
        nbt.put("PayloadPos", NbtHelper.fromBlockPos(pos));
        nbt.putInt("RocketId", entityID);
        DataResult<NbtElement> dataResult = World.CODEC.encodeStart(NbtOps.INSTANCE, worldKey);
        nbt.put("PayloadDimension", dataResult.get().left().get());

        nbt.putBoolean("PayloadTracked", true);

        return nbt;
    }
}
