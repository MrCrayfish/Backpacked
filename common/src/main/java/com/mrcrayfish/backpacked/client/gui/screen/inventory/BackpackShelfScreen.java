package com.mrcrayfish.backpacked.client.gui.screen.inventory;

import com.mrcrayfish.backpacked.client.gui.MouseRestorer;
import com.mrcrayfish.backpacked.inventory.container.BackpackShelfMenu;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class BackpackShelfScreen extends UnlockableContainerScreen<BackpackShelfMenu>
{
    private static final Identifier BACKPACK_BACKGROUND = Utils.id("backpack/background");
    private static final Identifier BACKPACK_SLOT = Utils.id("backpack/slot");
    private static final Identifier INVENTORY_BACKGROUND = Utils.id("backpack/inventory");
    private static final Identifier INVENTORY_SLOT = Utils.id("backpack/inventory_slot");
    private static final Identifier SHELF = Utils.id("backpack/shelf");

    public BackpackShelfScreen(BackpackShelfMenu menu, Inventory playerInventory, Component title)
    {
        super(menu, playerInventory, title);
        this.imageHeight = 48 + 43 + 3 + 90 + 2;
    }

    @Override
    protected void init()
    {
        MouseRestorer.loadCapturedPosition();
        super.init();
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {}

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
    {
        // Draw the shelf image
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SHELF, this.leftPos + (this.imageWidth - 74) / 2, this.topPos + 12, 74, 33);

        // Draws a slightly transparent slot for the shelf
        Slot shelfSlot = this.getMenu().slots.getFirst();
        graphics.fill(this.leftPos + shelfSlot.x, this.topPos + shelfSlot.y - 1, this.leftPos + shelfSlot.x + 16, this.topPos + shelfSlot.y, 0x11FFFFFF);
        graphics.fill(this.leftPos + shelfSlot.x - 1, this.topPos + shelfSlot.y, this.leftPos + shelfSlot.x + 17, this.topPos + shelfSlot.y + 16, 0x11FFFFFF);
        graphics.fill(this.leftPos + shelfSlot.x, this.topPos + shelfSlot.y + 16, this.leftPos + shelfSlot.x + 16, this.topPos + shelfSlot.y + 17, 0x11FFFFFF);

        // Draws the backpack background
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKPACK_BACKGROUND, this.leftPos, this.topPos + 48, this.imageWidth, 43);

        // Draws a slots for the backpacks
        Slot backpacksFirstSlot = this.getMenu().slots.get(1);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKPACK_SLOT, this.leftPos + backpacksFirstSlot.x - 1, this.topPos + backpacksFirstSlot.y - 1, this.menu.getManagementContainer().getContainerSize() * 18, 18);

        // Draws the background for the player inventory
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, INVENTORY_BACKGROUND, this.leftPos, this.topPos + 94, 176, 90);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, INVENTORY_SLOT, this.leftPos + 1 + 6, this.topPos + 94 + 7, 162, 54);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, INVENTORY_SLOT, this.leftPos + 1 + 6, this.topPos + 94 + 65, 162, 18);
    }

    @Override
    public void removed()
    {
        super.removed();
        MouseRestorer.capturePosition();
    }
}
