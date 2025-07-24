package com.mrcrayfish.backpacked.client.gui.screen.inventory;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.gui.MouseRestorer;
import com.mrcrayfish.backpacked.client.gui.screen.widget.MiniButton;
import com.mrcrayfish.backpacked.inventory.ManagementInventory;
import com.mrcrayfish.backpacked.inventory.container.BackpackManagementMenu;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageOpenBackpack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
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
    private static final ResourceLocation LABEL_BACKGROUND = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/label");
    private static final ResourceLocation CHECKERS = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/checkers");

    public BackpackManagementScreen(BackpackManagementMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title);
        this.titleLabelX = 17;
        this.titleLabelY = -10;
        // Header height + Slot Height + Footer Height + Gap + Inventory Height
        this.imageHeight = 8 + 18 + 15 + 3 + 101;
        int slotsWidth = menu.getContainer().getContainerSize() * 18;
        this.imageWidth = Math.max(this.imageWidth, 11 + slotsWidth + 11);
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
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFF61503D, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFF404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
    {
        int slotsWidth = this.menu.getContainer().getContainerSize() * 18;
        int backgroundWidth = Math.max(this.imageWidth, 11 + slotsWidth + 11); // Padding + Width + Padding
        int titleWidth = this.font.width(this.title);
        int headerWidth = 7 + titleWidth + 7;
        graphics.blitSprite(LABEL_BACKGROUND, this.leftPos + 10, this.topPos - 16, headerWidth, 20);

        int backgroundX = (this.imageWidth - backgroundWidth) / 2;
        int backgroundHeight = 8 + 18 + 15; // Header height + Slot Height + Footer Height
        graphics.blitSprite(BACKPACK_BACKGROUND, this.leftPos + backgroundX, this.topPos, backgroundWidth, backgroundHeight);

        Slot backpackSlot = this.getMenu().slots.getFirst();
        graphics.blitSprite(BACKPACK_SLOT, this.leftPos + backpackSlot.x - 1, this.topPos + backpackSlot.y - 1, slotsWidth, 18);

        int checkersX = this.leftPos + 10;
        int checkersWidth = (backgroundWidth - 11 - 11 - slotsWidth) / 2 - 1;
        if(checkersWidth > 0)
        {
            graphics.blitSprite(CHECKERS, checkersX, this.topPos + backpackSlot.y - 1, checkersWidth, 18);
            graphics.blitSprite(CHECKERS, checkersX + checkersWidth + slotsWidth + 4, this.topPos + backpackSlot.y - 1, checkersWidth, 18);
        }

        int inventoryY = this.topPos + backgroundHeight + 3;
        graphics.blitSprite(INVENTORY_BACKGROUND, this.leftPos, inventoryY, 176, 101);
        graphics.blitSprite(INVENTORY_SLOT, this.leftPos + 1 + 6, inventoryY + 18, 162, 54);
        graphics.blitSprite(INVENTORY_SLOT, this.leftPos + 1 + 6, inventoryY + 76, 162, 18);
    }

    @Override
    public void removed()
    {
        super.removed();
        MouseRestorer.capturePosition();
    }
}
