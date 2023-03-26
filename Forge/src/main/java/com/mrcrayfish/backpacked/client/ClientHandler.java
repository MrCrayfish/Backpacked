package com.mrcrayfish.backpacked.client;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import com.mrcrayfish.backpacked.client.model.*;
import com.mrcrayfish.backpacked.client.model.backpack.*;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.BackpackLayer;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.ShelfRenderer;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.VillagerBackpackLayer;
import com.mrcrayfish.backpacked.core.ModBlockEntities;
import com.mrcrayfish.backpacked.core.ModContainers;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.core.ModLayerDefinitions;
import com.mrcrayfish.backpacked.integration.Controllable;
import com.mrcrayfish.framework.Registration;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.WanderingTraderRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.registries.ForgeRegistries;

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

    public static void onRegisterCreativeTab(CreativeModeTabEvent.Register event)
    {
        event.registerCreativeModeTab(new ResourceLocation(Constants.MOD_ID, "creative_tab"), builder -> {
            builder.title(Component.translatable("itemGroup." + Constants.MOD_ID));
            builder.icon(() -> new ItemStack(ModItems.BACKPACK.get()));
            builder.displayItems((flags, output, permission) -> {
                Registration.get(Registries.ITEM).stream().filter(entry -> entry.getId().getNamespace().equals(Constants.MOD_ID)).forEach(entry -> {
                    output.accept((ItemLike) entry.get());
                });
                for(Enchantment enchantment : ForgeRegistries.ENCHANTMENTS) {
                    if(enchantment.category == Backpacked.ENCHANTMENT_TYPE) {
                        output.accept(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, enchantment.getMaxLevel())), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
                    }
                }
            });
        });
    }

    public static void onAddLayers(EntityRenderersEvent.AddLayers event)
    {
        addBackpackLayer(event.getSkin("default"));
        addBackpackLayer(event.getSkin("slim"));

        EntityRenderer<?> renderer = event.getRenderer(EntityType.WANDERING_TRADER);
        if(renderer instanceof WanderingTraderRenderer traderRenderer)
        {
            traderRenderer.addLayer(new VillagerBackpackLayer<>(traderRenderer));
        }

        ModelInstances.get().loadModels(event.getEntityModels());
    }

    private static void addBackpackLayer(LivingEntityRenderer<?, ?> renderer)
    {
        if(renderer instanceof PlayerRenderer playerRenderer)
        {
            playerRenderer.addLayer(new BackpackLayer<>(playerRenderer));
        }
    }
}
