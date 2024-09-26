package org.gum.csp.item;

import net.minecraft.item.Item;
import org.gum.csp.registries.PayloadRegistry;

public class PayloadItem extends Item {
    PayloadRegistry.PAYLOADS payloadsType;
    public PayloadItem(Settings settings, PayloadRegistry.PAYLOADS type) {
        super(settings);
        this.payloadsType = type;
    }
}
