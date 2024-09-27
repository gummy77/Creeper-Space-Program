package org.gum.csp.client.renderer;

import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.LightType;
import org.gum.csp.client.CspMainClient;
import org.gum.csp.datastructs.RocketPart;
import org.gum.csp.entity.PayloadEntity;
import org.gum.csp.entity.RocketEntity;

public class PayloadEntityRenderer extends EntityRenderer<PayloadEntity> {

    private final EntityModelLoader modelLoader;
    private final BlockRenderManager blockRenderManager;

    public PayloadEntityRenderer(EntityRendererFactory.Context context){
        super(context);
        modelLoader = context.getModelLoader();
        blockRenderManager = context.getBlockRenderManager();
        this.shadowRadius = 1f;
        this.shadowOpacity = 0.5f;
    }

    @Override
    public void render(PayloadEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.push();

        Vec3d rotationVector = entity.getVelocity();
        Vec3d UP = new Vec3d(0,1,0);
        Vec3d xAxis = UP.crossProduct(rotationVector);
        float w = (float) (Math.sqrt(rotationVector.length() * rotationVector.length()) + UP.dotProduct(rotationVector));

        Quaternion rotation = new Quaternion((float) xAxis.x, (float) xAxis.y, (float) xAxis.z, w);
        rotation.normalize();

        matrices.multiply(rotation);

        if(entity.getPayloadSettings() != null){
            if(entity.getPayloadSettings().blocks != null && entity.getPayloadSettings().blocks.length > 0) {
                matrices.translate(-0.5f, 0, -0.5f);

                RocketPart[] blocks = entity.getPayloadSettings().blocks;

                for (RocketPart block : blocks) {
                    matrices.push();
                    matrices.translate(block.offset.getX(), block.offset.getY(), block.offset.getZ());
                    blockRenderManager.renderBlockAsEntity(block.Block, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
                    matrices.pop();
                }
                matrices.pop();
                return;
            }
        }
        modelLoader.getModelPart(CspMainClient.ROCKET_MODEL_LAYER).render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(getTexture(entity))), light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
    }

    @Override
    public Identifier getTexture(PayloadEntity entity) {
        return new Identifier("csp", "textures/entity/rocket.png");
    }
}
