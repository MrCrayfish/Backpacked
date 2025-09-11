package com.mrcrayfish.backpacked.client.gui.screen.widget.popup;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.util.ScreenUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class ScreenWithPopupMenu extends Screen implements PopupMenuHandler
{
    protected @Nullable PopupMenu menu;

    protected ScreenWithPopupMenu(Component title)
    {
        super(title);
    }

    protected boolean hasPopupMenu()
    {
        return this.menu != null;
    }

    @Override
    public void setPopupMenu(@Nullable PopupMenu menu)
    {
        if(this.menu != null && this.menu != menu)
        {
            this.menu.hide();
        }
        this.menu = menu;
    }

    @Override
    public final void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        boolean dropdown = this.hasPopupMenu();
        super.render(graphics, dropdown ? -1000 : mouseX, dropdown ? -1000 : mouseY, partialTick);
        this.renderForeground(graphics, dropdown ? -1000 : mouseX, dropdown ? -1000 : mouseY, partialTick);
        if(this.menu != null)
        {
            PoseStack poseStack = graphics.pose();
            poseStack.pushPose();
            poseStack.translate(0, 0, 300);
            this.menu.render(graphics, mouseX, mouseY, partialTick);
            poseStack.popPose();
        }
    }

    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public final boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(this.menu != null)
        {
            if(!this.menu.mouseClicked(mouseX, mouseY, button))
            {
                this.setPopupMenu(null);
            }
            return true;
        }
        if(this.onMouseClicked(mouseX, mouseY, button))
        {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    protected boolean onMouseClicked(double mouseX, double mouseY, int button)
    {
        return false;
    }
}
