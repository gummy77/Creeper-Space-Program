package org.gum.csp.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.gum.csp.registries.PayloadRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class PayloadItem extends Item {
    PayloadRegistry.PAYLOADS payloadsType;
    public PayloadItem(Settings settings, PayloadRegistry.PAYLOADS type) {
        super(settings);
        this.payloadsType = type;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.of("§7Mass: " + payloadsType.getMass() + "kg"));
        tooltip.add(Text.of("§7Min Height: " + (payloadsType.minHeight() == 0 ? "none" : payloadsType.minHeight()+"m")));
        tooltip.add(Text.of(payloadsType.canBeTracked() ? "§7Trackable" : "§7Untrackable"));
    }
}
