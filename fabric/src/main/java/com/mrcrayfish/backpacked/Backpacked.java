package com.mrcrayfish.backpacked;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.loader.api.FabricLoader;

public class Backpacked implements ModInitializer
{
    private static boolean trinketsLoaded;

    public Backpacked()
    {
        trinketsLoaded = FabricLoader.getInstance().isModLoaded("trinkets");
    }

    @Override
    public void onInitialize()
    {
        Bootstrap.init();
    }

    public static boolean isTrinketsLoaded()
    {
        return trinketsLoaded;
    }
}
