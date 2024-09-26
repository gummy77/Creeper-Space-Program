package org.gum.csp.registries;

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

    public enum PAYLOADS {
        DEFAULT,
        STARDUST,
        MAPPER,
    }
}
