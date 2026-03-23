package com.mrcrayfish.backpacked.client.gui.screen.widget.popup;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
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

    protected CustomContainerScreen(T menu, Inventory inventory, Component title, int imageWidth, int imageHeight)
    {
        super(menu, inventory, title, imageWidth, imageHeight);
    }

    @Override
    public PopupMenuController getPopupMenuController()
    {
        return this.controller;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick)
    {
        boolean hasPopup = this.hasPopupMenu();
        super.extractRenderState(extractor, hasPopup ? -1000 : mouseX, hasPopup ? -1000 : mouseY, partialTick);
        this.extractForeground(extractor, hasPopup ? -1000 : mouseX, hasPopup ? -1000 : mouseY, partialTick);
        this.controller.extract(extractor, mouseX, mouseY, partialTick);
        this.extractTooltip(extractor, mouseX, mouseY);
    }

    public abstract void extractForeground(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTicks);

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
