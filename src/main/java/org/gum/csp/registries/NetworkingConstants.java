package org.gum.csp.registries;

import net.minecraft.util.Identifier;
import org.gum.csp.CspMain;

public class NetworkingConstants {

    public static final Identifier ATTACH_FUSE_PACKET_ID;
    public static final Identifier LAUNCH_ROCKET_PACKET_ID;

    static {
        ATTACH_FUSE_PACKET_ID = new Identifier(CspMain.MODID, "attach_fuse");
        LAUNCH_ROCKET_PACKET_ID = new Identifier(CspMain.MODID, "launch_rocket");
    }
}
