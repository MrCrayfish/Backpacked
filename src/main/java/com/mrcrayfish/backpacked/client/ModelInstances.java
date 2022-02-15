package com.mrcrayfish.backpacked.client;

import com.mrcrayfish.backpacked.client.model.*;
import com.mrcrayfish.backpacked.core.ModLayerDefinitions;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Author: MrCrayfish
 */
public class ModelInstances
{
    private BackpackModel standardModel;
    private BackpackModel classic;
    private BackpackModel bambooBasketModel;
    private BackpackModel rocket;
    private BackpackModel miniChest;
    private BackpackModel trashCan;
    private BackpackModel honeyJar;
    private BackpackModel turtleShell;
    private BackpackModel cardboardBox;
    private BackpackModel sheepPlush;
    private BackpackModel wanderingBag;
    private BackpackModel piglinPack;
    private BackpackModel endCrystal;
    private BackpackModel cogwheel;

    @SubscribeEvent
    public void onLoadModels(EntityRenderersEvent.AddLayers event)
    {
        EntityModelSet models = event.getEntityModels();
        this.standardModel = new StandardBackpackModel(models.bakeLayer(ModLayerDefinitions.STANDARD));
        this.classic = new ClassicBackpackModel(models.bakeLayer(ModLayerDefinitions.CLASSIC));
        this.bambooBasketModel = new BambooBasketBackpackModel(models.bakeLayer(ModLayerDefinitions.BAMBOO_BASKET));
        this.rocket = new RocketBackpackModel(models.bakeLayer(ModLayerDefinitions.ROCKET));
        this.miniChest = new MiniChestBackpackModel(models.bakeLayer(ModLayerDefinitions.MINI_CHEST));
        this.trashCan = new TrashCanBackpackModel(models.bakeLayer(ModLayerDefinitions.TRASH_CAN));
        this.honeyJar = new HoneyJarBackpackModel(models.bakeLayer(ModLayerDefinitions.HONEY_JAR));
        this.turtleShell = new TurtleShellBackpackModel(models.bakeLayer(ModLayerDefinitions.TURTLE_SHELL));
        this.cardboardBox = new CardboardBoxBackpackModel(models.bakeLayer(ModLayerDefinitions.CARDBOARD_BOX));
        this.sheepPlush = new SheepPlushBackpackModel(models.bakeLayer(ModLayerDefinitions.SHEEP_PLUSH));
        this.wanderingBag = new WanderingBagBackpackModel(models.bakeLayer(ModLayerDefinitions.WANDERING_BAG));
        this.piglinPack = new PiglinPackBackpackModel(models.bakeLayer(ModLayerDefinitions.PIGLIN_PACK));
        this.endCrystal = new EndCrystalBackpackModel(models.bakeLayer(ModLayerDefinitions.END_CRYSTAL));
        this.cogwheel = new CogwheelBackpackModel(models.bakeLayer(ModLayerDefinitions.COGWHEEL));
    }

    public BackpackModel getStandardModel()
    {
        return this.standardModel;
    }

    public BackpackModel getClassic()
    {
        return this.classic;
    }

    public BackpackModel getBambooBasketModel()
    {
        return this.bambooBasketModel;
    }

    public BackpackModel getRocket()
    {
        return this.rocket;
    }

    public BackpackModel getMiniChest()
    {
        return this.miniChest;
    }

    public BackpackModel getTrashCan()
    {
        return this.trashCan;
    }

    public BackpackModel getHoneyJar()
    {
        return this.honeyJar;
    }

    public BackpackModel getTurtleShell()
    {
        return this.turtleShell;
    }

    public BackpackModel getCardboardBox()
    {
        return this.cardboardBox;
    }

    public BackpackModel getSheepPlush()
    {
        return this.sheepPlush;
    }

    public BackpackModel getWanderingBag()
    {
        return this.wanderingBag;
    }

    public BackpackModel getPiglinPack()
    {
        return this.piglinPack;
    }

    public BackpackModel getEndCrystal()
    {
        return this.endCrystal;
    }

    public BackpackModel getCogwheel()
    {
        return this.cogwheel;
    }
}
