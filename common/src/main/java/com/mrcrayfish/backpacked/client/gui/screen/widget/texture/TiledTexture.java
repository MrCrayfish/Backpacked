package com.mrcrayfish.backpacked.client.gui.screen.widget.texture;

import com.mrcrayfish.framework.api.client.screen.widget.texture.FrameworkTexture;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class TiledTexture extends FrameworkTexture
{
    private final ResourceLocation source;
    private final int textureU, textureV;
    private final int textureWidth, textureHeight;

    TiledTexture(ResourceLocation source, int textureU, int textureV, int textureWidth, int textureHeight)
    {
        this.source = source;
        this.textureU = textureU;
        this.textureV = textureV;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public int width()
    {
        return this.textureWidth;
    }

    @Override
    public int height()
    {
        return this.textureHeight;
    }

    @Override
    public void draw(GuiGraphics graphics, int x, int y, int width, int height)
    {
        graphics.blitRepeating(this.source, x, y, width, height, this.textureU, this.textureV, this.textureWidth, this.textureHeight);
    }

    public static TiledTexture create(ResourceLocation source, int textureU, int textureV, int textureWidth, int textureHeight)
    {
        return new TiledTexture(source, textureU, textureV, textureWidth, textureHeight);
    }
}
