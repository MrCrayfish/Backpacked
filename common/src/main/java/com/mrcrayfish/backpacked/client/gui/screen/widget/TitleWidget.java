package com.mrcrayfish.backpacked.client.gui.screen.widget;

import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;

import java.util.function.Supplier;

public class TitleWidget extends AbstractWidget
{
    private static final Identifier CHECKERS = Utils.id("backpack/checkers");

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
    protected void extractWidgetRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick)
    {
        FormattedCharSequence displayText = this.display.get();
        int titleWidth = this.font.width(displayText);
        int titleX = this.getX() + (this.getWidth() - titleWidth) / 2 + this.shift;
        if(this.getWidth() > titleWidth)
        {
            extractor.blitSprite(RenderPipelines.GUI_TEXTURED, CHECKERS, this.getX(), this.getY(), titleX - this.getX() - 3, this.font.lineHeight);
            extractor.blitSprite(RenderPipelines.GUI_TEXTURED, CHECKERS, titleX + titleWidth + 3, this.getY(), this.getRight() - titleX - titleWidth - 3, this.font.lineHeight);
        }
        extractor.text(this.font, displayText, titleX, this.getY() + 1, 0xFF61503D, false);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}

    @Override
    protected boolean isValidClickButton(MouseButtonInfo info)
    {
        return false; // Prevents clicking
    }

    public TitleWidget setShift(int shift)
    {
        this.shift = shift;
        return this;
    }
}
