package com.mrcrayfish.backpacked.client.gui.screen.widget;

import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.CommonComponents;
import org.jetbrains.annotations.Nullable;

public class Divider extends AbstractWidget
{
    private int colour = 0xFFFFFFFF;

    private Divider(Orientation orientation, int size)
    {
        super(0, 0, orientation == Orientation.HORIZONTAL ? size : 1, orientation == Orientation.VERTICAL ? size : 1, CommonComponents.EMPTY);
    }

    public Divider colour(int colour)
    {
        this.colour = colour;
        return this;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        graphics.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), this.colour);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}

    @Override
    protected boolean isValidClickButton(int button)
    {
        return false; // Prevents clicking
    }

    @Nullable
    public ComponentPath nextFocusPath(FocusNavigationEvent event)
    {
        return null;
    }

    public enum Orientation
    {
        VERTICAL, HORIZONTAL;
    }

    public static Divider horizontal(int size)
    {
        return new Divider(Orientation.HORIZONTAL, size);
    }

    public static Divider vertical(int size)
    {
        return new Divider(Orientation.VERTICAL, size);
    }
}
