package com.mrcrayfish.backpacked.client;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import com.mrcrayfish.backpacked.client.model.ModelInstances;
import com.mrcrayfish.backpacked.client.model.backpack.*;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.BackpackLayer;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.ShelfRenderer;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.VillagerBackpackLayer;
import com.mrcrayfish.backpacked.core.ModBlockEntities;
import com.mrcrayfish.backpacked.core.ModContainers;
import com.mrcrayfish.backpacked.core.ModEnchantments;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.core.ModLayerDefinitions;
import com.mrcrayfish.framework.Registration;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.mixin.client.rendering.EntityRenderersMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.WanderingTraderRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.ItemLike;

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

        // Create creative mode tab
        FabricItemGroup.builder(new ResourceLocation(Constants.MOD_ID, "creative_tab"))
            .title(Component.translatable("itemGroup." + Constants.MOD_ID))
            .icon(() -> new ItemStack(ModItems.BACKPACK.get()))
            .displayItems((params, output) -> {
                Registration.get(Registries.ITEM).stream().filter(entry -> entry.getId().getNamespace().equals(Constants.MOD_ID)).forEach(entry -> {
                    output.accept((ItemLike) entry.get());
                });
                Registration.get(Registries.ENCHANTMENT).stream().filter(entry -> entry.getId().getNamespace().equals(Constants.MOD_ID)).forEach(entry -> {
                    Enchantment enchantment = (Enchantment) entry.get();
                    output.accept(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, enchantment.getMaxLevel())), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
                });
            })
            .build();
    }

    public static void bakeBackpackModels(EntityModelSet entityModelSet)
    {
        ModelInstances.get().loadModels(entityModelSet);
    }
}
