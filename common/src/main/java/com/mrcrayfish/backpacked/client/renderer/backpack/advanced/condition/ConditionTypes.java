package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.condition;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.resources.Identifier;

public class ConditionTypes
{
    private static final BiMap<Identifier, BaseCondition.Type> CONDITIONS = HashBiMap.create();

    public static void register(BaseCondition.Type type)
    {
        BaseCondition.Type existing = CONDITIONS.putIfAbsent(type.id(), type);
        if(existing != null)
            throw new IllegalStateException("Condition already registered: " + type.id());
    }

    public static BiMap<Identifier, BaseCondition.Type> getAll()
    {
        return CONDITIONS;
    }
}
