package com.mrcrayfish.backpacked.client.gui.screen.widget.popup.dropdown.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.dropdown.MenuItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class PopupItem extends MenuItem
{
    private final PopupMenu child;

    private PopupItem(Component label, PopupMenu child)
    {
        super(label);
        this.child = child;
    }

    @Override
    protected void visitChildMenus(Consumer<PopupMenu> consumer)
    {
        consumer.accept(this.child);
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
        this.child.show(this.getRectangle());
    }

    @Override
    protected boolean selected()
    {
        return this.getParent().isActiveChildMenu(this.child);
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
