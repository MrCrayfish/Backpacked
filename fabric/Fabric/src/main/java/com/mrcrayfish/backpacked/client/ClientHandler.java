package com.mrcrayfish.backpacked.client;

import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import com.mrcrayfish.backpacked.client.model.ModelInstances;
import com.mrcrayfish.backpacked.client.model.backpack.*;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.BackpackLayer;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.ShelfRenderer;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.VillagerBackpackLayer;
import com.mrcrayfish.backpacked.core.ModBlockEntities;
import com.mrcrayfish.backpacked.core.ModContainers;
import com.mrcrayfish.backpacked.core.ModLayerDefinitions;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.WanderingTraderRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

/**
 * Author: MrCrayfish
 */
public class ClientHandler implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ClientBootstrap.init();
        MenuScreens.register(ModContainers.BACKPACK.get(), BackpackScreen::new);
        BlockEntityRenderers.register(ModBlockEntities.SHELF.get(), ShelfRenderer::new);

        // Register model layers
        EntityModelLayerRegistry.registerModelLayer(ModLayerDefinitions.STANDARD, StandardBackpackModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(ModLayerDefinitions.CLASSIC, ClassicBackpackModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(ModLayerDefinitions.BAMBOO_BASKET, BambooBasketBackpackModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(ModLayerDefinitions.ROCKET, RocketBackpackModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(ModLayerDefinitions.MINI_CHEST, MiniChestBackpackModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(ModLayerDefinitions.TRASH_CAN, TrashCanBackpackModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(ModLayerDefinitions.HONEY_JAR, HoneyJarBackpackModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(ModLayerDefinitions.TURTLE_SHELL, TurtleShellBackpackModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(ModLayerDefinitions.CARDBOARD_BOX, CardboardBoxBackpackModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(ModLayerDefinitions.SHEEP_PLUSH, SheepPlushBackpackModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(ModLayerDefinitions.WANDERING_BAG, WanderingBagBackpackModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(ModLayerDefinitions.PIGLIN_PACK, PiglinPackBackpackModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(ModLayerDefinitions.END_CRYSTAL, EndCrystalBackpackModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(ModLayerDefinitions.COGWHEEL, CogwheelBackpackModel::createLayer);

        // Add backpack layers for player and wandering trader
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if(entityRenderer instanceof WanderingTraderRenderer renderer) {
                registrationHelper.register(new VillagerBackpackLayer<>(renderer));
            } else if(entityRenderer instanceof PlayerRenderer renderer) {
                registrationHelper.register(new BackpackLayer<>(renderer));
            }
        });
    }

    public static void bakeBackpackModels(EntityModelSet entityModelSet)
    {
        ModelInstances.get().loadModels(entityModelSet);
    }
}
