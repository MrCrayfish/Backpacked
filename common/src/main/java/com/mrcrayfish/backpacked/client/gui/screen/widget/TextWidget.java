package com.mrcrayfish.backpacked.client.gui.screen.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class TextWidget extends AbstractWidget
{
    private final Supplier<Component> text;
    private final Font font;
    private int colour = 0xFFFFFFFF;
    private boolean shadow = false;

    public TextWidget(Component text, Font font)
    {
        super(0, 0, font.width(text.getVisualOrderText()), font.lineHeight, text);
        this.text = () -> text;
        this.font = font;
    }

    public TextWidget(Supplier<Component> text, Font font)
    {
        super(0, 0, font.width(text.get().getVisualOrderText()), font.lineHeight, CommonComponents.EMPTY);
        this.text = text;
        this.font = font;
    }

    public TextWidget setColour(int colour)
    {
        this.colour = colour;
        return this;
    }

    public TextWidget setShadow(boolean shadow)
    {
        this.shadow = shadow;
        return this;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick)
    {
        extractor.text(this.font, this.text.get(), this.getX(), this.getY() + 1, this.colour, this.shadow);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}

    @Override
    protected boolean isValidClickButton(MouseButtonInfo info)
    {
        return false; // Prevents clicking
    }
}
