package com.mrcrayfish.backpacked;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class Backpacked implements ModInitializer
{
    private static boolean trinketsLoaded;

    public Backpacked()
    {
        // TODO is this too early?
        trinketsLoaded = FabricLoader.getInstance().isModLoaded("trinkets");
        Constants.LOG.info("Is trinkets loaded? " + trinketsLoaded);
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
