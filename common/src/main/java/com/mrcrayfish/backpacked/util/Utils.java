package com.mrcrayfish.backpacked.util;

import com.mrcrayfish.backpacked.Constants;
import net.minecraft.resources.ResourceLocation;

public class Utils
{
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
