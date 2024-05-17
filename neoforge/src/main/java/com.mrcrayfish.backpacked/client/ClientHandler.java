package com.mrcrayfish.backpacked.client;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import com.mrcrayfish.backpacked.client.model.ModelInstances;
import com.mrcrayfish.backpacked.client.model.backpack.*;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.BackpackLayer;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.ShelfRenderer;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.VillagerBackpackLayer;
import com.mrcrayfish.backpacked.core.ModBlockEntities;
import com.mrcrayfish.backpacked.core.ModContainers;
import com.mrcrayfish.backpacked.core.ModLayerDefinitions;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.WanderingTraderRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientHandler
{
    @SubscribeEvent
    private static void onClientSetup(FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> {
            ClientBootstrap.init();
            if(!FMLLoader.isProduction()) {
                NeoForge.EVENT_BUS.register(new PickpocketDebugRenderer());
            }
        });
    }

    @SubscribeEvent
    private static void onRegisterMenuScreens(RegisterMenuScreensEvent event)
    {
        event.register(ModContainers.BACKPACK.get(), BackpackScreen::new);
    }

    @SubscribeEvent
    private static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(ModLayerDefinitions.STANDARD, StandardBackpackModel::createLayer);
        event.registerLayerDefinition(ModLayerDefinitions.CLASSIC, ClassicBackpackModel::createLayer);
        event.registerLayerDefinition(ModLayerDefinitions.BAMBOO_BASKET, BambooBasketBackpackModel::createLayer);
        event.registerLayerDefinition(ModLayerDefinitions.ROCKET, RocketBackpackModel::createLayer);
        event.registerLayerDefinition(ModLayerDefinitions.MINI_CHEST, MiniChestBackpackModel::createLayer);
        event.registerLayerDefinition(ModLayerDefinitions.TRASH_CAN, TrashCanBackpackModel::createLayer);
        event.registerLayerDefinition(ModLayerDefinitions.HONEY_JAR, HoneyJarBackpackModel::createLayer);
        event.registerLayerDefinition(ModLayerDefinitions.TURTLE_SHELL, TurtleShellBackpackModel::createLayer);
        event.registerLayerDefinition(ModLayerDefinitions.CARDBOARD_BOX, CardboardBoxBackpackModel::createLayer);
        event.registerLayerDefinition(ModLayerDefinitions.SHEEP_PLUSH, SheepPlushBackpackModel::createLayer);
        event.registerLayerDefinition(ModLayerDefinitions.WANDERING_BAG, WanderingBagBackpackModel::createLayer);
        event.registerLayerDefinition(ModLayerDefinitions.PIGLIN_PACK, PiglinPackBackpackModel::createLayer);
        event.registerLayerDefinition(ModLayerDefinitions.END_CRYSTAL, EndCrystalBackpackModel::createLayer);
        event.registerLayerDefinition(ModLayerDefinitions.COGWHEEL, CogwheelBackpackModel::createLayer);
    }

    @SubscribeEvent
    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(ModBlockEntities.SHELF.get(), ShelfRenderer::new);
    }

    @SubscribeEvent
    private static void onAddLayers(EntityRenderersEvent.AddLayers event)
    {
        addBackpackLayer(event.getSkin(PlayerSkin.Model.WIDE));
        addBackpackLayer(event.getSkin(PlayerSkin.Model.SLIM));

        EntityRenderer<?> renderer = event.getRenderer(EntityType.WANDERING_TRADER);
        if(renderer instanceof WanderingTraderRenderer traderRenderer)
        {
            traderRenderer.addLayer(new VillagerBackpackLayer<>(traderRenderer));
        }

        ModelInstances.get().loadModels(event.getEntityModels());
    }

    private static void addBackpackLayer(EntityRenderer<?> renderer)
    {
        if(renderer instanceof PlayerRenderer playerRenderer)
        {
            playerRenderer.addLayer(new BackpackLayer<>(playerRenderer));
        }
    }
}
