package com.mrcrayfish.backpacked.client.gui;

import net.minecraft.resources.ResourceLocation;

public record ItemSprites(ResourceLocation background, ResourceLocation hovered, ResourceLocation selected, ResourceLocation selectedHovered)
{
    public ItemSprites(ResourceLocation background, ResourceLocation hovered, ResourceLocation selected)
    {
        this(background, hovered, selected, selected);
    }

    public ResourceLocation get(boolean selected, boolean hovered)
    {
        if(selected) return hovered ? this.selectedHovered : this.selected;
        return hovered ? this.hovered : this.background;
    }
}
