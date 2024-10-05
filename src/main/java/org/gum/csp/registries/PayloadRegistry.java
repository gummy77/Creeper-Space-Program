package org.gum.csp.registries;

import net.minecraft.item.ItemStack;
import org.gum.csp.datastructs.Payload;
import org.gum.csp.payloads.DefaultPayload;
import org.gum.csp.payloads.StardustCatcher;

public class PayloadRegistry {
    public static Payload STARDUST_CATCHER = new StardustCatcher();
    public static Payload DEFAULT_PAYLOAD = new DefaultPayload();

    public static Payload getPayload(PAYLOADS i) {

        switch (i) {
            case DEFAULT:
                return DEFAULT_PAYLOAD;
            case STARDUST:
                return STARDUST_CATCHER;
            case MAPPER:
                return null;
        }

        return null;
    }

    public static ItemStack getPayloadStack(PAYLOADS payload) {
        switch (payload) {
            case DEFAULT:
                return ItemRegistry.DEFAULT_PAYLOAD_ITEM.getDefaultStack();
            case STARDUST:
                return null;
            case MAPPER:
                return null;
        }

        return null;
    }

    public static PAYLOADS payloadFromStack(ItemStack stack) {
        if(stack.getItem() == ItemRegistry.DEFAULT_PAYLOAD_ITEM) {
            return PAYLOADS.DEFAULT;
        }
        /*
        else if(stack.getItem() == ItemRegistry.STARDUST_CATCHER_PAYLOAD) {
            return STARDUST_CATCHER;
        }
        */


        return null;
    }

    public enum PAYLOADS {
        DEFAULT,
        STARDUST,
        MAPPER,
    }
}
