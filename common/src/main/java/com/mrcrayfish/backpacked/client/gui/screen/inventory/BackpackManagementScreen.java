package com.mrcrayfish.backpacked.client.gui.screen.inventory;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.Keys;
import com.mrcrayfish.backpacked.client.gui.MouseRestorer;
import com.mrcrayfish.backpacked.client.gui.screen.widget.BackpackButtons;
import com.mrcrayfish.backpacked.inventory.container.BackpackManagementMenu;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageOpenBackpack;
import com.mrcrayfish.framework.api.client.screen.widget.FrameworkButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

public class BackpackManagementScreen extends UnlockableContainerScreen<BackpackManagementMenu>
{
    private static final Component LABEL_NO_BACKPACK = Component.translatable("backpacked.gui.no_backpack_equipped");
    private static final Component LABEL_NO_BACKPACK_PLURAL = Component.translatable("backpacked.gui.no_backpack_equipped.plural");
    private static final Component LABEL_OPEN_BACKPACK_INVENTORY = Component.translatable("backpacked.gui.open_backpack_inventory");

    private static final ResourceLocation BACKPACK_BACKGROUND = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/background");
    private static final ResourceLocation BACKPACK_SLOT = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/slot");
    private static final ResourceLocation INVENTORY_BACKGROUND = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/inventory");
    private static final ResourceLocation INVENTORY_SLOT = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/inventory_slot");
    private static final ResourceLocation LABEL_BACKGROUND = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/label");
    private static final ResourceLocation LABEL_WARNING_BACKGROUND = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/label_warning");
    private static final ResourceLocation CHECKERS = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/checkers");
    private static final ResourceLocation ARROW_RIGHT = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/arrow_right");

    private @Nullable FrameworkButton backButton;

    public BackpackManagementScreen(BackpackManagementMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title);
        this.titleLabelX = 17;
        this.titleLabelY = 6;
        this.imageHeight = 16 + 8 + 18 + 15 + 3 + 101; // Label + Header height + Slot Height + Footer Height + Gap + Inventory Height
        int slotsWidth = menu.getContainer().getContainerSize() * 18;
        this.imageWidth = Math.max(this.imageWidth, 11 + slotsWidth + 11);
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init()
    {
        MouseRestorer.loadCapturedPosition();
        super.init();

        if(this.menu.showInventoryButton())
        {
            this.backButton = this.addRenderableWidget(BackpackButtons.builder()
                .setPosition(this.leftPos + this.imageWidth + 4, this.topPos + (41 - 16) / 2 + 17)
                .setSize(16, 16)
                .setIcon(ARROW_RIGHT, 4, 6)
                .setAction(btn -> {
                    Network.getPlay().sendToServer(new MessageOpenBackpack());
                }).build()
            );
            this.backButton.setTooltip(Tooltip.create(LABEL_OPEN_BACKPACK_INVENTORY));
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
    {
        int titleWidth = this.font.width(this.title);
        graphics.drawString(this.font, this.title, (this.imageWidth - titleWidth) / 2, this.titleLabelY, 0xFF61503D, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFF404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
    {
        if(this.menu.hadNoBackpacksEquippedOnInitialOpen())
        {
            boolean plural = this.menu.getContainer().getContainerSize() > 1;
            Component message = plural ? LABEL_NO_BACKPACK_PLURAL : LABEL_NO_BACKPACK;
            int messageWidth = this.font.width(message);
            int messageBgWidth = 7 + messageWidth + 7;
            int messageY = 8;
            graphics.fillGradient(0, 0, this.width, 50, 0xAA000000, 0x00000000);
            graphics.blitSprite(LABEL_WARNING_BACKGROUND, (this.width - messageBgWidth) / 2, messageY, messageBgWidth, 20);
            graphics.drawString(this.font, message, (this.width - messageWidth) / 2, messageY + 6, 0xFFFFFFFF);
        }

        if(this.menu.showInventoryButton() && this.backButton != null)
        {
            this.backButton.active = this.menu.getContainer().hasAnyMatching(stack -> !stack.isEmpty());
            int backPanelX = this.backButton.getX() - 28;
            int backPanelY = this.backButton.getY() - 5;
            graphics.blitSprite(LABEL_BACKGROUND, backPanelX, backPanelY, 50, 26);
        }

        int slotsWidth = this.menu.getContainer().getContainerSize() * 18;
        int backgroundWidth = Math.max(this.imageWidth, 11 + slotsWidth + 11); // Padding + Width + Padding
        int titleWidth = this.font.width(this.title);
        int headerWidth = 20 + titleWidth + 20;
        int labelX = this.leftPos + (this.imageWidth - headerWidth) / 2;
        graphics.blitSprite(LABEL_BACKGROUND, labelX, this.topPos, headerWidth, 20);

        int titleX = this.leftPos + (this.imageWidth - titleWidth) / 2;
        int titleCheckersX = labelX + 5;
        int titleCheckersWidth = titleX - titleCheckersX - 2;
        if(titleCheckersWidth > 0)
        {
            graphics.blitSprite(CHECKERS, titleCheckersX, this.topPos + 7, titleCheckersWidth, 5);
            graphics.blitSprite(CHECKERS, titleX + titleWidth + 1, this.topPos + 7, titleCheckersWidth, 5);
        }

        int backgroundX = (this.imageWidth - backgroundWidth) / 2;
        int backgroundY = 16;
        int backgroundHeight = 8 + 18 + 15; // Header height + Slot Height + Footer Height
        graphics.blitSprite(BACKPACK_BACKGROUND, this.leftPos + backgroundX, this.topPos + backgroundY, backgroundWidth, backgroundHeight);

        Slot backpackSlot = this.getMenu().slots.getFirst();
        graphics.blitSprite(BACKPACK_SLOT, this.leftPos + backpackSlot.x - 1, this.topPos + backpackSlot.y - 1, slotsWidth, 18);

        int checkersX = this.leftPos + 10;
        int checkersWidth = (backgroundWidth - 11 - 11 - slotsWidth) / 2 - 1;
        if(checkersWidth > 0)
        {
            graphics.blitSprite(CHECKERS, checkersX, this.topPos + backpackSlot.y - 1, checkersWidth, 18);
            graphics.blitSprite(CHECKERS, checkersX + checkersWidth + slotsWidth + 4, this.topPos + backpackSlot.y - 1, checkersWidth, 18);
        }

        int inventoryY = this.topPos + backgroundY + backgroundHeight + 3;
        graphics.blitSprite(INVENTORY_BACKGROUND, this.leftPos, inventoryY, 176, 101);
        graphics.blitSprite(INVENTORY_SLOT, this.leftPos + 1 + 6, inventoryY + 18, 162, 54);
        graphics.blitSprite(INVENTORY_SLOT, this.leftPos + 1 + 6, inventoryY + 76, 162, 18);
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int action)
    {
        if(Keys.KEY_BACKPACK.matches(key, scanCode))
        {
            this.onClose();
            return true;
        }
        return super.keyPressed(key, scanCode, action);
    }

    @Override
    public void removed()
    {
        super.removed();
        MouseRestorer.capturePosition();
    }
}
