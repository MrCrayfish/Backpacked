package com.mrcrayfish.backpacked.client.gui;

import com.mrcrayfish.backpacked.common.PaymentItem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class ItemCostTooltip implements ClientTooltipComponent
{
    private final Component label;
    private final ItemStack item;

    public ItemCostTooltip(PaymentItem payment, int cost)
    {
        this.item = new ItemStack(payment.getItem(), cost);
        this.label = this.item.getHoverName();
    }

    @Override
    public int getHeight(Font font)
    {
        return 16;
    }

    @Override
    public int getWidth(Font font)
    {
        // Item Width + Spacing + Label Width
        return 15 + 3 + font.width(this.label);
    }

    @Override
    public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor extractor)
    {
        extractor.item(this.item, x - 1, y - 1);
        extractor.itemDecorations(font, this.item, x - 1, y - 1, this.item.getCount() == 1 ? "1" : null);
        extractor.text(font, this.label, x + 15 + 2, y + 3, 0xFF2B2203, false);
        extractor.text(font, this.label, x + 15 + 4, y + 3, 0xFF2B2203, false);
        extractor.text(font, this.label, x + 15 + 3, y + 2, 0xFF2B2203, false);
        extractor.text(font, this.label, x + 15 + 3, y + 4, 0xFF2B2203, false);
        extractor.text(font, this.label, x + 15 + 3, y + 3, 0xFFAAAAAA, false);
    }
}
