package com.mrcrayfish.backpacked.client.gui;

import com.mrcrayfish.backpacked.Constants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ExperienceCostTooltip implements ClientTooltipComponent
{
    private static final ResourceLocation ICON_ORB = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/exp_orb");

    private final Component label;

    public ExperienceCostTooltip(int cost)
    {
        String key = (cost == 1 ? "backpacked.gui.experience_cost" : "backpacked.gui.experience_cost.plural");
        this.label = Component.translatable(key, cost);
    }

    @Override
    public int getHeight()
    {
        return 11;
    }

    @Override
    public int getWidth(Font font)
    {
        // Icon Width + Spacing + Label Width
        return 9 + 3 + font.width(this.label);
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics)
    {
        graphics.blitSprite(ICON_ORB, x, y, 9, 9);
        graphics.drawString(font, this.label, x + 9 + 2, y + 1, 0xFF2B2203, false);
        graphics.drawString(font, this.label, x + 9 + 4, y + 1, 0xFF2B2203, false);
        graphics.drawString(font, this.label, x + 9 + 3, y, 0xFF2B2203, false);
        graphics.drawString(font, this.label, x + 9 + 3, y + 2, 0xFF2B2203, false);
        graphics.drawString(font, this.label, x + 9 + 3, y + 1, 0xFFB2E65C, false);
    }
}
