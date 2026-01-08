package com.mrcrayfish.backpacked.client.gui.screen.widget.popup;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public abstract class CustomScreen extends Screen implements PopupMenuHandler
{
    protected final PopupMenuController controller = new PopupMenuController();

    protected CustomScreen(Component title)
    {
        super(title);
    }

    @Override
    public PopupMenuController getPopupMenuController()
    {
        return this.controller;
    }

    @Override
    public final void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        boolean dropdown = this.hasPopupMenu();
        super.render(graphics, dropdown ? -1000 : mouseX, dropdown ? -1000 : mouseY, partialTick);
        this.renderForeground(graphics, dropdown ? -1000 : mouseX, dropdown ? -1000 : mouseY, partialTick);
        this.controller.render(graphics, mouseX, mouseY, partialTick);
    }

    protected abstract void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick)
    {
        if(this.controller.isOpened())
        {
            return this.controller.mouseClicked(event, doubleClick);
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event)
    {
        if(this.controller.isOpened())
        {
            return this.controller.mouseReleased(event);
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event)
    {
        if(this.controller.isOpened())
        {
            return this.controller.keyPressed(event);
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean keyReleased(KeyEvent event)
    {
        if(this.controller.isOpened())
        {
            return this.controller.keyReleased(event);
        }
        return super.keyReleased(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event)
    {
        if(this.controller.isOpened())
        {
            return this.controller.charTyped(event);
        }
        return super.charTyped(event);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double dx, double dy)
    {
        if(this.controller.isOpened())
        {
            return this.controller.mouseScrolled(x, y, dx, dy);
        }
        return super.mouseScrolled(x, y, dx, dy);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY)
    {
        if(this.controller.isOpened())
        {
            return this.controller.mouseDragged(event, deltaX, deltaY);
        }
        return super.mouseDragged(event, deltaX, deltaY);
    }
}
