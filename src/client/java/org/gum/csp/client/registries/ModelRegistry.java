package org.gum.csp.client.registries;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import org.gum.csp.CspMain;
import org.gum.csp.client.model.*;
import org.gum.csp.client.renderer.*;
import org.gum.csp.registries.EntityRegistry;

public class ModelRegistry {

    public static final EntityModelLayer ROCKET_MODEL_LAYER = registerModel("rocket_model", EntityRegistry.ROCKET_ENTITY, RocketEntityRenderer::new);
    public static final EntityModelLayer DEPLOYED_PARACHUTE_MODEL_LAYER = registerModel("deployed_parachute_model", EntityRegistry.PAYLOAD_ENTITY, ParachuteRenderer::new);
    public static final EntityModelLayer UNDEPLOYED_PARACHUTE_MODEL_LAYER = registerModel("undeployed_parachute_model", EntityRegistry.PAYLOAD_ENTITY, ParachuteRenderer::new);
    public static final EntityModelLayer FALLEN_PARACHUTE_MODEL_LAYER = registerModel("fallen_parachute_model", EntityRegistry.PAYLOAD_ENTITY, ParachuteRenderer::new);
    public static final EntityModelLayer PAYLOAD_MODEL_LAYER = registerModel("payload_model", EntityRegistry.PAYLOAD_ENTITY, PayloadEntityRenderer::new);
    public static final EntityModelLayer GNEEP_MODEL_LAYER = registerModel("gneep_model", EntityRegistry.GNEEP_ENTITY, GneepEntityRenderer::new);


    public static void registerModels() {
        EntityModelLayerRegistry.registerModelLayer(ROCKET_MODEL_LAYER, RocketEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(PAYLOAD_MODEL_LAYER, PayloadEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(DEPLOYED_PARACHUTE_MODEL_LAYER, DeployedParachuteModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(UNDEPLOYED_PARACHUTE_MODEL_LAYER, UndeployedParachuteModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(FALLEN_PARACHUTE_MODEL_LAYER, FallenParachuteModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(GNEEP_MODEL_LAYER, GneepEntityModel::getTexturedModelData);
    }

    private static <E extends Entity> EntityModelLayer registerModel(String path, EntityType<E> entityType, EntityRendererFactory<E> rendererFactory) {
        EntityModelLayer modelLayer = new EntityModelLayer(new Identifier(CspMain.MODID, path), "main");
        EntityRendererRegistry.register(entityType, rendererFactory);
        return modelLayer;
    }
}
