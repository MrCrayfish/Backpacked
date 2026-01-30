package com.mrcrayfish.backpacked.client.gui.screen.widget.popup.dropdown.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.dropdown.MenuItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class PopupItem extends MenuItem
{
    private final PopupMenu menu;

    private PopupItem(Component label, PopupMenu menu)
    {
        super(label);
        this.menu = menu;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTick)
    {
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        if(this.selected())
        {
            poseStack.translate(0, 0, 51);
        }
        super.renderWidget(graphics, mouseX, mouseY, deltaTick);
        Font font = Minecraft.getInstance().font;
        int top = this.getY() + (this.getHeight() - font.lineHeight) / 2 + 1;
        graphics.drawString(Minecraft.getInstance().font, ">", this.getX() + this.getWidth() - 10, top, 0xFFFFFFFF);
        poseStack.popPose();
    }

    @Override
    public void onClick(double mouseX, double mouseY)
    {
        this.menu.show(this.getRectangle());
    }

    @Override
    protected boolean selected()
    {
        return this.menu.hasChild();
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
