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

            float scale = entity.getPayloadSettings().getMaxWidth() / 16 ;

            if(!entity.isOnGround()){
                matrices.translate(0, 1, 0);
                matrices.scale(scale * 2, scale * 2, scale * 2);
                matrices.translate(0, 2, 0);

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
                if(entity.getParachuteAngle() < 90) {
                    entity.setParachuteAngle(entity.getParachuteAngle() + 2f);

                    matrices.translate(0, 0.25, 0);
                    matrices.scale(scale * 2, scale * 2, scale * 2);

                    //matrices.translate(0, 4, 0);

                    matrices.multiply(new Quaternion(Vec3f.POSITIVE_X, 180 + entity.getParachuteAngle(), true));

                    matrices.translate(0, -2.25f, 0);

                    modelLoader.getModelPart(ModelRegistry.DEPLOYED_PARACHUTE_MODEL_LAYER).render(matrices,
                            vertexConsumers.getBuffer(RenderLayer.getEntitySolid(
                                    new Identifier(CspMain.MODID, "textures/entity/parachute_texture.png")
                            )), light, OverlayTexture.DEFAULT_UV);
                } else {
                    matrices.scale(scale * 3, scale * 3, scale * 3);
                    matrices.translate(0, -1.375f, 1.75);
                    modelLoader.getModelPart(ModelRegistry.FALLEN_PARACHUTE_MODEL_LAYER).render(matrices,
                            vertexConsumers.getBuffer(RenderLayer.getEntitySolid(
                                    new Identifier(CspMain.MODID, "textures/entity/fallen_parachute.png")
                            )), light, OverlayTexture.DEFAULT_UV);
                }
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
