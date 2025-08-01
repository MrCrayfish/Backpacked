package com.mrcrayfish.backpacked.client;

import net.minecraft.resources.ResourceLocation;

public interface SpriteProvider
{
    ResourceLocation getSprite(boolean active, boolean hovered);
}
