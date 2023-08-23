package com.mrcrayfish.backpacked;

import com.mrcrayfish.framework.FrameworkSetup;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class Backpacked implements ModInitializer
{
    private static boolean trinketsLoaded;

    public Backpacked()
    {
        FrameworkSetup.run();
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
