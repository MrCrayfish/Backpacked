package com.mrcrayfish.backpacked.client.gui.screen.widget.popup;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.jetbrains.annotations.Nullable;

public final class PopupMenuController implements GuiEventListener
{
    @Nullable PopupMenu base;
    @Nullable GuiEventListener focused;

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

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if(this.base != null)
        {
            PoseStack poseStack = graphics.pose();
            poseStack.pushPose();
            poseStack.translate(0, 0, 300);
            this.base.render(graphics, mouseX, mouseY, partialTick);
            poseStack.popPose();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(this.base != null)
        {
            if(!this.base.mouseClicked(mouseX, mouseY, button))
            {
                this.closeAll();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if(this.base != null)
        {
            return this.base.mouseReleased(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        if(this.base != null)
        {
            return this.base.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double dx, double dy)
    {
        if(this.base != null)
        {
            return this.base.mouseScrolled(x, y, dx, dy);
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if(this.base != null)
        {
            return this.base.keyPressed(keyCode, scanCode, modifiers);
        }
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers)
    {
        if(this.base != null)
        {
            return this.base.keyReleased(keyCode, scanCode, modifiers);
        }
        return false;
    }

    @Override
    public boolean charTyped(char c, int modifiers)
    {
        if(this.base != null)
        {
            return this.base.charTyped(c, modifiers);
        }
        return false;
    }

    @Override
    public void setFocused(boolean focused) {}

    @Override
    public boolean isFocused()
    {
        return false;
    }

    @Nullable
    public GuiEventListener getFocused()
    {
        return this.focused;
    }

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
}
