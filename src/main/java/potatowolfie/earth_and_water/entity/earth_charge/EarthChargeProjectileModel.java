package potatowolfie.earth_and_water.entity.earth_charge;

// Made with Blockbench 4.12.4
// Exported for Minecraft version 1.17+ for Yarn

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class EarthChargeProjectileModel extends EntityModel<EarthChargeProjectileEntity> {
	private final ModelPart earth_charge;
	public EarthChargeProjectileModel(ModelPart root) {
		this.earth_charge = root.getChild("earth_charge");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bb_main = modelPartData.addChild("earth_charge", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData cube_r1 = bb_main.addChild("cube_r1", ModelPartBuilder.create().uv(0, 8).mirrored().cuboid(2.0F, -2.9142F, -1.4142F, 3.0F, 3.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
				.uv(0, 8).cuboid(-5.0F, -2.9142F, -1.4142F, 3.0F, 3.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.7854F));

		ModelPartData cube_r2 = bb_main.addChild("cube_r2", ModelPartBuilder.create().uv(0, 8).mirrored().cuboid(2.0F, -2.9142F, 1.4142F, 3.0F, 3.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
				.uv(0, 8).cuboid(-5.0F, -2.9142F, 1.4142F, 3.0F, 3.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, -0.7854F));

		ModelPartData cube_r3 = bb_main.addChild("cube_r3", ModelPartBuilder.create().uv(0, 8).mirrored().cuboid(2.0F, -2.9142F, 1.4142F, 3.0F, 3.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
				.uv(0, 8).cuboid(-5.0F, -2.9142F, 1.4142F, 3.0F, 3.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData cube_r4 = bb_main.addChild("cube_r4", ModelPartBuilder.create().uv(0, 8).mirrored().cuboid(2.0F, -2.9142F, -1.4142F, 3.0F, 3.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
				.uv(0, 8).cuboid(-5.0F, -2.9142F, -1.4142F, 3.0F, 3.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 16, 16);
	}
	@Override
	public void setAngles(EarthChargeProjectileEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
		earth_charge.render(matrices, vertexConsumer, light, overlay, color);
	}
}