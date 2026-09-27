package potatowolfie.earth_and_water.entity.item.spiked_shield;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.object.equipment.ShieldModel;

// Made with Blockbench 4.12.4

@Environment(EnvType.CLIENT)
public class SpikedShieldEntityModel extends ShieldModel {
	private final ModelPart plate;
	private final ModelPart handle;
	private final ModelPart spikes;

	public SpikedShieldEntityModel(ModelPart root) {
		super(root);
		this.plate = root.getChild("plate");
		this.handle = root.getChild("handle");
		this.spikes = root.getChild("spikes");
	}

	public static LayerDefinition createLayer() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();

		PartDefinition plate = modelPartData.addOrReplaceChild("plate", CubeListBuilder.create()
						.texOffs(0, 0).addBox(-12.0F, -23.0F, 0.0F, 12.0F, 22.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(26, 0).addBox(-7.0F, -15.0F, 1.0F, 2.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
						.texOffs(0, 23).addBox(-10.0F, -21.0F, -0.01F, 2.0F, 2.0F, 0.02F, new CubeDeformation(0.0F))
						.texOffs(0, 23).mirror().addBox(-4.0F, -21.0F, -0.01F, 2.0F, 2.0F, 0.02F, new CubeDeformation(0.0F)).mirror(false)
						.texOffs(0, 25).addBox(-10.0F, -16.0F, -0.01F, 2.0F, 2.0F, 0.02F, new CubeDeformation(0.0F))
						.texOffs(0, 25).mirror().addBox(-4.0F, -16.0F, -0.01F, 2.0F, 2.0F, 0.02F, new CubeDeformation(0.0F)).mirror(false)
						.texOffs(0, 27).addBox(-10.0F, -10.0F, -0.01F, 2.0F, 2.0F, 0.02F, new CubeDeformation(0.0F))
						.texOffs(0, 27).mirror().addBox(-4.0F, -10.0F, -0.01F, 2.0F, 2.0F, 0.02F, new CubeDeformation(0.0F)).mirror(false)
						.texOffs(0, 29).addBox(-10.0F, -5.0F, -0.01F, 2.0F, 2.0F, 0.02F, new CubeDeformation(0.0F))
						.texOffs(0, 29).mirror().addBox(-4.0F, -5.0F, -0.01F, 2.0F, 2.0F, 0.02F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

		PartDefinition spikes = modelPartData.addOrReplaceChild("spikes", CubeListBuilder.create()
						.texOffs(4, 23).addBox(-3.02F, -21.0F, -3.0F, 0.02F, 2.0F, 3.0F, new CubeDeformation(0.0F))
						.texOffs(4, 23).addBox(-9.02F, -21.0F, -3.0F, 0.02F, 2.0F, 3.0F, new CubeDeformation(0.0F))
						.texOffs(4, 23).addBox(-9.02F, -16.0F, -3.0F, 0.02F, 2.0F, 3.0F, new CubeDeformation(0.0F))
						.texOffs(4, 23).addBox(-3.02F, -16.0F, -3.0F, 0.02F, 2.0F, 3.0F, new CubeDeformation(0.0F))
						.texOffs(10, 23).addBox(-9.02F, -10.0F, -3.0F, 0.02F, 2.0F, 3.0F, new CubeDeformation(0.0F))
						.texOffs(10, 23).addBox(-3.02F, -10.0F, -3.0F, 0.02F, 2.0F, 3.0F, new CubeDeformation(0.0F))
						.texOffs(10, 23).addBox(-3.02F, -5.0F, -3.0F, 0.02F, 2.0F, 3.0F, new CubeDeformation(0.0F))
						.texOffs(10, 23).addBox(-9.02F, -5.0F, -3.0F, 0.02F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

		PartDefinition handle = modelPartData.addOrReplaceChild("handle", CubeListBuilder.create(),
				PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

		return LayerDefinition.create(modelData, 64, 64);
	}

	/*
	public void render(ItemStack stack, PoseStack matrices, MultiBufferSource vertexConsumers,
					   int light, int overlay, SpriteId baseSprite, SpriteId noPatternSprite,
					   SpriteGetter spriteHolder) {
		SpriteId spriteToUse = (noPatternSprite != null) ? noPatternSprite : baseSprite;

		VertexConsumer vertexConsumer = spriteHolder.get(spriteToUse).wrap(
				vertexConsumers.getBuffer(RenderTypes.armorCutoutNoCull(spriteToUse.atlasLocation()))
		);

		this.plate.render(matrices, vertexConsumer, light, overlay);
		this.handle.render(matrices, vertexConsumer, light, overlay);
		this.spikes.render(matrices, vertexConsumer, light, overlay);
	}

	 */

	@Override
	public ModelPart plate() {
		return this.plate;
	}

	@Override
	public ModelPart handle() {
		return this.handle;
	}

	public ModelPart getSpikes() {
		return this.spikes;
	}
}