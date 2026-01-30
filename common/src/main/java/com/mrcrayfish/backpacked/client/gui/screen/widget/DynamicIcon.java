package com.mrcrayfish.backpacked.client.gui.screen.widget;

import com.mrcrayfish.framework.api.client.screen.widget.element.Icon;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Supplier;

public class DynamicIcon extends Icon
{
    private final Supplier<Icon> icon;

    private DynamicIcon(Supplier<Icon> icon)
    {
        this.icon = icon;
    }

    @Override
    public int width()
    {
        return this.icon.get().width();
    }

    @Override
    public int height()
    {
        return this.icon.get().height();
    }

    @Override
    public void draw(GuiGraphics graphics, int x, int y, float partialTick)
    {
        this.icon.get().draw(graphics, x, y, partialTick);
    }

    public static DynamicIcon create(Supplier<Icon> icon)
    {
        return new DynamicIcon(icon);
    }
}
