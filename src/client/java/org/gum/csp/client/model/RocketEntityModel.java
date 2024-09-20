package org.gum.csp.client.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;
import org.gum.csp.entity.RocketEntity;

public class RocketEntityModel extends EntityModel<RocketEntity> {

    private final ModelPart root;

    public RocketEntityModel(ModelPart root) {
        this.root = root;
    }

    public static TexturedModelData getTexturedModelData () {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-4f, -16f, -4f, 8f, 32f, 8f),
                ModelTransform.pivot(0f, 16f, 0f));


        return TexturedModelData.of(modelData, 16, 16);
    }

    @Override
    public void setAngles(RocketEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.root.render(matrices, vertices, light, overlay);
    }
}
