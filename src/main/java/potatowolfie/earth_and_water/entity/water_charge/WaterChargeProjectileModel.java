package potatowolfie.earth_and_water.entity.water_charge;

// Made with Blockbench 4.12.4
// Exported for Minecraft version 1.17+ for Yarn

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class WaterChargeProjectileModel extends EntityModel<WaterChargeProjectileEntity> {
	private final ModelPart water_charge;
	public WaterChargeProjectileModel(ModelPart root) {
		this.water_charge = root.getChild("water_charge");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData water_charge = modelPartData.addChild("water_charge", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F))
				.uv(0, 8).cuboid(-2.0F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.25F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData cube_r1 = water_charge.addChild("cube_r1", ModelPartBuilder.create().uv(9, 0).cuboid(-1.0F, 3.2321F, -1.4019F, 2.0F, 0.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, -2.0944F));

		ModelPartData cube_r2 = water_charge.addChild("cube_r2", ModelPartBuilder.create().uv(9, 0).cuboid(-1.0F, 3.2321F, -1.4019F, 2.0F, 0.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 2.0944F, 0.0F, 0.0F));

		ModelPartData cube_r3 = water_charge.addChild("cube_r3", ModelPartBuilder.create().uv(9, 0).cuboid(-1.0F, -3.2321F, -1.4019F, 2.0F, 0.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, -1.0472F));

		ModelPartData cube_r4 = water_charge.addChild("cube_r4", ModelPartBuilder.create().uv(9, 0).cuboid(-1.0F, -3.2321F, -1.4019F, 2.0F, 0.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

		return TexturedModelData.of(modelData, 32, 32);
	}
	@Override
	public void setAngles(WaterChargeProjectileEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
		water_charge.render(matrices, vertexConsumer, light, overlay, color);
	}
}