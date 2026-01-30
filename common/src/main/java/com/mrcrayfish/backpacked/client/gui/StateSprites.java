package com.mrcrayfish.backpacked.client.gui;

import com.mrcrayfish.framework.api.client.screen.widget.texture.FrameworkTexture;

public record StateSprites(FrameworkTexture defaultSprite, FrameworkTexture hoveredSprite, FrameworkTexture selectedSprite, FrameworkTexture selectedHoveredSprite)
{
    public StateSprites(FrameworkTexture defaultSprite, FrameworkTexture hoveredSprite, FrameworkTexture selectedSprite)
    {
        this(defaultSprite, hoveredSprite, selectedSprite, selectedSprite);
    }

    public StateSprites(FrameworkTexture defaultSprite, FrameworkTexture hoveredSprite)
    {
        this(defaultSprite, hoveredSprite, defaultSprite, hoveredSprite);
    }

    public FrameworkTexture get(boolean selected, boolean hovered)
    {
        if(selected) return hovered ? this.selectedHoveredSprite : this.selectedSprite;
        return hovered ? this.hoveredSprite : this.defaultSprite;
    }
}
