package com.mrcrayfish.backpacked.client.gui.screen.widget.popup.dropdown;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.backpacked.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class MenuItem extends AbstractWidget
{
    protected static final WidgetSprites SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/dropdown/menu_item"),
            ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/dropdown/menu_item_hovered")
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

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTick)
    {
        RenderSystem.enableBlend();
        graphics.blitSprite(SPRITES.get(this.active, this.isHovered() || this.selected()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
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
