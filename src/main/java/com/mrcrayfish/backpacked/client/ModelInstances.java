package com.mrcrayfish.backpacked.client;

import com.mrcrayfish.backpacked.client.model.BackpackModel;
import com.mrcrayfish.backpacked.client.model.BambooBasketBackpackModel;
import com.mrcrayfish.backpacked.client.model.ClassicBackpackModel;
import com.mrcrayfish.backpacked.client.model.HoneyJarBackpackModel;
import com.mrcrayfish.backpacked.client.model.MiniChestBackpackModel;
import com.mrcrayfish.backpacked.client.model.RocketBackpackModel;
import com.mrcrayfish.backpacked.client.model.StandardBackpackModel;
import com.mrcrayfish.backpacked.client.model.TrashCanBackpackModel;
import com.mrcrayfish.backpacked.client.model.TurtleShellBackpackModel;

/**
 * Author: MrCrayfish
 */
public class ModelInstances
{
    public static final BackpackModel STANDARD = new StandardBackpackModel();
    public static final BackpackModel CLASSIC = new ClassicBackpackModel();
    public static final BackpackModel BAMBOO_BASKET = new BambooBasketBackpackModel();
    public static final BackpackModel ROCKET = new RocketBackpackModel();
    public static final BackpackModel MINI_CHEST = new MiniChestBackpackModel();
    public static final BackpackModel TRASH_CAN = new TrashCanBackpackModel();
    public static final BackpackModel HONEY_JAR = new HoneyJarBackpackModel();
    public static final BackpackModel TURTLE_SHELL = new TurtleShellBackpackModel();
}
