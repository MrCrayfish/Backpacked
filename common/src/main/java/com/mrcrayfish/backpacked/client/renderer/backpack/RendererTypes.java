package com.mrcrayfish.backpacked.client.renderer.backpack;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.Map;

public final class RendererTypes
{
    private static final BiMap<ResourceLocation, BackpackRenderer.Type> SOURCES = HashBiMap.create();

    public static void register(BackpackRenderer.Type type)
    {
        BackpackRenderer.Type existing = SOURCES.putIfAbsent(type.id(), type);
        if(existing != null)
            throw new IllegalStateException("Renderer already registered: " + type.id());
    }

    public static Map<ResourceLocation, BackpackRenderer.Type> getAll()
    {
        return Collections.unmodifiableMap(SOURCES);
    }
}
