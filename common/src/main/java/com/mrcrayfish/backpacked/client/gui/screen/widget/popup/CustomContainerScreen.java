package com.mrcrayfish.backpacked.client.gui.screen.widget.popup;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public abstract class CustomContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> implements PopupMenuHandler
{
    private @Nullable PopupMenu popup;

    public CustomContainerScreen(T menu, Inventory playerInventory, Component title)
    {
        super(menu, playerInventory, title);
    }

    public boolean hasPopupMenu()
    {
        return this.popup != null;
    }

    @Override
    public void setPopupMenu(@Nullable PopupMenu menu)
    {
        if(this.popup != null && this.popup != menu)
        {
            this.popup.hide();
        }
        this.popup = menu;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        boolean hasPopup = this.popup != null;
        super.render(graphics, hasPopup ? -1000 : mouseX, hasPopup ? -1000 : mouseY, partialTicks);
        this.renderForeground(graphics, hasPopup ? -1000 : mouseX, hasPopup ? -1000 : mouseY, partialTicks);
        if(this.popup != null)
        {
            PoseStack poseStack = graphics.pose();
            poseStack.pushPose();
            poseStack.translate(0, 0, 300);
            this.popup.render(graphics, mouseX, mouseY, partialTicks);
            poseStack.popPose();
        }
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    public abstract void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks);

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(this.popup != null)
        {
            if(!this.popup.mouseClicked(mouseX, mouseY, button))
            {
                this.setPopupMenu(null);
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if(this.popup != null)
        {
            return this.popup.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        // Exclusive input given to popups
        if(this.popup != null)
        {
            return this.popup.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers)
    {
        if(this.popup != null)
        {
            return this.popup.keyReleased(keyCode, scanCode, modifiers);
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char c, int modifiers)
    {
        if(this.popup != null)
        {
            return this.popup.charTyped(c, modifiers);
        }
        return super.charTyped(c, modifiers);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double dx, double dy)
    {
        if(this.popup != null)
        {
            return this.popup.mouseScrolled(x, y, dx, dy);
        }
        return super.mouseScrolled(x, y, dx, dy);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        if(this.popup != null)
        {
            return this.popup.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
}
