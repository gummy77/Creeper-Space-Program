package org.gum.csp.registries;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.gum.csp.datastructs.Payload;
import org.gum.csp.payloads.*;

public class PayloadRegistry {
    public static Payload STARDUST_CATCHER = new StardustCatcher();
    public static Payload DEFAULT_PAYLOAD = new DefaultPayload(); //TODO move these to ENUM
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
        DEFAULT(true, 1, 0),
        STARDUST(true, 1, 10000),
        RAIN_STARTER(false, 2, 750),
        MAPPER(true, 1, 1000);

        private final boolean canBeTracked;
        private final float minHeight;
        private final float mass;

        public boolean canBeTracked() {
            return canBeTracked;
        }
        public float minHeight() {
            return minHeight;
        }
        public float getMass() {
            return mass;
        }

        PAYLOADS(boolean canBeTracked, float mass, float minHeight) {
            this.minHeight = minHeight;
            this.canBeTracked = canBeTracked;
            this.mass = mass;
        }
    }
}
