package com.mrcrayfish.backpacked.client;

import net.minecraft.resources.Identifier;

public interface SpriteProvider
{
    Identifier getSprite(boolean active, boolean hovered);
}
