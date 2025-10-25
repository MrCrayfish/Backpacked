package com.mrcrayfish.backpacked.client.gui.screen.widget;

import com.mrcrayfish.backpacked.Constants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TitleWidget extends AbstractWidget
{
    private static final ResourceLocation CHECKERS = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/checkers");

    private final Font font;

    public TitleWidget(Component text, Font font)
    {
        super(0, 0, font.width(text.getVisualOrderText()), font.lineHeight, text);
        this.font = font;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        int titleWidth = this.font.width(this.getMessage());
        int titleX = this.getX() + (this.getWidth() - titleWidth) / 2;
        if(this.getWidth() > titleWidth)
        {
            graphics.blitSprite(CHECKERS, this.getX(), this.getY(), titleX - this.getX() - 3, this.font.lineHeight);
            graphics.blitSprite(CHECKERS, titleX + titleWidth + 3, this.getY(), this.getRight() - titleX - titleWidth - 3, this.font.lineHeight);
        }
        graphics.drawString(this.font, this.getMessage(), titleX, this.getY() + 1, 0xFF61503D, false);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}
}
