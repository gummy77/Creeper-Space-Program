package org.gum.csp.item;

import net.minecraft.client.item.TooltipData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.gum.csp.registries.PayloadRegistry;

import java.util.Optional;

public class PayloadItem extends Item {
    PayloadRegistry.PAYLOADS payloadsType;
    public PayloadItem(Settings settings, PayloadRegistry.PAYLOADS type) {
        super(settings);
        this.payloadsType = type;
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        TooltipData tooltipData = super.getTooltipData(stack).orElse(null);
        Optional<TooltipData> data = Optional.of(tooltipData);

        return data;
    }
}
