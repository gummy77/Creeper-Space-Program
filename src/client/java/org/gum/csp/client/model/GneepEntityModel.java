package org.gum.csp.client.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.gum.csp.entity.GneepEntity;

// Made with Blockbench 4.11.1
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class GneepEntityModel extends EntityModel<GneepEntity> {
	private final ModelPart arm_r;
	private final ModelPart arm_l;
	private final ModelPart leg_r;
	private final ModelPart leg_l;
	private final ModelPart head;
	private final ModelPart bb_main;

	public GneepEntityModel(ModelPart root) {
		this.arm_r = root.getChild("arm_r");
		this.arm_l = root.getChild("arm_l");
		this.leg_r = root.getChild("leg_r");
		this.leg_l = root.getChild("leg_l");
		this.head = root.getChild("head");
		this.bb_main = root.getChild("bb_main");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData arm_r = modelPartData.addChild("arm_r", ModelPartBuilder.create().uv(0, 18).cuboid(-2.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-2.0F, 15.0F, 0.0F, 0.0F, 0.0F, 0.2618F));

		ModelPartData arm_l = modelPartData.addChild("arm_l", ModelPartBuilder.create().uv(20, 0).cuboid(0.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, 15.0F, 0.0F, 0.0F, 0.0F, -0.2618F));

		ModelPartData leg_r = modelPartData.addChild("leg_r", ModelPartBuilder.create().uv(12, 11).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 20.0F, 0.0F));

		ModelPartData leg_l = modelPartData.addChild("leg_l", ModelPartBuilder.create().uv(12, 17).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(1.0F, 20.0F, 0.0F));

		ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -7.0F, -2.0F, 6.0F, 7.0F, 4.0F, new Dilation(0.0F))
		.uv(20, 6).cuboid(-4.0F, -11.0F, 0.0F, 3.0F, 4.0F, 0.0F, new Dilation(0.0F))
		.uv(20, 10).cuboid(1.0F, -11.0F, 0.0F, 3.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 15.0F, 0.0F));

		ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 11).cuboid(-2.0F, -9.0F, -1.0F, 4.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		return TexturedModelData.of(modelData, 32, 32);
	}
	@Override
	public void setAngles(GneepEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.head.pitch = headPitch * 0.017453292F;
		this.head.yaw = netHeadYaw * 0.017453292F;
	}
	public void animateModel(GneepEntity gneepEntity, float limbAngle, float limbDistance, float tickDelta) {
		this.leg_l.pitch = MathHelper.cos(limbAngle * 0.6662F + 3.1415927F) * 1.4F * limbDistance;
		this.leg_r.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		arm_r.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		arm_l.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		leg_r.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		leg_l.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		bb_main.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}