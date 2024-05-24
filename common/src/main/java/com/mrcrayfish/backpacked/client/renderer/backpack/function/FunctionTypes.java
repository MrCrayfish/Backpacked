package com.mrcrayfish.backpacked.client.renderer.backpack.function;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class FunctionTypes
{
    private static final BiMap<ResourceLocation, BaseFunction.Type> FUNCTIONS = HashBiMap.create();

    public static void register(BaseFunction.Type type)
    {
        BaseFunction.Type existing = FUNCTIONS.putIfAbsent(type.id(), type);
        if(existing != null)
            throw new IllegalStateException("Function already registered: " + type.id());
    }

    public static BiMap<ResourceLocation, BaseFunction.Type> getAll()
    {
        return FUNCTIONS;
    }
}
