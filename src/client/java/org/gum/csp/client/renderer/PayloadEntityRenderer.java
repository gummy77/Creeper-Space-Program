package org.gum.csp.client.renderer;

import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import org.gum.csp.CspMain;
import org.gum.csp.client.CspMainClient;
import org.gum.csp.client.registries.ModelRegistry;
import org.gum.csp.datastructs.RocketPart;
import org.gum.csp.entity.PayloadEntity;

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
    public boolean shouldRender(PayloadEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }

    @Override
    public void render(PayloadEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.push();

        if(entity.getPayloadSettings() != null){
            matrices.push();
            matrices.translate(0, 1, 0);
            float scale = entity.getPayloadSettings().getMaxWidth() / 16 * 2;
            matrices.scale(scale, scale, scale);
            matrices.translate(0, 2, 0);

            if(!entity.isOnGround()){
                matrices.multiply(Quaternion.fromEulerXyz((float) Math.PI, 0, 0));
                if(entity.hasParachuteDeployed()) {
                    modelLoader.getModelPart(ModelRegistry.DEPLOYED_PARACHUTE_MODEL_LAYER).render(matrices,
                            vertexConsumers.getBuffer(RenderLayer.getEntitySolid(
                                    new Identifier(CspMain.MODID, "textures/entity/parachute_texture.png")
                            )), light, OverlayTexture.DEFAULT_UV);
                } else {
                    modelLoader.getModelPart(ModelRegistry.UNDEPLOYED_PARACHUTE_MODEL_LAYER).render(matrices,
                            vertexConsumers.getBuffer(RenderLayer.getEntitySolid(
                                    new Identifier(CspMain.MODID, "textures/entity/parachute_texture.png")
                            )), light, OverlayTexture.DEFAULT_UV);
                }
            } else {

            }
            matrices.pop();

            this.shadowRadius = entity.getPayloadSettings().getMaxWidth() * 0.0625f * 1.5f;
            if(entity.getPayloadSettings().blocks != null && entity.getPayloadSettings().blocks.length > 0) {
                matrices.translate(-0.5f, 0, -0.5f);

                RocketPart[] blocks = entity.getPayloadSettings().blocks;

                for (RocketPart block : blocks) {
                    matrices.push();
                    matrices.translate(block.offset.getX(), block.offset.getY(), block.offset.getZ());
                    blockRenderManager.renderBlockAsEntity(block.block, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
                    matrices.pop();
                }
                matrices.pop();
                return;
            }
        }
        modelLoader.getModelPart(ModelRegistry.PAYLOAD_MODEL_LAYER).render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(getTexture(entity))), light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
    }

    @Override
    public Identifier getTexture(PayloadEntity entity) {
        return new Identifier(CspMain.MODID, "textures/entity/rocket.png");
    }
}
