package com.mrcrayfish.backpacked.client.gui.screen.widget;

import com.mrcrayfish.backpacked.Constants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class TitleWidget extends AbstractWidget
{
    private static final ResourceLocation CHECKERS = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/checkers");

    private final Supplier<Component> text;
    private final Font font;

    public TitleWidget(Component text, Font font)
    {
        super(0, 0, font.width(text.getVisualOrderText()), font.lineHeight, text);
        this.text = () -> text;
        this.font = font;
    }

    public TitleWidget(Supplier<Component> text, Font font)
    {
        super(0, 0, font.width(text.get().getVisualOrderText()), font.lineHeight, text.get());
        this.text = text;
        this.font = font;
    }

    @Override
    public Component getMessage()
    {
        return this.text.get();
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        Component message = this.getMessage();
        int titleWidth = this.font.width(message);
        int titleX = this.getX() + (this.getWidth() - titleWidth) / 2;
        if(this.getWidth() > titleWidth)
        {
            graphics.blitSprite(CHECKERS, this.getX(), this.getY(), titleX - this.getX() - 3, this.font.lineHeight);
            graphics.blitSprite(CHECKERS, titleX + titleWidth + 3, this.getY(), this.getRight() - titleX - titleWidth - 3, this.font.lineHeight);
        }
        graphics.drawString(this.font, message, titleX, this.getY() + 1, 0xFF61503D, false);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}

    @Override
    protected boolean isValidClickButton(int button)
    {
        return false; // Prevents clicking
    }
}
