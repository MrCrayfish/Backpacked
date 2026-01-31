package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.condition;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.resources.ResourceLocation;

public class ConditionTypes
{
    private static final BiMap<ResourceLocation, BaseCondition.Type> CONDITIONS = HashBiMap.create();

    public static void register(BaseCondition.Type type)
    {
        BaseCondition.Type existing = CONDITIONS.putIfAbsent(type.id(), type);
        if(existing != null)
            throw new IllegalStateException("Condition already registered: " + type.id());
    }

    public static BiMap<ResourceLocation, BaseCondition.Type> getAll()
    {
        return CONDITIONS;
    }
}
