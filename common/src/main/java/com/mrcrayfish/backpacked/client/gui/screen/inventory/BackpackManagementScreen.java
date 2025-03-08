package com.mrcrayfish.backpacked.client.gui.screen.inventory;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.inventory.container.BackpackManagementMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BackpackManagementScreen extends AbstractContainerScreen<BackpackManagementMenu>
{
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/backpack_management.png");

    public BackpackManagementScreen(BackpackManagementMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title);
        this.imageHeight = 119;
        this.inventoryLabelY = this.imageHeight - 94;
        this.titleLabelY = Integer.MIN_VALUE;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
    {
        graphics.blit(GUI_TEXTURE, this.leftPos, this.topPos, 0, 0, 176, 119);
    }
}
