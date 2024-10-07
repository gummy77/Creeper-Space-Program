package org.gum.csp.client.renderer;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.gum.csp.CspMain;
import org.gum.csp.client.CspMainClient;
import org.gum.csp.datastructs.RocketPart;
import org.gum.csp.entity.PayloadEntity;

public class ParachuteRenderer extends EntityRenderer<PayloadEntity> {

    private final EntityModelLoader modelLoader;

    public ParachuteRenderer(EntityRendererFactory.Context context){
        super(context);
        modelLoader = context.getModelLoader();
    }

    @Override
    public boolean shouldRender(PayloadEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }

    @Override
    public void render(PayloadEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.push();

        modelLoader.getModelPart(CspMainClient.PARACHUTE_MODEL_LAYER).render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(getTexture(entity))), light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
    }

    @Override
    public Identifier getTexture(PayloadEntity entity) {
        return new Identifier(CspMain.MODID, "textures/entity/parachute_texture.png");
    }
}
