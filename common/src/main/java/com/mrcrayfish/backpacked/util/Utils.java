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
     * @param path the path for the ResourceLocation
     * @return a new ResourceLocation instance
     */
    public static ResourceLocation rl(String path)
    {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path);
    }
}
