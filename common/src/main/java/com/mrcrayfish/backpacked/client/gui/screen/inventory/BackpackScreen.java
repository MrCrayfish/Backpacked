package com.mrcrayfish.backpacked.client.gui.screen.inventory;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.Keys;
import com.mrcrayfish.backpacked.client.gui.screen.CustomiseBackpackScreen;
import com.mrcrayfish.backpacked.client.gui.screen.widget.MiniButton;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainerMenu;
import com.mrcrayfish.backpacked.inventory.container.slot.LockedSlot;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageRequestCustomisation;
import com.mrcrayfish.backpacked.network.message.MessageRequestManagement;
import com.mrcrayfish.backpacked.network.message.MessageUnlockSlot;
import com.mrcrayfish.backpacked.platform.ClientServices;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class BackpackScreen extends AbstractContainerScreen<BackpackContainerMenu>
{
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/backpack.png");
    private static final Component CUSTOMISE_TOOLTIP = Component.translatable("backpacked.button.customise.tooltip");
    private static final Component CONFIG_TOOLTIP = Component.translatable("backpacked.button.config.tooltip");

    private static final ResourceLocation BACKPACK_BACKGROUND = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/background");
    private static final ResourceLocation BACKPACK_SLOT = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/slot");
    private static final ResourceLocation INVENTORY_SPRITE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/inventory");
    private static final ResourceLocation INVENTORY_SLOT = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/inventory_slot");
    private static final ResourceLocation ICON_CUSTOMISE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/customise");
    private static final ResourceLocation ICON_CONFIG = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/config");
    private static final ResourceLocation ICON_LOCK = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/lock");

    private final int cols;
    private final int rows;
    private final boolean owner;
    private boolean opened;
    private @Nullable LockedSlot hoveredLockedSlot;

    public BackpackScreen(BackpackContainerMenu backpackContainerMenu, Inventory playerInventory, Component titleIn)
    {
        super(backpackContainerMenu, playerInventory, titleIn);
        this.cols = backpackContainerMenu.getCols();
        this.rows = backpackContainerMenu.getRows();
        this.owner = backpackContainerMenu.isOwner();
        this.imageWidth = 14 + Math.max(this.cols, 9) * 18;
        this.imageHeight = 114 + this.rows * 18;
        this.inventoryLabelX = Math.max(((this.cols * 18) - (9 * 18)) / 2, 0) + 7;
        this.inventoryLabelY = this.rows * 18 + 17 + 10;
    }

    @Override
    public void init()
    {
        super.init();
        if(!this.opened)
        {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.ARMOR_EQUIP_LEATHER.value(), 0.75F, 1.0F));
            this.opened = true;
        }

        List<MiniButton> buttons = this.gatherButtons();
        for(int i = 0; i < buttons.size(); i++)
        {
            MiniButton button = buttons.get(i);
            switch(Config.CLIENT.buttonAlignment.get())
            {
                case LEFT -> {
                    int titleWidth = this.minecraft.font.width(this.title);
                    button.setX(this.leftPos + titleWidth + 8 + 3 + i * 13);
                }
                case RIGHT -> {
                    button.setX(this.leftPos + this.imageWidth - 7 - 10 - (buttons.size() - 1 - i) * 13);
                }
            }
            button.setY(this.topPos + 5);
            this.addRenderableWidget(button);
        }
    }

    private List<MiniButton> gatherButtons()
    {
        List<MiniButton> buttons = new ArrayList<>();
        buttons.add(new MiniButton(0, 0, ICON_CUSTOMISE, button -> {
            Network.getPlay().sendToServer(new MessageRequestManagement());
        }));
        boolean canCustomise = this.owner && !Config.SERVER.backpack.disableCustomisation.get();
        if(canCustomise)
        {
            MiniButton customiseButton = new MiniButton(0, 0, ICON_CUSTOMISE, onPress -> {
                Network.getPlay().sendToServer(new MessageRequestCustomisation());
            });
            customiseButton.setTooltip(Tooltip.create(CUSTOMISE_TOOLTIP));
            buttons.add(customiseButton);
        }
        if(!Config.CLIENT.hideConfigButton.get())
        {
            MiniButton configButton = new MiniButton(0, 0, ICON_CONFIG, onPress -> this.openConfigScreen());
            configButton.setTooltip(Tooltip.create(CONFIG_TOOLTIP));
            buttons.add(configButton);
        }
        return buttons;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        this.hoveredLockedSlot = null;
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
        if(this.hoveredLockedSlot != null && !this.hoveredLockedSlot.isUnlocked())
        {
            graphics.renderTooltip(this.font, Component.literal("Unlock"), mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
    {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFF3A2B1B, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
    {
        this.drawBackgroundWindow(graphics, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, mouseX, mouseY);
    }

    private void drawBackgroundWindow(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY)
    {
        // Backpack Inventory
        int backpackHeight = 17 + this.rows * 18 + 18;
        graphics.blitSprite(BACKPACK_BACKGROUND, x - 60, y + 4, 70, backpackHeight - 16);
        graphics.blitSprite(BACKPACK_BACKGROUND, x - 4, y - 4, width + 8, backpackHeight);

        // Draw Backpack Slots
        int slotWidth = this.cols * 18;
        int slotHeight = this.rows * 18;
        int minSlotWidth = 9 * 18; //Player inventory will always have 9 columns
        int backpackStartX = Math.max((minSlotWidth - slotWidth) / 2, 0);
        graphics.blitSprite(BACKPACK_SLOT, backpackStartX + x + 7, y + 17, slotWidth, slotHeight);

        // Player Inventory
        graphics.blitSprite(INVENTORY_SPRITE, x, y + backpackHeight - 1, width, 90);

        // Draw Player Inventory Slots
        int inventoryStartX = Math.max((slotWidth - minSlotWidth) / 2, 0);
        graphics.blit(GUI_TEXTURE, x + inventoryStartX + 7, y + backpackHeight + 6, 163, 76, 15, 157, 163, 76, 256, 256);

        for(Slot slot : this.getMenu().slots)
        {
            if(slot instanceof LockedSlot lockedSlot)
            {
                if(this.isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY))
                {
                    this.hoveredLockedSlot = lockedSlot;
                }
                if(!lockedSlot.isUnlocked())
                {
                    graphics.blitSprite(ICON_LOCK, x + slot.x + 2, y + slot.y + 2, 12, 12);
                }
            }
        }
    }

    private void openConfigScreen()
    {
        ClientServices.CLIENT.openConfigScreen();
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
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(this.hoveredLockedSlot != null && !this.hoveredLockedSlot.isUnlocked())
        {
            Network.PLAY.sendToServer(new MessageUnlockSlot(this.hoveredLockedSlot.getContainerSlot()));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
