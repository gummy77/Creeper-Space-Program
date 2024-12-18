package org.gum.csp.client.renderer;

import net.minecraft.block.BlockState;
import net.minecraft.client.font.TextRenderer;
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
import org.gum.csp.client.registries.ModelRegistry;
import org.gum.csp.datastructs.RocketPart;
import org.gum.csp.datastructs.RocketSettings;
import org.gum.csp.entity.RocketEntity;

public class RocketEntityRenderer extends EntityRenderer<RocketEntity> {

    private final EntityModelLoader modelLoader;
    private final BlockRenderManager blockRenderManager;
    private final TextRenderer textRenderer;

    public RocketEntityRenderer(EntityRendererFactory.Context context){
        super(context);
        modelLoader = context.getModelLoader();
        blockRenderManager = context.getBlockRenderManager();
        textRenderer = context.getTextRenderer();

        this.shadowRadius = 0.5f;
        this.shadowOpacity = 0.5f;
    }

    @Override
    public boolean shouldRender(RocketEntity entity, Frustum frustum, double x, double y, double z) {
        return true; //TODO make this work (below code doesnt)
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


        /* damage wobble here probably

        float wobbleRotation = entity.getWobble();
        matrices.multiply(Quaternion.fromEuler(0, wobbleRotation, 0));
        */

        if(entity.getRocketSettings().blocks != null && entity.getRocketSettings().blocks.length > 0) {
            this.shadowRadius = entity.getRocketSettings().getMaxWidth() * 0.0625f * 1.5f;

            matrices.translate(-0.5f, 0, -0.5f);

            RocketPart[] blocks = entity.getRocketSettings().blocks;

            for (RocketPart block : blocks) {
                matrices.push();
                matrices.translate(block.offset.getX(), block.offset.getY(), block.offset.getZ());
                BlockState state = block.block;
                blockRenderManager.renderBlockAsEntity(state, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
                matrices.pop();
            }
        } else {
            modelLoader.getModelPart(ModelRegistry.ROCKET_MODEL_LAYER).render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(getTexture(entity))), light, OverlayTexture.DEFAULT_UV);
        }

        matrices.pop();

        int infoTime = entity.getRenderInfo();
        if(infoTime != 0) {
            renderRocketStats(entity, tickDelta, matrices, vertexConsumers, infoTime);
        }

        Entity linkedEntity = entity.getLinkedEntity();
        if (linkedEntity != null) {
            this.renderFuse(entity, tickDelta, matrices, vertexConsumers, linkedEntity);
        }
    }

    private void renderRocketStats(RocketEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int infoTime) {
        RocketSettings rocketSettings = entity.getRocketSettings();
        if(rocketSettings != null) {

            Vec3d playerPos = entity.getInfoLinkedPlayer().getPos();
            Vec3d rocketPos = entity.getPos();

            double x = playerPos.x - rocketPos.x;
            double y = playerPos.z - rocketPos.z;

            double lerpSpeed = tickDelta * 0.1;

            double currentRotation = entity.getInfoRenderAngle();
            double newRotation = Math.atan2(x, y);


            double delta = newRotation - currentRotation;
            delta = MathHelper.clamp(delta - (Math.floor(delta / Math.PI) * Math.PI), 0f, Math.PI);

            if(delta > Math.PI/2) delta -= Math.PI;

            double rotation = currentRotation + (delta * lerpSpeed);


            entity.setInfoRenderAngle(rotation);

            int alpha = 0;
            if(infoTime > 20) {
                alpha = 255;
            } else {
                alpha = (int)(255f * ((float)infoTime / 20f));
            }
            int Color = ColorHelper.Argb.getArgb(alpha, 255, 255,255);

            matrices.push();
            matrices.multiply(Quaternion.fromEulerXyz(0, (float) (rotation - 0.35f), 0));
            matrices.translate(0.5f, 3 , 0);
            matrices.scale(0.025f, -0.025f, 0.025f);

            if(rocketSettings.payload != null) {
                matrices.translate(0, -20 , 0);
            }

            matrices.push();
            matrices.scale(1.5f, 1.5f , 1.5f);
            textRenderer.draw(matrices, rocketSettings.getRocketTitle(), 0, -5, Color);
            matrices.pop();

            textRenderer.draw(matrices, "Power: " + rocketSettings.Power + "N", 0, 10, Color);
            textRenderer.draw(matrices, "Total Mass: " + rocketSettings.Mass + "kg", 0, 20, Color);
            textRenderer.draw(matrices, "Acceleration: " + (float)((int)(rocketSettings.Power / rocketSettings.Mass * 100f)) / 100f + "m/s", 0, 30, Color);

            textRenderer.draw(matrices, "Burn Time: " + rocketSettings.burnTime + "s", 0, 45, Color);
            textRenderer.draw(matrices, "Estimated Height: " + (int) (RocketEntity.calculateMaxHeight(rocketSettings)) + "m", 0, 55, Color);
            textRenderer.draw(matrices, "Chance of Failure: " + rocketSettings.Volatility + "%", 0, 65, Color);

            if(rocketSettings.payload != null) {
                matrices.push();
                matrices.scale(1.25f, 1.25f , 1.25f);
                textRenderer.draw(matrices, "Payload: " + rocketSettings.payload.getDisplayName(), 0, 65, Color);
                matrices.pop();
                textRenderer.draw(matrices, "Payload Mass: " + rocketSettings.payload.getMass() + "kg", 0, 95, Color);
                if(rocketSettings.payload.canBeTracked())
                    textRenderer.draw(matrices, "Estimated Travel Distance: " + (int) (RocketEntity.calculateMaxHeight(rocketSettings) * 0.15f) + "m", 0, 105, Color);
            }

            matrices.pop();
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
