package com.mrcrayfish.backpacked.client.gui.screen.inventory;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.gui.MouseRestorer;
import com.mrcrayfish.backpacked.inventory.ManagementInventory;
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

    private boolean opened;

    public BackpackManagementScreen(BackpackManagementMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title);
        this.titleLabelX = 11;
        this.titleLabelY = 10;
        // Header height + Slot Height + Footer Height + Gap + Inventory Height
        this.imageHeight = 19 + 18 + 15 + 3 + 101;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init()
    {
        MouseRestorer.loadCapturedPosition();
        super.init();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
    {
        int slotsWidth = ManagementInventory.SIZE * 18;
        int backgroundWidth = Math.max(this.imageWidth, 11 + slotsWidth + 11);
        int backgroundX = (this.imageWidth - backgroundWidth) / 2;
        graphics.drawString(this.font, this.title, backgroundX + this.titleLabelX, this.titleLabelY, 0xFF61503D, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFF404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
    {
        int slotsWidth = ManagementInventory.SIZE * 18;
        int backgroundWidth = Math.max(this.imageWidth, 11 + slotsWidth + 11); // Padding + Width + Padding
        int backgroundHeight = 19 + 18 + 15; // Header height + Slot Height + Footer Height
        int backgroundX = (this.imageWidth - backgroundWidth) / 2;
        graphics.blitSprite(BACKPACK_BACKGROUND, this.leftPos + backgroundX, this.topPos, backgroundWidth, backgroundHeight);

        Slot backpackSlot = this.getMenu().slots.getFirst();
        graphics.blitSprite(BACKPACK_SLOT, this.leftPos + backpackSlot.x - 1, this.topPos + backpackSlot.y - 1, ManagementInventory.SIZE * 18, 18);

        graphics.blitSprite(INVENTORY_BACKGROUND, this.leftPos, this.topPos + 55, 176, 101);
        graphics.blitSprite(INVENTORY_SLOT, this.leftPos + 1 + 6, this.topPos + 55 + 18, 162, 54);
        graphics.blitSprite(INVENTORY_SLOT, this.leftPos + 1 + 6, this.topPos + 55 + 76, 162, 18);
    }

    @Override
    public void removed()
    {
        super.removed();
        MouseRestorer.capturePosition();
    }
}
