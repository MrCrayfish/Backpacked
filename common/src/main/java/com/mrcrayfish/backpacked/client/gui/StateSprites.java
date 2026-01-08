package com.mrcrayfish.backpacked.client.gui;

import net.minecraft.resources.Identifier;

public record StateSprites(Identifier defaultSprite, Identifier hoveredSprite, Identifier selectedSprite, Identifier selectedHoveredSprite)
{
    public StateSprites(Identifier defaultSprite, Identifier hoveredSprite, Identifier selectedSprite)
    {
        this(defaultSprite, hoveredSprite, selectedSprite, selectedSprite);
    }

    public StateSprites(Identifier defaultSprite, Identifier hoveredSprite)
    {
        this(defaultSprite, hoveredSprite, defaultSprite, hoveredSprite);
    }

    public Identifier get(boolean selected, boolean hovered)
    {
        if(selected) return hovered ? this.selectedHoveredSprite : this.selectedSprite;
        return hovered ? this.hoveredSprite : this.defaultSprite;
    }
}
