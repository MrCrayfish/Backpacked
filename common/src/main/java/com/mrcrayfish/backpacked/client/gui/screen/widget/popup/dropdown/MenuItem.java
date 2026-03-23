package com.mrcrayfish.backpacked.client.gui.screen.widget.popup.dropdown;

import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

public abstract class MenuItem extends AbstractWidget
{
    protected static final WidgetSprites SPRITES = new WidgetSprites(
        Utils.id("backpack/dropdown/menu_item"),
        Utils.id("backpack/dropdown/menu_item_hovered")
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
    protected void extractWidgetRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float deltaTick)
    {
        extractor.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITES.get(this.active, this.isHovered() || this.selected()), this.getX(), this.getY(), this.getWidth(), this.getHeight());

        Font font = Minecraft.getInstance().font;
        int offset = (this.getHeight() - font.lineHeight) / 2 + 1;
        extractor.text(Minecraft.getInstance().font, this.getMessage(), this.getX() + offset, this.getY() + offset, 0xFFFFFFFF);
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
