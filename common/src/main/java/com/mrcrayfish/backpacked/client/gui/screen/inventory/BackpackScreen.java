package com.mrcrayfish.backpacked.client.gui.screen.inventory;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.Keys;
import com.mrcrayfish.backpacked.client.gui.MouseRestorer;
import com.mrcrayfish.backpacked.client.gui.screen.widget.AugmentPopupMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.CustomButton;
import com.mrcrayfish.backpacked.client.gui.screen.widget.EnumButton;
import com.mrcrayfish.backpacked.client.gui.screen.widget.MiniButton;
import com.mrcrayfish.backpacked.common.UnlockableSlotMode;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainerMenu;
import com.mrcrayfish.backpacked.inventory.container.slot.UnlockableSlot;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageNavigateBackpackIndex;
import com.mrcrayfish.backpacked.network.message.MessageRequestCustomisation;
import com.mrcrayfish.backpacked.network.message.MessageRequestManagement;
import com.mrcrayfish.backpacked.network.message.MessageSetAugments;
import com.mrcrayfish.backpacked.platform.ClientServices;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

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

    private static final WidgetSprites AUGMENT_TOGGLE_SPRITES = new WidgetSprites(
        Utils.rl("backpack/augment_toggle_on"),
        Utils.rl("backpack/augment_toggle_off"),
        Utils.rl("backpack/augment_toggle_on_focused"),
        Utils.rl("backpack/augment_toggle_off_focused")
    );
    private static final WidgetSprites AUGMENT_SETTINGS_SPRITES = new WidgetSprites(
        Utils.rl("backpack/augment_settings"),
        Utils.rl("backpack/augment_settings_disabled"),
        Utils.rl("backpack/augment_settings_focused")
    );

    private static final int TITLE_LABEL_WIDTH = 110;
    private static final int TITLE_PADDING = 5;
    private static final int BACKPACK_TOP = 16;
    private static final int BACKPACK_PADDING_TOP = 11;
    private static final int BACKPACK_PADDING_SIDE = 11;
    private static final int BACKPACK_PADDING_BOTTOM = 14;
    private static final int GAP = 3;
    private static final int INVENTORY_WIDTH = 176;
    private static final int INVENTORY_HEIGHT = 101;
    private static final int QUICK_BUTTONS_PADDING = 4;
    private static final int QUICK_BUTTONS_GAP = 2;
    private static final int QUICK_BUTTONS_SIZE = 10;

    private final Player player;
    private final int cols;
    private final int rows;
    private final boolean owner;
    private boolean opened;
    private int buttonCount;
    private int timer;
    private GridLayout augmentsLayout;

    public BackpackScreen(BackpackContainerMenu menu, Inventory playerInventory, Component titleIn)
    {
        super(menu, playerInventory, titleIn);
        this.player = playerInventory.player;
        this.cols = menu.getCols();
        this.rows = menu.getRows();
        this.owner = menu.isOwner();
        this.imageWidth = BACKPACK_PADDING_SIDE + Math.max(this.cols, 9) * 18 + BACKPACK_PADDING_SIDE;
        this.imageHeight = BACKPACK_TOP + BACKPACK_PADDING_TOP + (this.rows * 18) + BACKPACK_PADDING_BOTTOM + GAP + INVENTORY_HEIGHT;
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

        List<AbstractButton> quickButtons = this.gatherQuickButtons();
        int backpackHeight = BACKPACK_PADDING_TOP + (this.rows * 18) + BACKPACK_PADDING_BOTTOM;
        int buttonsHeight = QUICK_BUTTONS_PADDING + (quickButtons.size() * (QUICK_BUTTONS_SIZE + QUICK_BUTTONS_GAP) - QUICK_BUTTONS_GAP) + QUICK_BUTTONS_PADDING;
        int buttonLeft = this.leftPos + this.imageWidth + 2;
        if(buttonsHeight > backpackHeight - QUICK_BUTTONS_PADDING * 2)
        {
            buttonLeft += 6;
        }
        int buttonTop = this.topPos + BACKPACK_TOP + (backpackHeight - buttonsHeight) / 2 + QUICK_BUTTONS_PADDING;
        for(int i = 0; i < quickButtons.size(); i++)
        {
            AbstractButton button = quickButtons.get(i);
            button.setX(buttonLeft);
            button.setY(buttonTop + i * (QUICK_BUTTONS_SIZE + QUICK_BUTTONS_GAP));
            this.addRenderableWidget(button);
        }
        this.buttonCount = quickButtons.size();

        if(this.owner)
        {
            int backpackIndex = this.menu.getBackpackIndex();
            int totalBackpacks = this.menu.getTotalBackpacks();
            Tooltip navigateTooltip = Tooltip.create(
                    Component.literal(Integer.toString(backpackIndex + 1))
                            .append(Component.literal(" / ").withStyle(ChatFormatting.BOLD, ChatFormatting.GRAY))
                            .append(Integer.toString(totalBackpacks))
            );

            int imageCenter = this.imageWidth / 2;
            int navBtnOffset = TITLE_LABEL_WIDTH / 2 + 2;
            MiniButton navPrevious = this.addRenderableWidget(new MiniButton(this.leftPos + imageCenter - navBtnOffset - 12, this.topPos + 3, 12, 12, ICON_PREVIOUS, onPress -> {
                Network.getPlay().sendToServer(new MessageNavigateBackpackIndex(false));
            }));
            navPrevious.setTooltip(navigateTooltip);
            navPrevious.active = backpackIndex > 0;

            MiniButton navNext = this.addRenderableWidget(new MiniButton(this.leftPos + imageCenter + navBtnOffset, this.topPos + 3, 12, 12, ICON_NEXT, onPress -> {
                Network.getPlay().sendToServer(new MessageNavigateBackpackIndex(true));
            }));
            navNext.setTooltip(navigateTooltip);
            navNext.active = backpackIndex < totalBackpacks - 1;

            GridLayout augments = this.createAugmentsPanel();
            augments.arrangeElements();
            augments.visitWidgets(this::addRenderableWidget);
            int augmentsX = this.leftPos - augments.getWidth() - 2;
            if(backpackHeight < 5 + augments.getHeight() + 5)
                augmentsX = this.leftPos - augments.getWidth() - 8;
            augments.setX(augmentsX);
            augments.setY(this.topPos + BACKPACK_TOP + (backpackHeight - augments.getHeight()) / 2);
            this.augmentsLayout = augments;
        }

        this.updateUnlockableSlots();
    }

    private GridLayout createAugmentsPanel()
    {
        GridLayout grid = new GridLayout(this.leftPos - 60, this.topPos + BACKPACK_TOP).spacing(3);
        grid.addChild(this.createAugmentLayout(Augments::first, Augments::setFirst), 0, 0);
        grid.addChild(this.createAugmentLayout(Augments::second, Augments::setSecond), 1, 0);
        grid.addChild(this.createAugmentLayout(Augments::third, Augments::setThird), 2, 0);
        return grid;
    }

    private LinearLayout createAugmentLayout(Function<Augments, Augment<?>> getter, BiFunction<Augments, Augment<?>, Augments> setter)
    {
        LinearLayout layout = LinearLayout.vertical().spacing(0);
        layout.addChild(CustomButton.builder().setSize(20, 16).setAction(btn -> {
            new AugmentPopupMenu(this, getter.apply(this.menu.getAugments()), augment -> {
                Augments updatedAugments = setter.apply(this.menu.getAugments(), augment);
                this.menu.setAugments(updatedAugments);
                Network.getPlay().sendToServer(new MessageSetAugments(updatedAugments));
            }).show(btn);
        }).setIcon(() -> getter.apply(this.menu.getAugments()).type().sprite(), 10, 10).build(), LayoutSettings::alignHorizontallyCenter);
        GridLayout options = new GridLayout().spacing(0);
        options.addChild(CustomButton.builder().setSize(10, 10).setTexture(AUGMENT_TOGGLE_SPRITES).build(), 0, 0);
        options.addChild(CustomButton.builder().setSize(10, 10).setTexture(AUGMENT_SETTINGS_SPRITES).build(), 0, 1);
        layout.addChild(options, LayoutSettings::alignHorizontallyCenter);
        return layout;
    }

    private List<AbstractButton> gatherQuickButtons()
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
                Player player = Minecraft.getInstance().player;
                if(player != null) {
                    int backpackIndex = ModSyncedDataKeys.SELECTED_BACKPACK.getValue(player);
                    Network.getPlay().sendToServer(new MessageRequestCustomisation(backpackIndex));
                }
            });
            customiseButton.setTooltip(Tooltip.create(CUSTOMISE_TOOLTIP));
            buttons.add(customiseButton);
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

        if(!Config.CLIENT.hideConfigButton.get())
        {
            MiniButton configButton = new MiniButton(0, 0, ICON_CONFIG, onPress -> this.openConfigScreen());
            configButton.setTooltip(Tooltip.create(CONFIG_TOOLTIP));
            buttons.add(configButton);
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
            case PURCHASABLE -> this.setHideLockedSlots(!this.getMenu().getController().canAffordNextSlot(this.player, Math.max(1, this.selectedSlots.size() + 1)));
        }
    }

    @Override
    protected void addSlotToSelected(UnlockableSlot slot)
    {
        super.addSlotToSelected(slot);
        this.updateUnlockableSlots();
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
        graphics.drawString(this.font, trimmedTitle, (this.imageWidth - titleWidth) / 2, 6, 0xFF61503D, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFF404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
    {
        this.drawBackgroundWindow(graphics, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, mouseX, mouseY);
    }

    private FormattedCharSequence getTrimmedTitle()
    {
        int maxWidth = TITLE_LABEL_WIDTH - TITLE_PADDING * 2;
        if(this.font.width(this.title) > maxWidth)
        {
            return Language.getInstance().getVisualOrder(FormattedText.composite(this.font.substrByWidth(this.title, maxWidth - this.font.width("...")), FormattedText.of("...")));
        }
        return this.title.getVisualOrderText();
    }

    private void drawBackgroundWindow(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY)
    {
        //graphics.fill(this.leftPos, this.topPos, this.leftPos + this.imageWidth, this.topPos + this.imageHeight, 0xFFFFFFFF);

        FormattedCharSequence trimmedTitle = this.getTrimmedTitle();
        int titleWidth = this.font.width(trimmedTitle);
        int labelX = x + (width - TITLE_LABEL_WIDTH) / 2;
        graphics.blitSprite(LABEL_BACKGROUND, labelX, y, TITLE_LABEL_WIDTH, 21);

        int titleX = x + (width - titleWidth) / 2;
        int checkersX = labelX + 5;
        int checkersWidth = titleX - checkersX - 2;
        if(checkersWidth > 0)
        {
            graphics.blitSprite(CHECKERS, checkersX, y + 7, checkersWidth, 5);
            graphics.blitSprite(CHECKERS, titleX + titleWidth + 1, y + 7, checkersWidth, 5);
        }

        // Calculate the height for the backpack inventory
        int backpackHeight = BACKPACK_PADDING_TOP + (this.rows * 18) + BACKPACK_PADDING_BOTTOM;

        // Draw the background label for the quick action buttons
        int buttonsHeight = QUICK_BUTTONS_PADDING + this.buttonCount * (QUICK_BUTTONS_SIZE + QUICK_BUTTONS_GAP) + QUICK_BUTTONS_PADDING;
        int buttonsX = x + width - 3;
        if(buttonsHeight > backpackHeight - QUICK_BUTTONS_PADDING * 2)
            buttonsX += 6;
        int buttonsY = y + BACKPACK_TOP + (backpackHeight - buttonsHeight) / 2;
        graphics.blitSprite(LABEL_BACKGROUND, buttonsX, buttonsY, 20, buttonsHeight);

        graphics.blitSprite(LABEL_BACKGROUND, this.augmentsLayout.getX() - 5, this.augmentsLayout.getY() - 5, 5 + this.augmentsLayout.getWidth() + 5, 5 + this.augmentsLayout.getHeight() + 5);

        // Backpack Inventory
        graphics.blitSprite(BACKPACK_BACKGROUND, x, y + BACKPACK_TOP, width, backpackHeight);

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
        int inventoryX = (width - INVENTORY_WIDTH) / 2;
        int inventoryY = BACKPACK_TOP + backpackHeight + GAP;
        graphics.blitSprite(INVENTORY_SPRITE, x + inventoryX, y + inventoryY, INVENTORY_WIDTH, INVENTORY_HEIGHT);

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
