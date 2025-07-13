package com.mrcrayfish.backpacked.client.gui.screen.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;

public class MiniButton extends Button
{
    private final ResourceLocation texture;

    public MiniButton(int x, int y, ResourceLocation texture, OnPress onPress)
    {
        super(x, y, 10, 10, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
        this.texture = texture;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        graphics.blitSprite(this.texture, this.getX(), this.getY(), 10, 10);
        if(this.isHovered)
        {
            graphics.fillGradient(this.getX(), this.getY(), this.getX() + 10, this.getY() + 10, -2130706433, -2130706433);
        }
    }
}
