package com.mrcrayfish.backpacked.util;

import com.mrcrayfish.backpacked.Constants;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;

public class Utils
{
    public static final RandomSource RANDOM = RandomSource.create();

    /**
     * Shortcut method to create an Identifier using "backpacked" as the namespace
     *
     * @param path the path for the Identifier
     * @return a new Identifier instance
     */
    public static Identifier id(String path)
    {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, path);
    }
}
