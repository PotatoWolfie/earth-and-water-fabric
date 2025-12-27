package potatowolfie.earth_and_water.entity.client.spiked_shield;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

// Made with Blockbench 4.12.4

@Environment(EnvType.CLIENT)
public class SpikedShieldEntityModel extends Model {
	private final ModelPart plate;
	private final ModelPart handle;
	private final ModelPart spikes;
	private final ModelPart root;

	public SpikedShieldEntityModel(ModelPart root) {
		super(RenderLayer::getEntityCutoutNoCull);
		this.root = root;
		this.plate = root.getChild("plate");
		this.handle = root.getChild("handle");
		this.spikes = root.getChild("spikes");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartData plate = modelPartData.addChild("plate", ModelPartBuilder.create()
						.uv(0, 0).cuboid(-12.0F, -23.0F, 0.0F, 12.0F, 22.0F, 1.0F, new Dilation(0.0F))
						.uv(26, 0).cuboid(-7.0F, -15.0F, 1.0F, 2.0F, 6.0F, 6.0F, new Dilation(0.0F))
						.uv(0, 23).cuboid(-10.0F, -21.0F, -0.01F, 2.0F, 2.0F, 0.02F, new Dilation(0.0F))
						.uv(0, 23).mirrored().cuboid(-4.0F, -21.0F, -0.01F, 2.0F, 2.0F, 0.02F, new Dilation(0.0F)).mirrored(false)
						.uv(0, 25).cuboid(-10.0F, -16.0F, -0.01F, 2.0F, 2.0F, 0.02F, new Dilation(0.0F))
						.uv(0, 25).mirrored().cuboid(-4.0F, -16.0F, -0.01F, 2.0F, 2.0F, 0.02F, new Dilation(0.0F)).mirrored(false)
						.uv(0, 27).cuboid(-10.0F, -10.0F, -0.01F, 2.0F, 2.0F, 0.02F, new Dilation(0.0F))
						.uv(0, 27).mirrored().cuboid(-4.0F, -10.0F, -0.01F, 2.0F, 2.0F, 0.02F, new Dilation(0.0F)).mirrored(false)
						.uv(0, 29).cuboid(-10.0F, -5.0F, -0.01F, 2.0F, 2.0F, 0.02F, new Dilation(0.0F))
						.uv(0, 29).mirrored().cuboid(-4.0F, -5.0F, -0.01F, 2.0F, 2.0F, 0.02F, new Dilation(0.0F)).mirrored(false),
				ModelTransform.of(0.0F, 1.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

		ModelPartData spikes = modelPartData.addChild("spikes", ModelPartBuilder.create()
						.uv(4, 23).cuboid(-3.02F, -21.0F, -3.0F, 0.02F, 2.0F, 3.0F, new Dilation(0.0F))
						.uv(4, 23).cuboid(-9.02F, -21.0F, -3.0F, 0.02F, 2.0F, 3.0F, new Dilation(0.0F))
						.uv(4, 23).cuboid(-9.02F, -16.0F, -3.0F, 0.02F, 2.0F, 3.0F, new Dilation(0.0F))
						.uv(4, 23).cuboid(-3.02F, -16.0F, -3.0F, 0.02F, 2.0F, 3.0F, new Dilation(0.0F))
						.uv(10, 23).cuboid(-9.02F, -10.0F, -3.0F, 0.02F, 2.0F, 3.0F, new Dilation(0.0F))
						.uv(10, 23).cuboid(-3.02F, -10.0F, -3.0F, 0.02F, 2.0F, 3.0F, new Dilation(0.0F))
						.uv(10, 23).cuboid(-3.02F, -5.0F, -3.0F, 0.02F, 2.0F, 3.0F, new Dilation(0.0F))
						.uv(10, 23).cuboid(-9.02F, -5.0F, -3.0F, 0.02F, 2.0F, 3.0F, new Dilation(0.0F)),
				ModelTransform.of(0.0F, 1.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

		ModelPartData handle = modelPartData.addChild("handle", ModelPartBuilder.create(),
				ModelTransform.of(0.0F, 1.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		this.plate.render(matrices, vertices, light, overlay, color);
		this.handle.render(matrices, vertices, light, overlay, color);
		this.spikes.render(matrices, vertices, light, overlay, color);
	}

	public ModelPart getPlate() {
		return this.plate;
	}

	public ModelPart getHandle() {
		return this.handle;
	}

	public ModelPart getSpikes() {
		return this.spikes;
	}

	public ModelPart getRoot() {
		return this.root;
	}
}