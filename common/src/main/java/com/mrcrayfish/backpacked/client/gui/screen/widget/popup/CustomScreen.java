package com.mrcrayfish.backpacked.client.gui.screen.widget.popup;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
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
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(this.controller.isOpened())
        {
            return this.controller.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if(this.controller.isOpened())
        {
            return this.controller.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if(this.controller.isOpened())
        {
            return this.controller.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers)
    {
        if(this.controller.isOpened())
        {
            return this.controller.keyReleased(keyCode, scanCode, modifiers);
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char c, int modifiers)
    {
        if(this.controller.isOpened())
        {
            return this.controller.charTyped(c, modifiers);
        }
        return super.charTyped(c, modifiers);
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
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        if(this.controller.isOpened())
        {
            return this.controller.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
}
