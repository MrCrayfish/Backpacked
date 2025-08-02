package com.mrcrayfish.backpacked.client.gui.screen.inventory;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.Keys;
import com.mrcrayfish.backpacked.client.gui.MouseRestorer;
import com.mrcrayfish.backpacked.client.gui.screen.widget.MiniButton;
import com.mrcrayfish.backpacked.client.gui.screen.widget.EnumButton;
import com.mrcrayfish.backpacked.common.UnlockableSlotMode;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainerMenu;
import com.mrcrayfish.backpacked.inventory.container.UnlockableContainerScreen;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageNavigateBackpackIndex;
import com.mrcrayfish.backpacked.network.message.MessageRequestCustomisation;
import com.mrcrayfish.backpacked.network.message.MessageRequestManagement;
import com.mrcrayfish.backpacked.platform.ClientServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class BackpackScreen extends UnlockableContainerScreen<BackpackContainerMenu>
{
    private static final Component MANAGEMENT_TOOLTIP = Component.translatable("backpacked.gui.manage_backpacks");
    private static final Component CUSTOMISE_TOOLTIP = Component.translatable("backpacked.button.customise.tooltip");
    private static final Component CONFIG_TOOLTIP = Component.translatable("backpacked.button.config.tooltip");

    private static final ResourceLocation BACKPACK_BACKGROUND = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/background");
    private static final ResourceLocation BACKPACK_SLOT = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/slot");
    private static final ResourceLocation INVENTORY_SPRITE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/inventory");
    private static final ResourceLocation INVENTORY_SLOT = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/inventory_slot");
    private static final ResourceLocation LABEL_BACKGROUND = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/label");
    private static final ResourceLocation ICON_MANAGEMENT = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/management");
    private static final ResourceLocation ICON_CUSTOMISE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/customise");
    private static final ResourceLocation ICON_CONFIG = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/config");
    private static final ResourceLocation ICON_PREVIOUS = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/previous");
    private static final ResourceLocation ICON_NEXT = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/next");
    private static final ResourceLocation CHECKERS = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/checkers");

    private static final int TITLE_LABEL_WIDTH = 94;

    private final int cols;
    private final int rows;
    private final boolean owner;
    private boolean opened;
    private int buttonCount;
    private int timer;

    public BackpackScreen(BackpackContainerMenu menu, Inventory playerInventory, Component titleIn)
    {
        super(menu, playerInventory, titleIn);
        this.cols = menu.getCols();
        this.rows = menu.getRows();
        this.owner = menu.isOwner();
        this.imageWidth = 11 + Math.max(this.cols, 9) * 18 + 11;
        this.imageHeight = 26 + this.rows * 18 + 15 + 3 + 101;
        this.titleLabelX = 6;
        this.titleLabelY = 0;
        this.inventoryLabelX = this.imageWidth / 2 - 80;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void init()
    {
        MouseRestorer.loadCapturedPosition();

        super.init();

        if(!this.opened)
        {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.ARMOR_EQUIP_LEATHER.value(), 0.75F, 1.0F));
            this.opened = true;
        }

        List<AbstractButton> buttons = this.gatherButtons();
        int buttonsWidth = 5 + (buttons.size() * 11 - 1) + 5;
        int buttonStart = this.leftPos + this.imageWidth - 5 - buttonsWidth + 5;
        for(int i = 0; i < buttons.size(); i++)
        {
            AbstractButton button = buttons.get(i);
            button.setX(buttonStart + i * 11);
            button.setY(this.topPos + 4);
            this.addRenderableWidget(button);
        }
        this.buttonCount = buttons.size();

        if(this.owner)
        {
            int backpackIndex = this.menu.getBackpackIndex();
            int totalBackpacks = this.menu.getTotalBackpacks();
            Tooltip navigateTooltip = Tooltip.create(
                    Component.literal(Integer.toString(backpackIndex + 1))
                            .append(Component.literal(" / ").withStyle(ChatFormatting.BOLD, ChatFormatting.GRAY))
                            .append(Integer.toString(totalBackpacks))
            );

            MiniButton navPrevious = this.addRenderableWidget(new MiniButton(this.leftPos + 2, this.topPos + 3, 12, 12, ICON_PREVIOUS, onPress -> {
                Network.getPlay().sendToServer(new MessageNavigateBackpackIndex(false));
            }));
            navPrevious.setTooltip(navigateTooltip);
            navPrevious.active = backpackIndex > 0;

            MiniButton navNext = this.addRenderableWidget(new MiniButton(this.leftPos + 16 + TITLE_LABEL_WIDTH + 2, this.topPos + 3, 12, 12, ICON_NEXT, onPress -> {
                Network.getPlay().sendToServer(new MessageNavigateBackpackIndex(true));
            }));
            navNext.setTooltip(navigateTooltip);
            navNext.active = backpackIndex < totalBackpacks - 1;
        }

        this.updateUnlockableSlots();
    }

    private List<AbstractButton> gatherButtons()
    {
        List<AbstractButton> buttons = new ArrayList<>();

        MiniButton manageButton = new MiniButton(0, 0, ICON_MANAGEMENT, button -> {
            Network.getPlay().sendToServer(new MessageRequestManagement());
        });
        manageButton.setTooltip(Tooltip.create(MANAGEMENT_TOOLTIP));
        buttons.add(manageButton);

        boolean canCustomise = this.owner && !Config.BACKPACK.cosmetics.disableCustomisation.get();
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

        if(!Config.BACKPACK.inventory.slots.unlockAllSlots.get())
        {
            EnumButton<UnlockableSlotMode> lockButton = new EnumButton<>(0, 0, 10, 10, Config.CLIENT.unlockableSlotMode.get(), (btn, value) -> {
                if(Config.CLIENT.unlockableSlotMode.get() != value) {
                    Config.CLIENT.unlockableSlotMode.set(value);
                    btn.setTooltip(this.createLockTooltip(value));
                }
                this.updateUnlockableSlots();
            });
            lockButton.setTooltip(this.createLockTooltip(Config.CLIENT.unlockableSlotMode.get()));
            buttons.add(lockButton);
        }

        return buttons;
    }

    private Tooltip createLockTooltip(UnlockableSlotMode mode)
    {
        MutableComponent tooltip = Component.literal("");
        tooltip.append(Component.translatable("backpacked.button.unlockable_slot_mode.tooltip").withStyle(ChatFormatting.UNDERLINE));
        tooltip.append("\n");
        tooltip = tooltip.append(Component.translatable(mode.getKey()).withStyle(mode.getFormat()));
        return Tooltip.create(tooltip);
    }

    private void updateUnlockableSlots()
    {
        if(Config.BACKPACK.inventory.slots.unlockAllSlots.get())
        {
            this.setHideLockedSlots(false);
            return;
        }

        switch(Config.CLIENT.unlockableSlotMode.get())
        {
            case ENABLED -> this.setHideLockedSlots(false);
            case DISABLED -> this.setHideLockedSlots(true);
            case PURCHASABLE -> this.setHideLockedSlots(!this.canUnlockNextSlot());
        }
    }

    @Override
    protected void containerTick()
    {
        super.containerTick();

        if(Config.CLIENT.unlockableSlotMode.get() == UnlockableSlotMode.PURCHASABLE)
        {
            if(this.timer-- <= 0)
            {
                this.updateUnlockableSlots();
                this.timer = 5;
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
    {
        FormattedCharSequence trimmedTitle = this.getTrimmedTitle();
        int titleWidth = this.font.width(trimmedTitle);
        graphics.drawString(this.font, trimmedTitle, 16 + (TITLE_LABEL_WIDTH - titleWidth) / 2, 5, 0xFF61503D, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFF404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
    {
        this.drawBackgroundWindow(graphics, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, mouseX, mouseY);
    }

    private FormattedCharSequence getTrimmedTitle()
    {
        int maxWidth = TITLE_LABEL_WIDTH - 10;
        if(this.font.width(this.title) > maxWidth)
        {
            return Language.getInstance().getVisualOrder(FormattedText.composite(this.font.substrByWidth(this.title, maxWidth - this.font.width("...")), FormattedText.of("...")));
        }
        return this.title.getVisualOrderText();
    }

    private void drawBackgroundWindow(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY)
    {
        //graphics.fill(this.leftPos, this.topPos, this.leftPos + this.imageWidth, this.topPos + this.imageHeight, 0xFFFFFFFF);
        graphics.blitSprite(LABEL_BACKGROUND, x + 16, y - 1, TITLE_LABEL_WIDTH, 21);

        FormattedCharSequence trimmedTitle = this.getTrimmedTitle();
        int titleWidth = this.font.width(trimmedTitle);
        int titleX = x + 16 + (TITLE_LABEL_WIDTH - titleWidth) / 2;
        int checkersX = x + 21;
        int checkersWidth = titleX - checkersX - 1;
        if(checkersWidth > 0)
        {
            graphics.blitSprite(CHECKERS, checkersX, y + 7, checkersWidth, 5);
            graphics.blitSprite(CHECKERS, titleX + titleWidth + 1, y + 7, checkersWidth, 5);
        }

        int buttonsWidth = 5 + (this.buttonCount * 11 - 1) + 5;
        int buttonsX = x + width - 5 - buttonsWidth;
        graphics.blitSprite(LABEL_BACKGROUND, buttonsX, y - 1, buttonsWidth, 21);

        // Backpack Inventory
        int backpackHeight = 20 + this.rows * 18 + 15;
        //graphics.blitSprite(BACKPACK_BACKGROUND, x - 60, y + 8, 70, backpackHeight - 16);
        graphics.blitSprite(BACKPACK_BACKGROUND, x, y + 16, width, backpackHeight - 10);

        // Draw Backpack Slots
        int backpackSlotsWidth = this.cols * 18;
        int backpackSlotsHeight = this.rows * 18;
        int backpackSlotsX = (width - backpackSlotsWidth) / 2;
        int backpackSlotsY = 27;
        graphics.blitSprite(BACKPACK_SLOT, x + backpackSlotsX, y + backpackSlotsY, backpackSlotsWidth, backpackSlotsHeight);

        int backpackCheckersWidth = (width - 11 - 11 - backpackSlotsWidth) / 2 - 3;
        if(backpackCheckersWidth > 0)
        {
            graphics.blitSprite(CHECKERS, x + 11, y + 27, backpackCheckersWidth, backpackSlotsHeight);
            graphics.blitSprite(CHECKERS, x + backpackSlotsX + backpackSlotsWidth + 3, y + 27, backpackCheckersWidth, backpackSlotsHeight);
        }

        // Player Inventory
        int inventoryWidth = 7 + 9 * 18 + 7;
        int inventoryHeight = 101;
        int inventoryX = (width - inventoryWidth) / 2;
        int inventoryY = backpackHeight + 9;
        graphics.blitSprite(INVENTORY_SPRITE, x + inventoryX, y + inventoryY, inventoryWidth, inventoryHeight);

        // Draw Player Inventory Slots
        int inventorySlotsWidth = 9 * 18;
        int inventorySlotsHeight = 3 * 18;
        int inventorySlotsX = (width - inventorySlotsWidth) / 2;
        int inventorySlotsY = inventoryY + 18;
        graphics.blitSprite(INVENTORY_SLOT, x + inventorySlotsX, y + inventorySlotsY, inventorySlotsWidth, inventorySlotsHeight);
        graphics.blitSprite(INVENTORY_SLOT, x + inventorySlotsX, y + inventorySlotsY + inventorySlotsHeight + 4, 9 * 18, 18);
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
    public void removed()
    {
        super.removed();
        MouseRestorer.capturePosition();
    }
}
