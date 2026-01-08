package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.function;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.resources.Identifier;

/**
 * Author: MrCrayfish
 */
public class FunctionTypes
{
    private static final BiMap<Identifier, BaseFunction.Type> FUNCTIONS = HashBiMap.create();

    public static void register(BaseFunction.Type type)
    {
        BaseFunction.Type existing = FUNCTIONS.putIfAbsent(type.id(), type);
        if(existing != null)
            throw new IllegalStateException("Function already registered: " + type.id());
    }

    public static BiMap<Identifier, BaseFunction.Type> getAll()
    {
        return FUNCTIONS;
    }
}
