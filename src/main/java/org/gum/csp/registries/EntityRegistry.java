package org.gum.csp.registries;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.gum.csp.CspMain;
import org.gum.csp.entity.EntitySettings;
import org.gum.csp.entity.RocketEntity;

public class EntityRegistry {

    public static EntityType<RocketEntity> ROCKETENTITY;

    public static void registerEntityAttributes() {

    }

    public static void registerEntities() {
        ROCKETENTITY = registerEntity("rocketentity", RocketEntity::new, RocketEntity.settings);
    }

    protected static <T extends Entity> EntityType<T> registerEntity(String path, EntityType.EntityFactory<T> type, EntitySettings settings) {
        EntityType<T> entityType = Registry.register(
                Registry.ENTITY_TYPE,
                new Identifier(CspMain.MODID, path),
                FabricEntityTypeBuilder.create(settings.spawnGroup, type)
                        .dimensions(EntityDimensions.fixed(settings.x, settings.y))
                        .build());

        if (settings.spawnsNaturally) {
            BiomeModifications.addSpawn(
                    (biomeSelectionContext -> biomeSelectionContext.hasTag(settings.selectorTag)),
                    settings.spawnGroup,
                    entityType,
                    settings.spawnWeight,
                    settings.minGroupSize,
                    settings.maxGroupSize);
        }
        return entityType;
    }
}
