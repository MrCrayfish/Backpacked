package com.mrcrayfish.backpacked.client;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import com.mrcrayfish.backpacked.client.model.ModelInstances;
import com.mrcrayfish.backpacked.client.model.backpack.*;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.BackpackLayer;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.ShelfRenderer;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.VillagerBackpackLayer;
import com.mrcrayfish.backpacked.core.ModBlockEntities;
import com.mrcrayfish.backpacked.core.ModContainers;
import com.mrcrayfish.backpacked.core.ModLayerDefinitions;
import com.mrcrayfish.backpacked.integration.Controllable;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.WanderingTraderRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.loading.FMLLoader;

/**
 * Author: MrCrayfish
 */
public class ClientHandler
{
    public static void init()
    {
        MenuScreens.register(ModContainers.BACKPACK.get(), BackpackScreen::new);

        if(Backpacked.isControllableLoaded())
        {
            Controllable.init();
        }

        if(!FMLLoader.isProduction())
        {
            MinecraftForge.EVENT_BUS.register(new ForgeClientEvents());
        }
    }

    //TODO convert these to fabric
    //Check
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event)
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

    //Check
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(ModBlockEntities.SHELF.get(), ShelfRenderer::new);
    }

    public static void onAddLayers(EntityRenderersEvent.AddLayers event)
    {
        addBackpackLayer(event.getSkin("default"), event.getContext().getItemRenderer());
        addBackpackLayer(event.getSkin("slim"), event.getContext().getItemRenderer());

        EntityRenderer<?> renderer = event.getRenderer(EntityType.WANDERING_TRADER);
        if(renderer instanceof WanderingTraderRenderer traderRenderer)
        {
            traderRenderer.addLayer(new VillagerBackpackLayer<>(traderRenderer));
        }

        ModelInstances.get().loadModels(event.getEntityModels());
    }

    private static void addBackpackLayer(EntityRenderer<?> renderer, ItemRenderer itemRenderer)
    {
        if(renderer instanceof PlayerRenderer playerRenderer)
        {
            playerRenderer.addLayer(new BackpackLayer<>(playerRenderer, itemRenderer));
        }
    }
}
