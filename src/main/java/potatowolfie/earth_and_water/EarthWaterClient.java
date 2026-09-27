package potatowolfie.earth_and_water;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import potatowolfie.earth_and_water.block.ModBlocks;
import potatowolfie.earth_and_water.block.entity.ModBlockEntities;
import potatowolfie.earth_and_water.block.entity.client.ReinforcedSpawnerBlockEntityRenderer;
import potatowolfie.earth_and_water.entity.ModEntities;
import potatowolfie.earth_and_water.entity.ModEntityModelLayers;
import potatowolfie.earth_and_water.entity.bore.BoreEntityModel;
import potatowolfie.earth_and_water.entity.bore.BoreEntityRenderer;
import potatowolfie.earth_and_water.entity.brine.BrineEntityModel;
import potatowolfie.earth_and_water.entity.brine.BrineEntityRenderer;
import potatowolfie.earth_and_water.entity.item.spiked_shield.SpikedShieldEntityModel;
import potatowolfie.earth_and_water.entity.item.spiked_shield.SpikedShieldModelRenderer;
import potatowolfie.earth_and_water.entity.earth_charge.EarthChargeProjectileModel;
import potatowolfie.earth_and_water.entity.earth_charge.EarthChargeProjectileRenderer;
import potatowolfie.earth_and_water.entity.water_charge.WaterChargeProjectileModel;
import potatowolfie.earth_and_water.entity.water_charge.WaterChargeProjectileRenderer;
import potatowolfie.earth_and_water.particle.ReinforcedSpawnerDetectionParticle;
import potatowolfie.earth_and_water.particle.ReinforcedSpawnerDetectionParticleInner;
import potatowolfie.earth_and_water.particle.ReinforcedSpawnerOutwardParticle;

public class EarthWaterClient implements ClientModInitializer {

    public static final Identifier SPIKED_BANNER_SHIELD_TYPE =
            Identifier.fromNamespaceAndPath(EarthWater.MOD_ID, "spiked_banner_shield");

    public static final ModelLayerLocation SPIKED_SHIELD_MODEL_LAYER = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(EarthWater.MOD_ID, "spiked_shield"), "main"
    );

    public static final SpriteId SPIKED_SHIELD_BASE =
            new SpriteId(
                    Sheets.SHIELD_SHEET,
                    Identifier.fromNamespaceAndPath(EarthWater.MOD_ID, "entity/shield/spiked_shield_base")
            );

    public static final SpriteId SPIKED_SHIELD_BASE_NO_PATTERN =
            new SpriteId(
                    Sheets.SHIELD_SHEET,
                    Identifier.fromNamespaceAndPath(EarthWater.MOD_ID, "entity/shield/spiked_shield_base_nopattern")
            );

    public static SpikedShieldEntityModel spikedShieldModel;

    @Override
    public void onInitializeClient() {
        ParticleProviderRegistry.getInstance().register(EarthWater.LIGHT_UP, EndRodParticle.Provider::new);
        ParticleProviderRegistry.getInstance().register(EarthWater.REINFORCED_SPAWNER_DETECTION, ReinforcedSpawnerDetectionParticle.Factory::new);
        ParticleProviderRegistry.getInstance().register(EarthWater.REINFORCED_SPAWNER_DETECTION_OUTWARD, ReinforcedSpawnerOutwardParticle.Factory::new);
        ParticleProviderRegistry.getInstance().register(EarthWater.REINFORCED_SPAWNER_DETECTION_INNER, ReinforcedSpawnerDetectionParticleInner.Factory::new);

        EntityRendererRegistry.register(ModEntities.EARTH_CHARGE, EarthChargeProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.WATER_CHARGE, WaterChargeProjectileRenderer::new);

        ModelLayerRegistry.registerModelLayer(
                ModEntityModelLayers.EARTH_CHARGE,
                EarthChargeProjectileModel::getTexturedModelData
        );
        ModelLayerRegistry.registerModelLayer(
                ModEntityModelLayers.WATER_CHARGE,
                WaterChargeProjectileModel::getTexturedModelData
        );

        EntityRendererRegistry.register(ModEntities.BORE, BoreEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.BRINE, BrineEntityRenderer::new);

        ModelLayerRegistry.registerModelLayer(
                ModEntityModelLayers.BORE,
                BoreEntityModel::getTexturedModelData
        );
        ModelLayerRegistry.registerModelLayer(
                ModEntityModelLayers.BRINE,
                BrineEntityModel::getTexturedModelData
        );

        ModelLayerRegistry.registerModelLayer(
                ModEntityModelLayers.SPIKED_SHIELD,
                SpikedShieldEntityModel::createLayer
        );

        SpecialModelRenderers.ID_MAPPER.put(
                SPIKED_BANNER_SHIELD_TYPE,
                (MapCodec) SpikedShieldModelRenderer.Unbaked.CODEC);

        BlockEntityRenderers.register(
                ModBlockEntities.REINFORCED_SPAWNER_BLOCK_ENTITY,
                ReinforcedSpawnerBlockEntityRenderer::new
        );

        ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, tooltipType, list) -> {
            if (itemStack.is(ModBlocks.REINFORCED_SPAWNER.asItem())) {
                list.add(Component.translatable("tooltip.earth-and-water.tooltipempty")
                        .withStyle(ChatFormatting.GRAY));
                list.add(Component.translatable("block.minecraft.spawner.desc1")
                        .withStyle(ChatFormatting.GRAY));
                list.add(Component.translatable("tooltip.earth-and-water.reinforced_spawner.desc2")
                        .withStyle(ChatFormatting.BLUE));
            }
        });
    }

    public static SpikedShieldEntityModel getSpikedShieldModel() {
        if (spikedShieldModel == null) {
            spikedShieldModel = new SpikedShieldEntityModel(Minecraft.getInstance()
                    .getEntityModels()
                    .bakeLayer(SPIKED_SHIELD_MODEL_LAYER));
        }
        return spikedShieldModel;
    }
}