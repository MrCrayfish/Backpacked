package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.value;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public final class ValueTypes
{
    private static final BiMap<ResourceLocation, Value.Type> SOURCES = HashBiMap.create();

    public static void register(Value.Type type)
    {
        Value.Type existing = SOURCES.putIfAbsent(type.id(), type);
        if(existing != null)
            throw new IllegalStateException("Source already registered: " + type.id());
    }

    public static Map<ResourceLocation, Value.Type> getAll()
    {
        return Collections.unmodifiableMap(SOURCES);
    }
}
