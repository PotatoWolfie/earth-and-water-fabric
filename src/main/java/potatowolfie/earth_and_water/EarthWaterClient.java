package potatowolfie.earth_and_water;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import potatowolfie.earth_and_water.block.ModBlocks;
import potatowolfie.earth_and_water.entity.ModEntities;
import potatowolfie.earth_and_water.entity.bore.BoreEntityModel;
import potatowolfie.earth_and_water.entity.bore.BoreEntityRenderer;
import potatowolfie.earth_and_water.entity.brine.BrineEntityModel;
import potatowolfie.earth_and_water.entity.brine.BrineEntityRenderer;
import potatowolfie.earth_and_water.entity.client.*;
import potatowolfie.earth_and_water.entity.client.spiked_shield.SpikedShieldEntityModel;
import potatowolfie.earth_and_water.entity.client.spiked_shield.SpikedShieldItemRenderer;
import potatowolfie.earth_and_water.entity.client.spiked_shield.SpikedShieldRenderer;
import potatowolfie.earth_and_water.entity.earth_charge.EarthChargeProjectileModel;
import potatowolfie.earth_and_water.entity.earth_charge.EarthChargeProjectileRenderer;
import potatowolfie.earth_and_water.entity.water_charge.WaterChargeProjectileModel;
import potatowolfie.earth_and_water.entity.water_charge.WaterChargeProjectileRenderer;
import potatowolfie.earth_and_water.item.ModItems;
import potatowolfie.earth_and_water.particle.ReinforcedSpawnerDetectionParticle;
import potatowolfie.earth_and_water.particle.ReinforcedSpawnerDetectionParticleInner;
import potatowolfie.earth_and_water.particle.ReinforcedSpawnerOutwardParticle;

public class EarthWaterClient implements ClientModInitializer {

    public static final EntityModelLayer SPIKED_SHIELD_MODEL_LAYER = new EntityModelLayer(
            Identifier.of(EarthWater.MOD_ID, "spiked_shield"), "main"
    );

    public static final SpriteIdentifier SPIKED_SHIELD_BASE = new SpriteIdentifier(
            TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE,
            Identifier.of(EarthWater.MOD_ID, "entity/shield/spiked_shield_base")
    );

    public static final SpriteIdentifier SPIKED_SHIELD_BASE_NO_PATTERN = new SpriteIdentifier(
            TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE,
            Identifier.of(EarthWater.MOD_ID, "entity/shield/spiked_shield_base_nopattern")
    );

    public static SpikedShieldEntityModel spikedShieldModel;

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.OXYGEN_BUBBLE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.REINFORCED_SPAWNER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POINTED_DARK_DRIPSTONE, RenderLayer.getCutout());
        ParticleFactoryRegistry.getInstance().register(EarthWater.LIGHT_UP, EndRodParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(EarthWater.REINFORCED_SPAWNER_DETECTION, ReinforcedSpawnerDetectionParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(EarthWater.REINFORCED_SPAWNER_DETECTION_OUTWARD, ReinforcedSpawnerOutwardParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(EarthWater.REINFORCED_SPAWNER_DETECTION_INNER, ReinforcedSpawnerDetectionParticleInner.Factory::new);

        EntityModelLayerRegistry.registerModelLayer(ModEntityModelLayers.EARTH_CHARGE, EarthChargeProjectileModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.EARTH_CHARGE, EarthChargeProjectileRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModEntityModelLayers.WATER_CHARGE, WaterChargeProjectileModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.WATER_CHARGE, WaterChargeProjectileRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(ModEntityModelLayers.BORE, BoreEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.BORE, BoreEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModEntityModelLayers.BRINE, BrineEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.BRINE, BrineEntityRenderer::new);

        ModelPredicateProviderRegistry.register(
                ModItems.SPIKED_SHIELD,
                Identifier.of("minecraft", "blocking"),
                (stack, world, entity, seed) -> entity instanceof LivingEntity && entity.isUsingItem() &&
                        entity.getActiveItem() == stack ? 1.0F : 0.0F
        );

        EntityModelLayerRegistry.registerModelLayer(SPIKED_SHIELD_MODEL_LAYER, SpikedShieldEntityModel::getTexturedModelData);

        BuiltinItemRendererRegistry.INSTANCE.register(
                ModItems.SPIKED_SHIELD,
                (stack, mode, matrices, vertexConsumers, light, overlay) -> {
                    SpikedShieldRenderer renderer = new SpikedShieldRenderer(EarthWaterClient.getSpikedShieldModel());
                    SpikedShieldItemRenderer itemRenderer = new SpikedShieldItemRenderer(renderer);
                    itemRenderer.render(stack, mode, matrices, vertexConsumers, light, overlay);
                }
        );
    }

    public static SpikedShieldEntityModel getSpikedShieldModel() {
        if (spikedShieldModel == null) {
            spikedShieldModel = new SpikedShieldEntityModel(MinecraftClient.getInstance()
                    .getEntityModelLoader()
                    .getModelPart(SPIKED_SHIELD_MODEL_LAYER));
        }
        return spikedShieldModel;
    }
}