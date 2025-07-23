package com.mrcrayfish.backpacked.client.gui.screen.inventory;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.Keys;
import com.mrcrayfish.backpacked.client.gui.ExperienceCostTooltip;
import com.mrcrayfish.backpacked.client.gui.MouseRestorer;
import com.mrcrayfish.backpacked.client.gui.screen.widget.MiniButton;
import com.mrcrayfish.backpacked.common.backpack.UnlockedSlots;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainerMenu;
import com.mrcrayfish.backpacked.inventory.container.slot.LockedSlot;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageNavigateBackpackIndex;
import com.mrcrayfish.backpacked.network.message.MessageRequestCustomisation;
import com.mrcrayfish.backpacked.network.message.MessageRequestManagement;
import com.mrcrayfish.backpacked.network.message.MessageUnlockSlot;
import com.mrcrayfish.backpacked.platform.ClientServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class BackpackScreen extends AbstractContainerScreen<BackpackContainerMenu>
{
    private static final Component MANAGEMENT_TOOLTIP = Component.translatable("backpacked.button.management.tooltip");
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
    private static final ResourceLocation ICON_LOCK = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/lock");
    private static final ResourceLocation ICON_PREVIOUS = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/previous");
    private static final ResourceLocation ICON_NEXT = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/next");
    private static final ResourceLocation CHECKERS = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/checkers");

    private static final int UNLOCK_TIME = 20;
    private static final int TITLE_LABEL_WIDTH = 94;

    private final int cols;
    private final int rows;
    private final boolean owner;
    private final Player openingPlayer;
    private boolean opened;
    private int buttonCount;

    private @Nullable LockedSlot hoveredLockedSlot;
    private LockedSlot clickedLockedSlot;
    private int heldUnlockTime;

    private MiniButton navigateLeftBtn;
    private MiniButton navigateRightBtn;
    private @Nullable Tooltip navigateTooltip;

    public BackpackScreen(BackpackContainerMenu menu, Inventory playerInventory, Component titleIn)
    {
        super(menu, playerInventory, titleIn);
        this.cols = menu.getCols();
        this.rows = menu.getRows();
        this.owner = menu.isOwner();
        this.openingPlayer = playerInventory.player;
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

        List<MiniButton> buttons = this.gatherButtons();
        int buttonStart = this.leftPos + this.imageWidth - 11 - (5 + (buttons.size() * 13 - 3) + 5) + 5;
        for(int i = 0; i < buttons.size(); i++)
        {
            MiniButton button = buttons.get(i);
            button.setX(buttonStart + i * 13);
            button.setY(this.topPos + 4);
            this.addRenderableWidget(button);
        }
        this.buttonCount = buttons.size();

        if(this.owner)
        {
            boolean leftVisible = this.navigateLeftBtn != null && this.navigateLeftBtn.visible;
            this.navigateLeftBtn = this.addRenderableWidget(new MiniButton(this.leftPos + 3, this.topPos + 3, 12, 12, ICON_PREVIOUS, onPress -> {
                Network.getPlay().sendToServer(new MessageNavigateBackpackIndex(false));
            }));
            this.navigateLeftBtn.visible = leftVisible;

            boolean rightVisible = this.navigateRightBtn != null && this.navigateRightBtn.visible;
            this.navigateRightBtn = this.addRenderableWidget(new MiniButton(this.leftPos + 16 + TITLE_LABEL_WIDTH + 2, this.topPos + 3, 12, 12, ICON_NEXT, onPress -> {
                Network.getPlay().sendToServer(new MessageNavigateBackpackIndex(true));
            }));
            this.navigateRightBtn.visible = rightVisible;

            if(this.navigateTooltip != null)
            {
                this.navigateLeftBtn.setTooltip(this.navigateTooltip);
                this.navigateRightBtn.setTooltip(this.navigateTooltip);
            }
        }
    }

    private List<MiniButton> gatherButtons()
    {
        List<MiniButton> buttons = new ArrayList<>();

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

        return buttons;
    }

    @Override
    protected void containerTick()
    {
        if(this.clickedLockedSlot != null)
        {
            // Cancel if the user moves the mouse off the locked slot
            if(this.hoveredLockedSlot != this.clickedLockedSlot)
            {
                this.clickedLockedSlot = null;
                return;
            }
            if(this.heldUnlockTime-- <= 0)
            {
                Network.PLAY.sendToServer(new MessageUnlockSlot(this.clickedLockedSlot.getContainerSlot()));
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.PLAYER_LEVELUP, 1.3F, 0.5F));
                this.clickedLockedSlot = null;
            }
            else if(this.heldUnlockTime % 2 == 0)
            {
                float pitch = 0.9F + 0.4F * (UNLOCK_TIME - this.heldUnlockTime) / (float) UNLOCK_TIME;
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, pitch, 0.25F));
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        this.setupNavigation();

        this.hoveredLockedSlot = null;
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

        if(this.hoveredLockedSlot != null && !this.hoveredLockedSlot.isUnlocked())
        {
            UnlockedSlots slots = this.getMenu().getUnlockedSlots();
            int experienceLevelCost = slots.nextUnlockCost();
            List<ClientTooltipComponent> components = new ArrayList<>();
            components.add(new ExperienceCostTooltip(experienceLevelCost));
            Component unlockHint = this.openingPlayer.experienceLevel >= experienceLevelCost || this.openingPlayer.isCreative()
                    ? Component.translatable("backpacked.gui.hold_to_unlock")
                    : Component.translatable("backpacked.gui.not_enough_exp").withStyle(ChatFormatting.RED);
            components.add(new ClientTextTooltip(unlockHint.getVisualOrderText()));
            ClientServices.CLIENT.drawTooltip(graphics, this.font, components, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE);
        }
    }

    private void setupNavigation()
    {
        if(this.navigateTooltip == null)
        {
            int backpackIndex = this.menu.getBackpackIndex(); // Starts at 1, not 0
            int totalBackpacks = this.menu.getTotalBackpacks();
            this.navigateTooltip = Tooltip.create(
                    Component.literal(Integer.toString(backpackIndex))
                            .append(Component.literal(" / ").withStyle(ChatFormatting.BOLD, ChatFormatting.GRAY))
                            .append(Integer.toString(totalBackpacks))
            );
            this.navigateLeftBtn.setTooltip(this.navigateTooltip);
            this.navigateRightBtn.setTooltip(this.navigateTooltip);
            this.navigateLeftBtn.visible = backpackIndex > 1;
            this.navigateRightBtn.visible = backpackIndex < totalBackpacks;
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

        int buttonsWidth = 5 + (this.buttonCount * 13 - 3) + 5;
        graphics.blitSprite(LABEL_BACKGROUND, x + width - buttonsWidth - 11, y - 1, buttonsWidth, 21);

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

        if(this.clickedLockedSlot != null)
        {
            int progressX = this.leftPos + this.clickedLockedSlot.x;
            int progressY = this.topPos + this.clickedLockedSlot.y;
            int progressWidth = (int) (16 * (UNLOCK_TIME - this.heldUnlockTime) / (float) UNLOCK_TIME);
            graphics.fill(progressX, progressY, progressX + progressWidth, progressY + 16, 0x88A7FF4C);
        }

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
        if(button == 0 && this.hoveredLockedSlot != null && !this.hoveredLockedSlot.isUnlocked())
        {
            UnlockedSlots slots = this.getMenu().getUnlockedSlots();
            if(slots.isUnlockable(this.hoveredLockedSlot.getContainerSlot()))
            {
                int experienceLevelCost = slots.nextUnlockCost();
                if(this.openingPlayer.experienceLevel >= experienceLevelCost || this.openingPlayer.isCreative())
                {
                    this.heldUnlockTime = UNLOCK_TIME;
                    this.clickedLockedSlot = this.hoveredLockedSlot;
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if(button == 0 && this.clickedLockedSlot != null)
        {
            this.clickedLockedSlot = null;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void removed()
    {
        super.removed();
        MouseRestorer.capturePosition();
    }
}
