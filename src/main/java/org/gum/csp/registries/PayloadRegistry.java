package org.gum.csp.registries;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.gum.csp.datastructs.Payload;
import org.gum.csp.payloads.*;

public class PayloadRegistry {
    public static Payload STARDUST_CATCHER = new StardustCatcher();
    public static Payload DEFAULT_PAYLOAD = new DefaultPayload();
    public static Payload RAIN_STARTER = new RainStarter();

    public static Payload getPayload(PAYLOADS i) {

        return switch (i) {
            case DEFAULT -> DEFAULT_PAYLOAD;
            case STARDUST -> STARDUST_CATCHER;
            case RAIN_STARTER -> RAIN_STARTER;
            case MAPPER -> null;
        };

    }

    public static ItemStack getPayloadStack(PAYLOADS payload) {
        return switch (payload) {
            case DEFAULT -> ItemRegistry.DEFAULT_PAYLOAD_ITEM.getDefaultStack();
            case STARDUST -> null;
            case RAIN_STARTER -> ItemRegistry.RAIN_STARTER_ITEM.getDefaultStack();
            case MAPPER -> null;
        };

    }

    public static PAYLOADS payloadFromStack(ItemStack stack) {
        if(stack.getItem() == ItemRegistry.DEFAULT_PAYLOAD_ITEM) {
            return PAYLOADS.DEFAULT;
        } else if(stack.getItem() == ItemRegistry.RAIN_STARTER_ITEM) {
            return PAYLOADS.RAIN_STARTER;
        }
        /*
        else if(stack.getItem() == ItemRegistry.STARDUST_CATCHER_PAYLOAD) {
            return STARDUST_CATCHER;
        }
        */

        return null;
    }

    public enum PAYLOADS {
        DEFAULT(true),
        STARDUST(true),
        RAIN_STARTER(false),
        MAPPER(true);

        private final boolean canBeTracked;

        public boolean canBeTracked() {
            return canBeTracked;
        }

        PAYLOADS(boolean canBeTracked) {
            this.canBeTracked = canBeTracked;
        }
    }
}
