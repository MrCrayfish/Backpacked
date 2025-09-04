package com.mrcrayfish.backpacked.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.client.gui.screen.widget.dropdown.DropdownMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public abstract class ScreenWithDropdownMenu extends Screen implements DropdownMenuHandler
{
    protected @Nullable DropdownMenu menu;

    protected ScreenWithDropdownMenu(Component title)
    {
        super(title);
    }

    protected boolean hasDropdownMenu()
    {
        return this.menu != null;
    }

    @Override
    public void setDropdownMenu(@Nullable DropdownMenu menu)
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
        boolean dropdown = this.hasDropdownMenu();
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
                this.setDropdownMenu(null);
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
