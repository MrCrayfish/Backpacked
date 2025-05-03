package com.mrcrayfish.backpacked.util;

import com.mrcrayfish.backpacked.Constants;
import net.minecraft.resources.ResourceLocation;

public class Utils
{
    public static ResourceLocation rl(String path)
    {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path);
    }
}
