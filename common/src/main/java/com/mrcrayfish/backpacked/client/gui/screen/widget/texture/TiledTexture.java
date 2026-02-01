package com.mrcrayfish.backpacked.client.gui.screen.widget.texture;

import com.mrcrayfish.framework.api.client.screen.widget.texture.FrameworkTexture;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

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
        int cols = Mth.ceil((double) width / this.textureWidth);
        int rows = Mth.ceil((double) height / this.textureHeight);
        int remainingHeight = height;
        for(int i = 0; i < rows; i++)
        {
            int remainingWidth = width;
            for(int j = 0; j < cols; j++)
            {
                int blitWidth = Mth.clamp(remainingWidth, 0, this.textureWidth);
                int blitHeight = Mth.clamp(remainingHeight, 0, this.textureHeight);
                graphics.blit(this.source, x + j * this.textureWidth, y + i * this.textureHeight, this.textureU, this.textureV, blitWidth, blitHeight);
                remainingWidth -= this.textureWidth;
            }
            remainingHeight -= this.textureHeight;
        }
    }

    public static TiledTexture create(ResourceLocation source, int textureU, int textureV, int textureWidth, int textureHeight)
    {
        return new TiledTexture(source, textureU, textureV, textureWidth, textureHeight);
    }
}
