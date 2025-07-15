package com.mrcrayfish.backpacked.client.gui.screen.inventory;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.inventory.container.BackpackManagementMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class BackpackManagementScreen extends AbstractContainerScreen<BackpackManagementMenu>
{
    private static final ResourceLocation BACKPACK_BACKGROUND = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/background");
    private static final ResourceLocation BACKPACK_SLOT = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/slot");
    private static final ResourceLocation INVENTORY_BACKGROUND = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/inventory");
    private static final ResourceLocation INVENTORY_SLOT = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/inventory_slot");

    public BackpackManagementScreen(BackpackManagementMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title);
        this.imageHeight = 43 + 3 + 90;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {}

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
    {
        graphics.blitSprite(BACKPACK_BACKGROUND, this.leftPos, this.topPos, this.imageWidth, 43);

        Slot backpackSlot = this.getMenu().slots.getFirst();
        graphics.blitSprite(BACKPACK_SLOT, this.leftPos + backpackSlot.x - 1, this.topPos + backpackSlot.y - 1, 18, 18);

        graphics.blitSprite(INVENTORY_BACKGROUND, this.leftPos, this.topPos + 46, 176, 90);
        graphics.blitSprite(INVENTORY_SLOT, this.leftPos + 1 + 6, this.topPos + 46 + 7, 162, 54);
        graphics.blitSprite(INVENTORY_SLOT, this.leftPos + 1 + 6, this.topPos + 46 + 65, 162, 18);
    }
}
