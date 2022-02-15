package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.model.*;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModLayerDefinitions
{
    public static final ModelLayerLocation STANDARD = register("standard");
    public static final ModelLayerLocation CLASSIC = register("classic");
    public static final ModelLayerLocation BAMBOO_BASKET = register("bamboo_basket");
    public static final ModelLayerLocation ROCKET = register("rocket");
    public static final ModelLayerLocation MINI_CHEST = register("mini_chest");
    public static final ModelLayerLocation TRASH_CAN = register("trash_can");
    public static final ModelLayerLocation HONEY_JAR = register("honey_jar");
    public static final ModelLayerLocation TURTLE_SHELL = register("turtle_shell");
    public static final ModelLayerLocation CARDBOARD_BOX = register("cardboard_box");
    public static final ModelLayerLocation SHEEP_PLUSH = register("sheep_plush");
    public static final ModelLayerLocation WANDERING_BAG = register("wandering_bag");
    public static final ModelLayerLocation PIGLIN_PACK = register("piglin_pack");
    public static final ModelLayerLocation END_CRYSTAL = register("end_crystal");
    public static final ModelLayerLocation COGWHEEL = register("cog_wheel");

    private static ModelLayerLocation register(String name)
    {
        return new ModelLayerLocation(new ResourceLocation(Reference.MOD_ID, name), "main");
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(STANDARD, StandardBackpackModel::createLayer);
        event.registerLayerDefinition(CLASSIC, ClassicBackpackModel::createLayer);
        event.registerLayerDefinition(BAMBOO_BASKET, BambooBasketBackpackModel::createLayer);
        event.registerLayerDefinition(ROCKET, RocketBackpackModel::createLayer);
        event.registerLayerDefinition(MINI_CHEST, MiniChestBackpackModel::createLayer);
        event.registerLayerDefinition(TRASH_CAN, TrashCanBackpackModel::createLayer);
        event.registerLayerDefinition(HONEY_JAR, HoneyJarBackpackModel::createLayer);
        event.registerLayerDefinition(TURTLE_SHELL, TurtleShellBackpackModel::createLayer);
        event.registerLayerDefinition(CARDBOARD_BOX, CardboardBoxBackpackModel::createLayer);
        event.registerLayerDefinition(SHEEP_PLUSH, SheepPlushBackpackModel::createLayer);
        event.registerLayerDefinition(WANDERING_BAG, WanderingBagBackpackModel::createLayer);
        event.registerLayerDefinition(PIGLIN_PACK, PiglinPackBackpackModel::createLayer);
        event.registerLayerDefinition(END_CRYSTAL, EndCrystalBackpackModel::createLayer);
        event.registerLayerDefinition(COGWHEEL, CogwheelBackpackModel::createLayer);
    }
}
