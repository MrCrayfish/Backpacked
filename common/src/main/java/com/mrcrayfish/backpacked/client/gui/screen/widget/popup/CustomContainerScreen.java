package com.mrcrayfish.backpacked.client.gui.screen.widget.popup;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class CustomContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> implements PopupMenuHandler
{
    protected final PopupMenuController controller = new PopupMenuController();

    protected CustomContainerScreen(T menu, Inventory playerInventory, Component title)
    {
        super(menu, playerInventory, title);
    }

    @Override
    public PopupMenuController getPopupMenuController()
    {
        return this.controller;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        boolean hasPopup = this.hasPopupMenu();
        super.render(graphics, hasPopup ? -1000 : mouseX, hasPopup ? -1000 : mouseY, partialTicks);
        this.renderForeground(graphics, hasPopup ? -1000 : mouseX, hasPopup ? -1000 : mouseY, partialTicks);
        this.controller.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    public abstract void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks);

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
