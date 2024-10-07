package org.gum.csp.registries;

import net.minecraft.item.ItemStack;
import org.gum.csp.datastructs.Payload;
import org.gum.csp.payloads.DefaultPayload;
import org.gum.csp.payloads.StardustCatcher;

public class PayloadRegistry {
    public static Payload STARDUST_CATCHER = new StardustCatcher();
    public static Payload DEFAULT_PAYLOAD = new DefaultPayload();

    public static Payload getPayload(PAYLOADS i) {

        return switch (i) {
            case DEFAULT -> DEFAULT_PAYLOAD;
            case STARDUST -> STARDUST_CATCHER;
            case MAPPER -> null;
        };

    }

    public static ItemStack getPayloadStack(PAYLOADS payload) {
        return switch (payload) {
            case DEFAULT -> ItemRegistry.DEFAULT_PAYLOAD_ITEM.getDefaultStack();
            case STARDUST -> null;
            case MAPPER -> null;
        };

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
