package org.gum.csp.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import org.gum.csp.entity.RocketEntity;
import org.gum.csp.registries.EntityRegistry;

public class Rocket extends Item {
    public Rocket(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        Entity entity = new RocketEntity(EntityRegistry.ROCKET_ENTITY, context.getWorld());
        entity.setPosition(context.getBlockPos().getX() + 0.5f, context.getBlockPos().getY() + 1, context.getBlockPos().getZ() + 0.5f);
        context.getWorld().spawnEntity(entity);
        return super.useOnBlock(context);
    }
}
