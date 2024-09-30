package org.gum.csp.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.LightBlock;
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
import org.gum.csp.entity.RocketEntity;
import org.gum.csp.registries.BlockRegistry;

public class RocketEntityRenderer extends EntityRenderer<RocketEntity> {

    private final EntityModelLoader modelLoader;
    private final BlockRenderManager blockRenderManager;

    public RocketEntityRenderer(EntityRendererFactory.Context context){
        super(context);
        modelLoader = context.getModelLoader();
        blockRenderManager = context.getBlockRenderManager();
        this.shadowRadius = 0.5f;
        this.shadowOpacity = 0.5f;
    }

    @Override
    public boolean shouldRender(RocketEntity entity, Frustum frustum, double x, double y, double z) {
        return true; //TODO make this work
//        Box box = new Box(x, y, z, x + entity.getWidth(), y + entity.getHeight(), z + entity.getWidth());
//        box.offset(entity.renderPosition.getX(), entity.renderPosition.getY(), entity.renderPosition.getZ());
//        return frustum.isVisible(box);
    }



    @Override
    public void render(RocketEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.push();

        Vec3d rotationVector = entity.getVelocity();
        Vec3d UP = new Vec3d(0,1,0);
        Vec3d xAxis = UP.crossProduct(rotationVector);
        float w = (float) (Math.sqrt(rotationVector.length() * rotationVector.length()) + UP.dotProduct(rotationVector));

        Quaternion rotation = new Quaternion((float) xAxis.x, (float) xAxis.y, (float) xAxis.z, w);
        rotation.normalize();

        matrices.multiply(rotation);

        if(entity.getRocketSettings().blocks != null && entity.getRocketSettings().blocks.length > 0) {
            this.shadowRadius = entity.getRocketSettings().getMaxWidth() * 0.0625f * 1.5f;

            matrices.translate(-0.5f, 0, -0.5f);

            RocketPart[] blocks = entity.getRocketSettings().blocks;

            for (RocketPart block : blocks) {
                matrices.push();
                matrices.translate(block.offset.getX(), block.offset.getY(), block.offset.getZ());
                blockRenderManager.renderBlockAsEntity(block.Block, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
                matrices.pop();
            }
        } else {
            modelLoader.getModelPart(CspMainClient.ROCKET_MODEL_LAYER).render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(getTexture(entity))), light, OverlayTexture.DEFAULT_UV);
        }

        matrices.pop();

        Entity linkedEntity = entity.getLinkedEntity();
        if (linkedEntity != null) {
            this.renderFuse(entity, tickDelta, matrices, vertexConsumers, linkedEntity);
        }
    }

    private <E extends Entity> void renderFuse(RocketEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider provider, E holdingEntity) {
        matrices.push();
        Vec3d vec3d = holdingEntity.getLeashPos(tickDelta);
        double g = MathHelper.lerp(tickDelta, entity.prevX, entity.getX());
        double h = MathHelper.lerp(tickDelta, entity.prevY, entity.getY());
        double i = MathHelper.lerp(tickDelta, entity.prevZ, entity.getZ());
        float j = (float)(vec3d.x - g);
        float k = (float)(vec3d.y - h);
        float l = (float)(vec3d.z - i);
        VertexConsumer vertexConsumer = provider.getBuffer(RenderLayer.getLeash());
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        float n = MathHelper.fastInverseSqrt(j * j + l * l) * 0.025F / 2.0F;
        float o = l * n;
        float p = j * n;
        BlockPos blockPos = new BlockPos(entity.getCameraPosVec(tickDelta));
        BlockPos blockPos2 = new BlockPos(holdingEntity.getCameraPosVec(tickDelta));
        int q = this.getBlockLight(entity, blockPos);

        int s = entity.world.getLightLevel(LightType.SKY, blockPos);
        int t = entity.world.getLightLevel(LightType.SKY, blockPos2);

        int u;
        for(u = 0; u <= 24; ++u) {
            renderFusePiece(vertexConsumer, matrix4f, j, k, l, q, s,0.025F, o, p, u);
        }

        for(u = 24; u >= 0; --u) {
            renderFusePiece(vertexConsumer, matrix4f, j, k, l, q, s, 0.0F, o, p, u);
        }

        matrices.pop();
    }

    private static void renderFusePiece(VertexConsumer vertexConsumer, Matrix4f positionMatrix, float f, float g, float h, int EntityBlockLight, int EntitySkyLight, float j, float k, float l, int pieceIndex) {
        float m = (float)pieceIndex / 24.0F;
        int p = LightmapTextureManager.pack(EntityBlockLight, EntitySkyLight);

        float u = f * m;
        float v = g > 0.0F ? g * m * m : g - g * (1.0F - m) * (1.0F - m);
        float w = h * m;
        vertexConsumer.vertex(positionMatrix, u - k, v + j, w + l).color(0.25f, 0.25f, 0.25f, 1.0F).light(p).next();
        vertexConsumer.vertex(positionMatrix, u + k, v + (float) 0.025 - j, w - l).color(0.2f, 0.2f, 0.2f, 1.0F).light(p).next();
    }

    @Override
    public Identifier getTexture(RocketEntity entity) {
        return new Identifier("csp", "textures/entity/rocket.png");
    }
}
