package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Constants;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;


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
        return new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, name), "main");
    }
}
