package com.mrcrayfish.backpacked.client.gui.screen.widget.popup;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public final class PopupMenuController implements ContainerEventHandler
{
    @Nullable PopupMenu base;
    @Nullable GuiEventListener focused;
    boolean dragging;

    void open(PopupMenu menu)
    {
        if(menu.controller != this || this.contains(menu))
            return;

        this.setFocused(null);

        if(this.base == null)
        {
            this.base = menu;
            return;
        }

        PopupMenu top = this.base;
        while(top.child != null)
        {
            top = top.child;
        }
        menu.parent = top;
        top.child = menu;
    }

    void close(PopupMenu menu)
    {
        if(menu.controller != this || this.base == null)
            return;

        if(menu.child == null && menu.parent == null)
        {
            if(menu != this.base)
            {
                return;
            }
        }

        this.setFocused(null);

        if(menu.parent != null)
        {
            menu.parent.child = null;
            menu.parent = null;
        }

        PopupMenu current = menu;
        while(current.child != null)
        {
            PopupMenu child = current.child;
            current.child.parent = null;
            current.child = null;
            current = child;
        }

        if(this.base == menu)
        {
            this.base = null;
        }
    }

    void closeAll()
    {
        if(this.base != null)
        {
            this.close(this.base);
        }
    }

    public boolean isOpened()
    {
        return this.base != null;
    }

    private boolean contains(PopupMenu menu)
    {
        PopupMenu current = this.base;
        while(current != null)
        {
            if(current == menu)
                return true;
            current = menu.child;
        }
        return false;
    }

    @Nullable
    PopupMenu top()
    {
        PopupMenu top = this.base;
        while(top != null && top.child != null)
        {
            top = top.child;
        }
        return top;
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if(this.base != null)
        {
            graphics.pose().translate(0, 0, 350);
            this.base.render(graphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public List<? extends GuiEventListener> children()
    {
        PopupMenu top = this.top();
        return top != null ? top.getWidgets() : List.of();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        // Send the mouse clicked event to the top level popup menu
        if(ContainerEventHandler.super.mouseClicked(mouseX, mouseY, button))
            return true;

        // Otherwise cascade down from the top popup. If the click occurred within the popup menu,
        // bounds, reset focused and set event as handled. If the popup is not the top popup menu,
        // close child menus.
        PopupMenu current = this.top();
        while(current != null)
        {
            if(current.getRectangle().containsPoint((int) mouseX, (int) mouseY))
            {
                this.setFocused(null);
                if(current.child != null)
                {
                    current.child.close();
                }
                return true;
            }
            current = current.parent;
        }

        // If nothing was clicked at all, close all popups
        this.closeAll();

        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        // Allows the user to press escape to close the top popup
        if(keyCode == GLFW.GLFW_KEY_ESCAPE)
        {
            PopupMenu top = this.top();
            if(top != null)
            {
                this.close(top);
                return true;
            }
        }
        return ContainerEventHandler.super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    @Nullable
    public GuiEventListener getFocused()
    {
        return this.focused;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener listener)
    {
        if(this.focused != null)
        {
            this.focused.setFocused(false);
        }
        if(listener != null)
        {
            listener.setFocused(true);
        }
        this.focused = listener;
    }

    @Override
    public boolean isDragging()
    {
        return this.dragging;
    }

    @Override
    public void setDragging(boolean dragging)
    {
        this.dragging = dragging;
    }
}
