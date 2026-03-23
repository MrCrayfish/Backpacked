package com.mrcrayfish.backpacked.client.gui.screen.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.Identifier;

public class MiniButton extends Button
{
    private final Identifier texture;

    public MiniButton(int x, int y, Identifier texture, OnPress onPress)
    {
        super(x, y, 10, 10, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
        this.texture = texture;
    }

    public MiniButton(int x, int y, int width, int height, Identifier texture, OnPress onPress)
    {
        super(x, y, width, height, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
        this.texture = texture;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick)
    {
        extractor.blitSprite(RenderPipelines.GUI_TEXTURED, this.texture, this.getX(), this.getY(), this.width, this.height);
        if(this.isHovered && this.active)
        {
            extractor.fillGradient(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, -2130706433, -2130706433);
        }
    }
}
