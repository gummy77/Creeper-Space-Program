package org.gum.csp.datastructs;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.gum.csp.registries.PayloadRegistry;

import java.util.*;

public class PayloadSettings {
    public RocketPart[] blocks;
    public PayloadRegistry.PAYLOADS payload;

    public PayloadSettings() { //setting these manually for now, will be based off of fuel later.
        blocks = new RocketPart[0];
    }

    public PayloadSettings(RocketSettings rocketSettings) {
        this();
        RocketPart[] parts = rocketSettings.blocks.clone();
        ArrayList<RocketPart> rocketParts = new ArrayList<>();
        for (int i = 0; i < rocketSettings.blocks.length; i++) {
            if(parts[i].partType == RocketPart.PartType.NOSE){
                parts[i].offset = new BlockPos(0, i, 0);
                rocketParts.add(parts[i]);
            }
        }
        this.blocks = rocketParts.toArray(new RocketPart[0]);
    }

    public PayloadSettings(RocketPart[] parts, PayloadRegistry.PAYLOADS payload){
        this(parts);
        this.payload = payload;
    }

    public PayloadSettings(RocketPart[] parts) { //setting these manually for now, will be based off of fuel later.
        blocks = parts;
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();

        if(payload != null) {
            nbt.putString("Payload", payload.toString());
        }
        nbt.putInt("BlockCount", blocks.length);
        for (int i = 0; i < blocks.length; i++) {
            nbt.put("Block"+i, blocks[i].toNbt());
        }

        return nbt;
    }

    public static PayloadSettings fromNbt(NbtCompound nbt) {
        int blockCount = nbt.getInt("BlockCount");
        RocketPart[] blocks = new RocketPart[blockCount];

        PayloadRegistry.PAYLOADS payload = null;
        try {
            payload = PayloadRegistry.PAYLOADS.valueOf(nbt.getString("Payload"));
        } catch (IllegalArgumentException ignored) {}

        for (int i = 0; i < blockCount; i++) {
            NbtCompound blockNbt = nbt.getCompound("Block"+i);

            blocks[i] = RocketPart.fromNbt(blockNbt);;
        }

        if(payload != null) {
            return new PayloadSettings(blocks, payload);
        }
        return new PayloadSettings(blocks);
    }
}

