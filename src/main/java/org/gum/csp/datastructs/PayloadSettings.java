package org.gum.csp.datastructs;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.math.BlockPos;
import org.gum.csp.registries.PayloadRegistry;

import java.util.*;

public class PayloadSettings {
    public RocketPart[] blocks;
    public PayloadRegistry.PAYLOADS payload;
    public ArrayList<ItemStack> returnItems;

    public PayloadSettings() { //setting these manually for now, will be based off of fuel later.
        this.blocks = new RocketPart[0];
        this.returnItems = new ArrayList<>();
    }

    public PayloadSettings(RocketSettings rocketSettings) {
        this();

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

    public PayloadSettings(RocketPart[] parts, ArrayList<ItemStack> items, PayloadRegistry.PAYLOADS payload){
        this(parts, items);
        this.payload = payload;
    }

    public PayloadSettings(RocketPart[] parts, ArrayList<ItemStack> items) { //setting these manually for now, will be based off of fuel later.
        this.blocks = parts;
        this.returnItems = items;
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

        if(payload != null) {
            nbt.putString("Payload", payload.toString());
        }

        nbt.putInt("ReturnItemCount", returnItems.size());
        NbtCompound items = new NbtCompound();
        for(int i = 0; i < returnItems.size(); i++) {
            items.putInt("item"+i, Item.getRawId(returnItems.get(i).getItem()));
            items.putInt("itemcount"+i, returnItems.get(i).getCount());
        }
        nbt.put("ReturnItems", items);

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

        ArrayList<ItemStack> returnItems = new ArrayList<>();
        int returnCount = nbt.getInt("ReturnItemCount");

        NbtCompound items = nbt.getCompound("ReturnItems");
        for(int i = 0; i < returnCount; i++) {
            int itemID = items.getInt("item"+i);
            int itemCount = items.getInt("itemcount"+i);
            ItemStack itemStack = Item.byRawId(itemID).getDefaultStack();
            itemStack.setCount(itemCount);
            returnItems.add(itemStack);
        }

        for (int i = 0; i < blockCount; i++) {
            NbtCompound blockNbt = nbt.getCompound("Block"+i);

            blocks[i] = RocketPart.fromNbt(blockNbt);;
        }

        if(payload != null) {
            return new PayloadSettings(blocks, returnItems, payload);
        }
        return new PayloadSettings(blocks, returnItems);
    }
}

