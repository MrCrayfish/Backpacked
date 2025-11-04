package com.mrcrayfish.backpacked.client.gui.screen.widget;

import com.mrcrayfish.backpacked.Constants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.function.Supplier;

public class TitleWidget extends AbstractWidget
{
    private static final ResourceLocation CHECKERS = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/checkers");

    private final Supplier<FormattedCharSequence> display;
    private final Font font;
    private int shift;

    public TitleWidget(FormattedCharSequence text, Component narration, Font font)
    {
        super(0, 0, font.width(text), font.lineHeight, narration);
        this.display = () -> text;
        this.font = font;
    }

    public TitleWidget(Component text, Font font)
    {
        super(0, 0, font.width(text.getVisualOrderText()), font.lineHeight, text);
        this.display = text::getVisualOrderText;
        this.font = font;
    }

    public TitleWidget(Supplier<Component> text, Font font)
    {
        super(0, 0, font.width(text.get().getVisualOrderText()), font.lineHeight, text.get());
        this.display = () -> text.get().getVisualOrderText();
        this.font = font;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        FormattedCharSequence displayText = this.display.get();
        int titleWidth = this.font.width(displayText);
        int titleX = this.getX() + (this.getWidth() - titleWidth) / 2 + this.shift;
        if(this.getWidth() > titleWidth)
        {
            graphics.blitSprite(CHECKERS, this.getX(), this.getY(), titleX - this.getX() - 3, this.font.lineHeight);
            graphics.blitSprite(CHECKERS, titleX + titleWidth + 3, this.getY(), this.getRight() - titleX - titleWidth - 3, this.font.lineHeight);
        }
        graphics.drawString(this.font, displayText, titleX, this.getY() + 1, 0xFF61503D, false);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}

    @Override
    protected boolean isValidClickButton(int button)
    {
        return false; // Prevents clicking
    }

    public TitleWidget setShift(int shift)
    {
        this.shift = shift;
        return this;
    }
}
