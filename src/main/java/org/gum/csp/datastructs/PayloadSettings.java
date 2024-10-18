package org.gum.csp.datastructs;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.math.BlockPos;
import org.gum.csp.entity.RocketEntity;
import org.gum.csp.registries.PayloadRegistry;

import java.util.*;

public class PayloadSettings {
    public RocketPart[] blocks;
    public PayloadRegistry.Payloads payload;
    public float heightReached;

    public PayloadSettings() {
        this.blocks = new RocketPart[0];
    }

    public PayloadSettings(RocketSettings rocketSettings) {
        this();

        this.payload = rocketSettings.payload;
        this.heightReached = RocketEntity.calculateMaxHeight(rocketSettings);

        RocketPart[] parts = new RocketPart[rocketSettings.blocks.length];
        for(int i = 0; i < rocketSettings.blocks.length; i++) {
            parts[i] = rocketSettings.blocks[i].copy();
        }

        ArrayList<RocketPart> rocketParts = new ArrayList<>();
        for (int i = 0; i < parts.length; i++) {
            if(parts[i].partType == RocketPart.PartType.NOSE){
                parts[i].offset = new BlockPos(0, i, 0);
                rocketParts.add(parts[i]);
            }
        }
        this.blocks = rocketParts.toArray(new RocketPart[0]);
    }

    public PayloadSettings(RocketPart[] parts, float heightReached) {
        this.blocks = parts;
        this.heightReached = heightReached;
    }

    public float getMaxWidth(){
        float mWidth = 0;
        for(RocketPart block : this.blocks){
            if(block.radius > mWidth){
                mWidth = block.radius;
            }
        }
        return mWidth;
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();

        if(payload != null) nbt.putString("Payload", payload.toString());
        nbt.putFloat("HeightReached", heightReached);

        nbt.putInt("BlockCount", blocks.length);
        for (int i = 0; i < blocks.length; i++) {
            nbt.put("Block"+i, blocks[i].toNbt());
        }

        return nbt;
    }

    public static PayloadSettings fromNbt(NbtCompound nbt) {
        int blockCount = nbt.getInt("BlockCount");
        RocketPart[] blocks = new RocketPart[blockCount];

        PayloadRegistry.Payloads payload = null;
        try {
            payload = PayloadRegistry.Payloads.valueOf(nbt.getString("Payload"));
        } catch (IllegalArgumentException ignored) {}

        float heightReached = nbt.getFloat("HeightReached");

        for (int i = 0; i < blockCount; i++) {
            NbtCompound blockNbt = nbt.getCompound("Block"+i);
            blocks[i] = RocketPart.fromNbt(blockNbt);;
        }

        PayloadSettings settings = new PayloadSettings(blocks, heightReached);
        if(payload != null) settings.payload = payload;

        return settings;
    }
}

