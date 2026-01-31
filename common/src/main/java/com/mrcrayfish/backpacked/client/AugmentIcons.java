package com.mrcrayfish.backpacked.client;

import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.framework.api.client.screen.widget.texture.FrameworkTexture;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class AugmentIcons
{
    private static final Map<AugmentType<?>, FrameworkTexture> ICONS = new ConcurrentHashMap<>();

    public static FrameworkTexture get(AugmentType<?> type)
    {
        return ICONS.getOrDefault(type, TextureDefinitions.MISSING_TEXTURE);
    }

    public static void set(AugmentType<?> type, FrameworkTexture texture)
    {
        ICONS.put(type, texture);
    }
}
