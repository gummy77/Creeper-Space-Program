package org.gum.csp.registries;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.gum.csp.CspMain;

public class NetworkingConstants {

    public static final Identifier ATTACH_FUSE_PACKET_ID;

    static {
        ATTACH_FUSE_PACKET_ID = new Identifier("csp", "attach_fuse");
    }
}
