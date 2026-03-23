package com.mrcrayfish.backpacked.client.gui.screen.widget.popup.dropdown.item;

import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.dropdown.DeferredWidgetDraw;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.dropdown.MenuItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class PopupItem extends MenuItem implements DeferredWidgetDraw
{
    private final PopupMenu menu;

    private PopupItem(Component label, PopupMenu menu)
    {
        super(label);
        this.menu = menu;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTick)
    {
        super.extractWidgetRenderState(graphics, mouseX, mouseY, deltaTick);
        Font font = Minecraft.getInstance().font;
        int top = this.getY() + (this.getHeight() - font.lineHeight) / 2 + 1;
        graphics.text(Minecraft.getInstance().font, ">", this.getX() + this.getWidth() - 10, top, 0xFFFFFFFF);
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick)
    {
        this.menu.show(this.getRectangle());
    }

    @Override
    protected boolean selected()
    {
        return this.menu.isOpen();
    }

    @Override
    public boolean shouldDefer()
    {
        return this.menu.isOpen();
    }

    @Override
    protected int calculateWidth()
    {
        Font font = Minecraft.getInstance().font;
        int labelOffset = (this.getHeight() - font.lineHeight) / 2 + 1;
        int labelWidth = font.width(this.getMessage());
        int arrowWidth = font.width(">");
        return labelOffset + labelWidth + labelOffset + arrowWidth + labelOffset;
    }

    public static MenuItem create(Component label, PopupMenu menu)
    {
        return new PopupItem(label, menu);
    }
}
