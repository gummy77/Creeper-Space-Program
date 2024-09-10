package org.gum.csp.client.renderer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.gum.csp.client.CspMainClient;
import org.gum.csp.client.model.RocketEntityModel;
import org.gum.csp.entity.RocketEntity;

public class RocketEntityRenderer extends EntityRenderer<RocketEntity> {

    private final EntityModelLoader modelLoader;


    public RocketEntityRenderer(EntityRendererFactory.Context context){
        super(context);
        modelLoader = context.getModelLoader();

        this.shadowRadius = 1.0f;
    }

    @Override
    public void render(RocketEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        modelLoader.getModelPart(CspMainClient.ROCKET_MODEL_LAYER).render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(getTexture(entity))), light, 0);

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.pop();
    }

    @Override
    public Identifier getTexture(RocketEntity entity) {
        return new Identifier("csp", "textures/entity/rocket.png");
    }
}
