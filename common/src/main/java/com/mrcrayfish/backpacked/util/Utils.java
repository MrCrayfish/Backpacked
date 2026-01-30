package com.mrcrayfish.backpacked.util;

import com.mrcrayfish.backpacked.Constants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public class Utils
{
    public static final RandomSource RANDOM = RandomSource.create();

    /**
     * Shortcut method to create a ResourceLocation using "backpacked" as the namespace
     *
     * @param name the path for the ResourceLocation
     * @return a new ResourceLocation instance
     */
    public static ResourceLocation rl(String name)
    {
        return new ResourceLocation(Constants.MOD_ID, name);
    }
}
