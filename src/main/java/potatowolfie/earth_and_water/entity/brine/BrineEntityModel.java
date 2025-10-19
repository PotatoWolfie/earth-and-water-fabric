package potatowolfie.earth_and_water.entity.brine;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.model.EntityModel;
import potatowolfie.earth_and_water.animation.BrineAnimations;

// Made with Blockbench 4.12.4

@Environment(EnvType.CLIENT)
public class BrineEntityModel extends EntityModel<BrineEntityRenderState> {
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart shell1;
	private final ModelPart shell2;
	private final ModelPart rods_top;
	private final ModelPart rods_bottom;
	private final ModelPart eyes;
	private final Animation idlingAnimation;
	private final Animation underwateridlingAnimation;
	private final Animation shootingAnimation;

	public BrineEntityModel(ModelPart modelPart) {
		super(modelPart);
		this.head = modelPart.getChild("head");
		this.body = modelPart.getChild("body");
		this.shell1 = this.body.getChild("shell1");
		this.shell2 = this.body.getChild("shell2");
		this.rods_top = this.body.getChild("rods_top");
		this.rods_bottom = this.body.getChild("rods_bottom");
		this.eyes = this.head.getChild("eyes");
		this.idlingAnimation = BrineAnimations.BRINE_IDLE.createAnimation(modelPart);
		this.underwateridlingAnimation = BrineAnimations.BRINE_UNDERWATER.createAnimation(modelPart);
		this.shootingAnimation = BrineAnimations.BRINE_SHOOTING.createAnimation(modelPart);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 4.0F, 0.0F));

		head.addChild("eyes", ModelPartBuilder.create()
						.uv(32, 0).cuboid(-4.0F, -8.0F, -4.01F, 8.0F, 8.0F, 0.0F),
				ModelTransform.origin(0.0F, 0.0F, 0.0F));

		ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 24).cuboid(-1.5F, -12.5F, -1.5F, 3.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 19.0F, 0.0F));

		ModelPartData shell1 = body.addChild("shell1", ModelPartBuilder.create().uv(0, 16).cuboid(-1.0F, -2.0F, -2.0F, 2.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(1.5F, -11.0F, 0.0F));

		ModelPartData shell2 = body.addChild("shell2", ModelPartBuilder.create().uv(0, 16).mirrored().cuboid(-1.0F, -2.0F, -2.0F, 2.0F, 4.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.origin(-1.5F, -11.0F, 0.0F));

		ModelPartData rods_top = body.addChild("rods_top", ModelPartBuilder.create(), ModelTransform.origin(0.0F, -13.0F, 0.0F));

		ModelPartData rod1 = rods_top.addChild("rod1", ModelPartBuilder.create().uv(12, 16).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(-6.0F, 4.0F, -6.0F));

		ModelPartData rod2 = rods_top.addChild("rod2", ModelPartBuilder.create().uv(12, 16).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(-6.0F, 4.0F, 6.0F));

		ModelPartData rod3 = rods_top.addChild("rod3", ModelPartBuilder.create().uv(12, 16).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(6.0F, 4.0F, 6.0F));

		ModelPartData rod4 = rods_top.addChild("rod4", ModelPartBuilder.create().uv(12, 16).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(6.0F, 4.0F, -6.0F));

		ModelPartData rods_bottom = body.addChild("rods_bottom", ModelPartBuilder.create(), ModelTransform.origin(0.0F, -4.0F, 0.0F));

		ModelPartData rod5 = rods_bottom.addChild("rod5", ModelPartBuilder.create().uv(20, 16).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 4.0F, -5.0F, -0.1309F, 0.0F, 0.0F));

		ModelPartData rod6 = rods_bottom.addChild("rod6", ModelPartBuilder.create().uv(20, 16).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-5.0F, 4.0F, 0.0F, 0.0F, 0.0F, 0.1309F));

		ModelPartData rod7 = rods_bottom.addChild("rod7", ModelPartBuilder.create().uv(20, 16).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 4.0F, 5.0F, 0.1309F, 0.0F, 0.0F));

		ModelPartData rod8 = rods_bottom.addChild("rod8", ModelPartBuilder.create().uv(20, 16).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(5.0F, 4.0F, 0.0F, 0.0F, 0.0F, -0.1309F));
		return TexturedModelData.of(modelData, 32, 32);
	}

	public void setAngles(BrineEntityRenderState brineEntityRenderState) {
		super.setAngles(brineEntityRenderState);

		this.idlingAnimation.apply(brineEntityRenderState.idleAnimationState, brineEntityRenderState.age);
		this.shootingAnimation.apply(brineEntityRenderState.attackAnimationState, brineEntityRenderState.age);
		this.underwateridlingAnimation.apply(brineEntityRenderState.underwaterAnimationState, brineEntityRenderState.age);
	}

	public ModelPart getHead() {
		return this.head;
	}

	public ModelPart getEyes() {
		return this.eyes;
	}

	public ModelPart getShell1() {
		return this.shell1;
	}

	public ModelPart getShell2() {
		return this.shell2;
	}

	public ModelPart getRodsTop() {
		return this.rods_top;
	}

	public ModelPart getRodsBottom() {
		return this.rods_bottom;
	}
}