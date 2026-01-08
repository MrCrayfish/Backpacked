package com.mrcrayfish.backpacked.client;

import com.google.common.collect.ImmutableMap;
import com.mrcrayfish.framework.api.client.model.FrameworkBakedModel;
import com.mrcrayfish.framework.api.client.model.FrameworkModelResource;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class StandaloneModels
{
    private static Map<Identifier, FrameworkModelResource<FrameworkBakedModel>> models = Map.of();

    public static void init(Map<Identifier, FrameworkModelResource<FrameworkBakedModel>> models)
    {
        StandaloneModels.models = ImmutableMap.copyOf(models);
    }

    @Nullable
    public static FrameworkModelResource<FrameworkBakedModel> getResource(Identifier id)
    {
        return models.get(id);
    }
}
