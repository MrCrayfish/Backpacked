package com.mrcrayfish.backpacked.client.gui.screen.widget.popup.dropdown;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.backpacked.client.TextureDefinitions;
import com.mrcrayfish.framework.api.client.screen.widget.texture.WidgetTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public abstract class MenuItem extends AbstractWidget // TODO DONE
{
    protected static final WidgetTextures SPRITES = new WidgetTextures(
        TextureDefinitions.DROPDOWN_ITEM,
        TextureDefinitions.DROPDOWN_ITEM_HOVERED
    );

    DropdownMenu owner;

    public MenuItem(Component label)
    {
        super(0, 0, 100, 20, label);
    }

    protected DropdownMenu getPopupMenu()
    {
        return this.owner;
    }

    protected boolean selected()
    {
        return false;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTick)
    {
        RenderSystem.enableBlend();
        SPRITES.get(this.active, this.isHovered() || this.selected()).draw(graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        RenderSystem.disableBlend();

        Font font = Minecraft.getInstance().font;
        int offset = (this.getHeight() - font.lineHeight) / 2 + 1;
        graphics.drawString(Minecraft.getInstance().font, this.getMessage(), this.getX() + offset, this.getY() + offset, 0xFFFFFFFF);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output)
    {
        output.add(NarratedElementType.TITLE, this.getMessage());
    }

    protected int calculateWidth()
    {
        Font font = Minecraft.getInstance().font;
        int labelOffset = (this.getHeight() - font.lineHeight) / 2 + 1;
        int labelWidth = font.width(this.getMessage());
        return labelOffset + labelWidth + labelOffset;
    }
}
