package com.mrcrayfish.backpacked.client.gui;

import net.minecraft.resources.ResourceLocation;

public record StateSprites(ResourceLocation defaultSprite, ResourceLocation hoveredSprite, ResourceLocation selectedSprite, ResourceLocation selectedHoveredSprite)
{
    public StateSprites(ResourceLocation defaultSprite, ResourceLocation hoveredSprite, ResourceLocation selectedSprite)
    {
        this(defaultSprite, hoveredSprite, selectedSprite, selectedSprite);
    }

    public StateSprites(ResourceLocation defaultSprite, ResourceLocation hoveredSprite)
    {
        this(defaultSprite, hoveredSprite, defaultSprite, hoveredSprite);
    }

    public ResourceLocation get(boolean selected, boolean hovered)
    {
        if(selected) return hovered ? this.selectedHoveredSprite : this.selectedSprite;
        return hovered ? this.hoveredSprite : this.defaultSprite;
    }
}
