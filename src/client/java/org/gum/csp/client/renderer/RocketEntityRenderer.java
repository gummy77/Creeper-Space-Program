package org.gum.csp.client.renderer;

import com.sun.jna.platform.win32.WinBase;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.LightType;
import org.gum.csp.client.CspMainClient;
import org.gum.csp.client.model.RocketEntityModel;
import org.gum.csp.entity.RocketEntity;

public class RocketEntityRenderer extends EntityRenderer<RocketEntity> {

    private final EntityModelLoader modelLoader;
    public Vec3d previousRenderPosition = new Vec3d(0, 0, 0);

    public RocketEntityRenderer(EntityRendererFactory.Context context){
        super(context);
        modelLoader = context.getModelLoader();
    }

    @Override
    public boolean shouldRender(RocketEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
//        Box box = new Box(x, y, z, x + entity.getWidth(), y + entity.getHeight(), z + entity.getWidth());
//        box.offset(entity.renderPosition.getX(), entity.renderPosition.getY(), entity.renderPosition.getZ());
//        return frustum.isVisible(box);
    }



    @Override
    public void render(RocketEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();


        Vec3d rotationVector = entity.getVelocity();
        Vec3d UP = new Vec3d(0,1,0);
        Vec3d xAxis = UP.crossProduct(rotationVector);
        float w = (float) (Math.sqrt(rotationVector.length() * rotationVector.length()) + UP.dotProduct(rotationVector));

        Quaternion rotation = new Quaternion((float) xAxis.x, (float) xAxis.y, (float) xAxis.z, w);
        rotation.normalize();

        matrices.multiply(rotation);

        modelLoader.getModelPart(CspMainClient.ROCKET_MODEL_LAYER).render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(getTexture(entity))), light, 0);

        matrices.pop();

        Entity linkedEntity = entity.getLinkedEntity();
        if (linkedEntity != null) {
            this.renderFuse(entity, tickDelta, matrices, vertexConsumers, linkedEntity, light);
        }
    }

    private <E extends Entity> void renderFuse(RocketEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider provider, E holdingEntity, int light) {
        matrices.push();
        Vec3d vec3d = holdingEntity.getLeashPos(tickDelta);
        double g = MathHelper.lerp((double)tickDelta, entity.prevX, entity.getX());
        double h = MathHelper.lerp((double)tickDelta, entity.prevY, entity.getY());
        double i = MathHelper.lerp((double)tickDelta, entity.prevZ, entity.getZ());
        float j = (float)(vec3d.x - g);
        float k = (float)(vec3d.y - h);
        float l = (float)(vec3d.z - i);
        float m = 0.025F;
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
            renderFusePiece(vertexConsumer, matrix4f, j, k, l, q, light, s, t, 0.025F, 0.025F, o, p, u, false);
        }

        for(u = 24; u >= 0; --u) {
            renderFusePiece(vertexConsumer, matrix4f, j, k, l, q, light, s, t, 0.025F, 0.0F, o, p, u, true);
        }

        matrices.pop();
    }

    private static void renderFusePiece(VertexConsumer vertexConsumer, Matrix4f positionMatrix, float f, float g, float h, int EntityBlockLight, int linkedEntityBlockLight, int EntitySkyLight, int linkedEntitySkyLight, float i, float j, float k, float l, int pieceIndex, boolean isLeashKnot) {
        float m = (float)pieceIndex / 24.0F;
        int n = (int)MathHelper.lerp(m, (float)EntityBlockLight, (float)linkedEntityBlockLight);
        int o = (int)MathHelper.lerp(m, (float)EntitySkyLight, (float)linkedEntitySkyLight);
        int p = LightmapTextureManager.pack(n, o);
        float u = f * m;
        float v = g > 0.0F ? g * m * m : g - g * (1.0F - m) * (1.0F - m);
        float w = h * m;
        vertexConsumer.vertex(positionMatrix, u - k, v + j, w + l).color(0, 0, 0, 1.0F).light(p).next();
        vertexConsumer.vertex(positionMatrix, u + k, v + i - j, w - l).color(0, 0, 0, 1.0F).light(p).next();
    }

    @Override
    public Identifier getTexture(RocketEntity entity) {
        return new Identifier("csp", "textures/entity/rocket.png");
    }
}
