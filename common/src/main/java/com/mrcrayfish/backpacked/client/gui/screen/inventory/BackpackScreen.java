package com.mrcrayfish.backpacked.client.gui.screen.inventory;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.Icons;
import com.mrcrayfish.backpacked.client.Keys;
import com.mrcrayfish.backpacked.client.augment.AugmentHolder;
import com.mrcrayfish.backpacked.client.augment.AugmentSettingsFactories;
import com.mrcrayfish.backpacked.client.gui.ExperienceCostTooltip;
import com.mrcrayfish.backpacked.client.gui.ItemCostTooltip;
import com.mrcrayfish.backpacked.client.gui.MouseRestorer;
import com.mrcrayfish.backpacked.client.gui.screen.widget.*;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.TextInputMenu;
import com.mrcrayfish.backpacked.common.ItemSorting;
import com.mrcrayfish.backpacked.common.Pagination;
import com.mrcrayfish.backpacked.common.UnlockableSlotMode;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainerMenu;
import com.mrcrayfish.backpacked.inventory.container.UnlockableController;
import com.mrcrayfish.backpacked.inventory.container.slot.UnlockableSlot;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.*;
import com.mrcrayfish.backpacked.platform.ClientServices;
import com.mrcrayfish.backpacked.util.ScreenUtil;
import com.mrcrayfish.backpacked.util.Utils;
import com.mrcrayfish.framework.api.client.screen.widget.Buttons;
import com.mrcrayfish.framework.api.client.screen.widget.FrameworkButton;
import com.mrcrayfish.framework.api.client.screen.widget.element.Icon;
import com.mrcrayfish.framework.api.client.screen.widget.element.Sound;
import com.mrcrayfish.framework.api.client.screen.widget.input.Action;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
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
import java.util.EnumMap;
import java.util.List;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class BackpackScreen extends UnlockableContainerScreen<BackpackContainerMenu>
{
    private static final Component MANAGEMENT_TOOLTIP = Component.translatable("backpacked.gui.manage_backpacks");
    private static final Component CUSTOMISE_TOOLTIP = Component.translatable("backpacked.button.customise.tooltip");
    private static final Component CONFIG_TOOLTIP = Component.translatable("backpacked.button.config.tooltip");
    private static final Component CONFIGURE = Component.translatable("backpacked.gui.configure");
    private static final Component SWAP_AUGMENT = Component.translatable("backpacked.gui.swap_augment");
    private static final Component RENAME = Component.translatable("backpacked.gui.rename");
    private static final Component SORT = Component.translatable("backpacked.gui.sort");
    public static final Function<Component, MutableComponent> HOLD_TO_EXPAND = component -> Component.translatable("backpacked.gui.hold_button_to_expand", component);
    private static final Component CLICK_TO_UNLOCK = Component.translatable("backpacked.gui.click_to_unlock");
    private static final Component AUGMENT_BAY = Component.translatable("backpacked.gui.augment_bay");
    private static final Component NOT_ENOUGH_EXP = Component.translatable("backpacked.gui.not_enough_exp");
    private static final Component MISSING_ITEMS = Component.translatable("backpacked.gui.missing_items");

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
    private static final ResourceLocation ICON_RENAME = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/rename");
    private static final ResourceLocation ICON_SORT = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/sort");
    private static final ResourceLocation ICON_LOCK = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/lock");
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
    private static final WidgetSprites BUTTON_TEXTURES = new WidgetSprites(
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/button_enabled"),
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/button_disabled"),
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/button_enabled_focused")
    );
    private static final WidgetSprites DISABLED_BUTTON_TEXTURES = new WidgetSprites(
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/button_disabled"),
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/button_disabled")
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
    private static final int LABEL_PADDING = 5;

    private static ItemSorting sorting = ItemSorting.ALPHABETICAL;

    private final Player player;
    private final int cols;
    private final int rows;
    private final boolean owner;
    private boolean opened;
    private int timer;
    private final List<Layout> layouts = new ArrayList<>();
    private EnumMap<Augments.Position, FrameworkButton> augmentsButtons;

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
        this.augmentsButtons = new EnumMap<>(Augments.Position.class);
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

        this.layouts.clear();

        Layout titleLayout = this.createTitleLayout();
        titleLayout.arrangeElements();
        titleLayout.setPosition(this.leftPos + (this.imageWidth - titleLayout.getWidth()) / 2, this.topPos + LABEL_PADDING);
        titleLayout.visitWidgets(this::addRenderableWidget);
        this.layouts.add(titleLayout);

        LinearLayout actionsLayout = this.createActionsLayout();
        actionsLayout.arrangeElements();
        actionsLayout.visitWidgets(this::addRenderableWidget);
        int backpackHeight = BACKPACK_PADDING_TOP + (this.rows * 18) + BACKPACK_PADDING_BOTTOM;
        int buttonsHeight = LABEL_PADDING + actionsLayout.getHeight() + LABEL_PADDING;
        int buttonLeft = this.leftPos + this.imageWidth + 2;
        if(buttonsHeight > backpackHeight - LABEL_PADDING * 2) // TODO temporary
        {
            buttonLeft += 6;
        }
        int buttonTop = this.topPos + BACKPACK_TOP + (backpackHeight - buttonsHeight) / 2 + LABEL_PADDING;
        actionsLayout.setPosition(buttonLeft, buttonTop);
        this.layouts.add(actionsLayout);

        Pagination pagination = this.menu.getPagination();
        if(pagination.totalPages() > 1)
        {
            Tooltip navigateTooltip = Tooltip.create(
                Component.literal(Integer.toString(pagination.currentPage()))
                    .append(Component.literal(" / ").withStyle(ChatFormatting.BOLD, ChatFormatting.GRAY))
                    .append(Integer.toString(pagination.totalPages()))
            );

            MiniButton navPrevious = this.addRenderableWidget(new MiniButton(titleLayout.getX() - 12 - LABEL_PADDING - 2, this.topPos + 3, 12, 12, ICON_PREVIOUS, onPress -> {
                pagination.previousPage();
            }));
            navPrevious.setTooltip(navigateTooltip);
            navPrevious.active = pagination.currentPage() > 1;

            MiniButton navNext = this.addRenderableWidget(new MiniButton(titleLayout.getX() + titleLayout.getWidth() + LABEL_PADDING + 2, this.topPos + 3, 12, 12, ICON_NEXT, onPress -> {
                pagination.nextPage();
            }));
            navNext.setTooltip(navigateTooltip);
            navNext.active = pagination.currentPage() < pagination.totalPages();
        }

        if(this.owner)
        {
            LinearLayout augmentsLayout = this.createAugmentsPanel();
            augmentsLayout.arrangeElements();
            augmentsLayout.visitWidgets(this::addRenderableWidget);
            int augmentsX = this.leftPos - augmentsLayout.getWidth() - 2;
            if(backpackHeight < 5 + augmentsLayout.getHeight() + 5) // TODO temporary
                augmentsX = this.leftPos - augmentsLayout.getWidth() - 8;
            augmentsLayout.setX(augmentsX);
            augmentsLayout.setY(this.topPos + BACKPACK_TOP + (backpackHeight - augmentsLayout.getHeight()) / 2);
            this.layouts.add(augmentsLayout);
        }

        this.updateUnlockableSlots();
    }

    private LinearLayout createAugmentsPanel()
    {
        LinearLayout layout = LinearLayout.vertical().spacing(2);
        UnlockableController bays = this.menu.getAugmentBayController();
        var positions = Augments.Position.values();
        for(int i = 0; i < bays.getMaxSlots() && i < positions.length; i++)
        {
            if(i != 0) layout.addChild(Divider.horizontal(30).colour(0xFFE0CDB7));
            layout.addChild(this.createAugmentLayout(positions[i]));
        }
        return layout;
    }

    private LinearLayout createAugmentLayout(Augments.Position position)
    {
        LinearLayout layout = LinearLayout.horizontal().spacing(1);
        FrameworkButton augmentBtn = layout.addChild(FrameworkButton.builder()
            .setSize(20, 20)
            .setTexture(() -> {
                if(!this.menu.getAugmentBayController().isSlotUnlocked(position.ordinal()))
                    return DISABLED_BUTTON_TEXTURES;
                return BUTTON_TEXTURES;
            })
            .setIcon(btn -> new AugmentIcon(btn, this.menu, position))
            .setDependent(() -> {
                if(!this.menu.getAugmentBayController().isSlotUnlocked(position.ordinal())) {
                    return this.menu.getAugmentBayController().canAffordNextSlot(this.minecraft.player, 1);
                }
                return true;
            })
            .setAction(btn -> {
                if(!this.menu.getAugmentBayController().isSlotUnlocked(position.ordinal())) {
                    Network.getPlay().sendToServer(new MessageUnlockAugmentBay(position));
                    return;
                }
                new AugmentPopupMenu(this, this.menu::getAugments, augment -> {
                    Network.getPlay().sendToServer(new MessageChangeAugment(position, augment));
                }).show(btn);
            }).setTooltip(btn -> {
                if(!this.menu.getAugmentBayController().isSlotUnlocked(position.ordinal())) {
                    return null;
                }
                AugmentType<?> type = this.menu.getAugments().getAugment(position).type();
                List<Component> lines = new ArrayList<>();
                lines.add(SWAP_AUGMENT);
                lines.add(type.name().plainCopy().withStyle(ChatFormatting.BLUE));
                AugmentType<?> depends = type.requires().get();
                if(depends != null) {
                    lines.add(Component.translatable("backpacked.gui.requires_augment", depends.name()).withStyle(ChatFormatting.LIGHT_PURPLE));
                }
                // Empty type should not add a description
                if(!type.isEmpty()) {
                    String rawDescription = type.description().getString();
                    int firstBreak = rawDescription.indexOf("\n");
                    if(!Screen.hasShiftDown() && firstBreak != -1) {
                        rawDescription = "• " + rawDescription.substring(0, firstBreak);
                    } else {
                        rawDescription = "• " + rawDescription.replace("\n", "\n• ");
                    }
                    lines.add(Component.literal(rawDescription).withStyle(ChatFormatting.GRAY));
                    if(!Screen.hasShiftDown() && firstBreak != -1) {
                        lines.add(HOLD_TO_EXPAND.apply(ScreenUtil.getShiftIcon()).withStyle(ChatFormatting.DARK_GRAY));
                    }
                }
                if(Minecraft.getInstance().options.advancedItemTooltips)
                    lines.add(Component.literal(type.id().toString()).withStyle(ChatFormatting.DARK_GRAY));
                return ScreenUtil.createMultilineTooltip(lines);
            }).setTooltipOptions(TooltipOptions.REBUILD_TOOLTIP_ON_SHIFT).build(), LayoutSettings::alignHorizontallyCenter);
        this.augmentsButtons.put(position, augmentBtn);

        // Adds a toggle and settings button for the augment
        GridLayout options = new GridLayout().spacing(0);
        options.addChild(BackpackButtons.state(() -> {
                return this.menu.getAugments().getState(position);
            }, newValue -> {
                this.updateAugments(this.menu.getAugments().setState(position, newValue));
            }, newValue -> {
                Network.getPlay().sendToServer(new MessageSetAugmentState(position, newValue));
            }).setIcon(btn -> () -> {
                boolean state = this.menu.getAugments().getState(position);
                return AUGMENT_TOGGLE_SPRITES.get(state, btn.isHovered() && btn.isActive());
            }, 10, 10).setDependent(() -> {
                return this.menu.getAugmentBayController().isSlotUnlocked(position.ordinal());
            }).noTexture().setSize(10, 10).build(), 0, 0);
        options.addChild(FrameworkButton.builder()
            .setSize(10, 10)
            .setTexture(AUGMENT_SETTINGS_SPRITES)
            .setTooltip(btn -> Tooltip.create(CONFIGURE))
            .setTooltipDelay(0)
            .setTooltipOptions(TooltipOptions.DISABLE_TOOLTIP_WHEN_WIDGET_INACTIVE)
            .setAction(btn -> {
                Augment<?> augment = this.menu.getAugments().getAugment(position);
                var factory = AugmentSettingsFactories.getFactory(augment);
                if(factory == null)
                    return;
                AugmentHolder<Augment<?>> holder = new AugmentHolder<>(() -> this.menu.getAugments().getAugment(position), updatedAugment -> {
                    Network.getPlay().sendToServer(new MessageUpdateAugment(position, updatedAugment));
                    this.updateAugments(this.menu.getAugments().setAugment(position, updatedAugment));
                }, position, this.menu.getBackpackIndex());
                factory.apply(this, holder).show(btn);
            }).setDependent(() -> {
                if(!this.menu.getAugmentBayController().isSlotUnlocked(position.ordinal()))
                    return false;
                // Setting button should only be active if it has a settings factory
                AugmentType<?> type = this.menu.getAugments().getAugment(position).type();
                return AugmentSettingsFactories.hasFactory(type);
            }).build(), 1, 0);
        layout.addChild(options, LayoutSettings::alignHorizontallyCenter);

        return layout;
    }

    private void updateAugments(Augments augments)
    {
        this.menu.setAugments(augments);
    }

    public void updateAugment(Augments.Position position, Augment<?> augment)
    {
        this.menu.setAugments(this.menu.getAugments().setAugment(position, augment));
        FrameworkButton augmentBtn = this.augmentsButtons.get(position);
        if(augmentBtn != null)
        {
            augmentBtn.rebuildTooltip();
        }
    }

    private Layout createTitleLayout()
    {
        LinearLayout layout = LinearLayout.horizontal().spacing(2);
        TitleWidget title = new TitleWidget(this.getTrimmedTitle(), this.title, Minecraft.getInstance().font);
        title.setWidth(TITLE_LABEL_WIDTH);
        layout.addChild(title);
        if(this.owner)
        {
            title.setShift(6);
            layout.addChild(new MiniButton(0, 0, ICON_RENAME, onPress -> {
                new TextInputMenu(this, this.title.getString(), 50, s -> {
                    Network.PLAY.sendToServer(new MessageRenameBackpack(s));
                }).show(this.getRectangle());
            })).setTooltip(Tooltip.create(RENAME));
        }
        return layout;
    }

    private LinearLayout createActionsLayout()
    {
        LinearLayout layout = LinearLayout.vertical().spacing(2);

        layout.addChild(FrameworkButton.builder()
            .setSize(10, 10)
            .setIcon(ICON_SORT, 10, 10)
            .setTooltipDelay(0)
            .setTooltip(btn -> ScreenUtil.createMultilineTooltip(List.of(
                SORT, sorting.label().plainCopy().withStyle(ChatFormatting.BLUE),
                Component.translatable("backpacked.gui.cycle_sort_mode", ScreenUtil.getIconComponent(Icons.MIDDLE_MOUSE)).withStyle(ChatFormatting.DARK_GRAY)
            )))
            .setPrimaryAction(btn -> {
                Network.getPlay().sendToServer(new MessageSortBackpack(sorting));
            })
            .setTertiaryAction(Action.create(btn -> {
                ItemSorting[] values = ItemSorting.values();
                sorting = values[(sorting.ordinal() + 1) % values.length];
            }, Sound.create(SoundEvents.WOODEN_BUTTON_CLICK_ON)))
            .setContentRenderer((btn, graphics, mouseX, mouseY, partialTick) -> {
                Icon icon = btn.getIcon();
                if(icon != null) {
                    icon.draw(graphics, btn.getX(), btn.getY(), partialTick);
                }
                if(btn.isHovered() && btn.isActive()) {
                    graphics.fillGradient(btn.getX(), btn.getY(), btn.getX() + btn.getWidth(), btn.getY() + btn.getHeight(), -2130706433, -2130706433);
                }
            }).build());

        if(this.owner && !Config.BACKPACK.cosmetics.disableCustomisation.get())
        {
            MiniButton customiseButton = layout.addChild(new MiniButton(0, 0, ICON_CUSTOMISE, onPress -> {
                Player player = Minecraft.getInstance().player;
                if(player != null) {
                    int backpackIndex = ModSyncedDataKeys.SELECTED_BACKPACK.getValue(player);
                    Network.getPlay().sendToServer(new MessageRequestCustomisation(backpackIndex));
                }
            }));
            customiseButton.setTooltip(Tooltip.create(CUSTOMISE_TOOLTIP));
        }

        if(!Config.BACKPACK.inventory.slots.unlockAllSlots.get())
        {
            EnumButton<UnlockableSlotMode> lockButton = layout.addChild(new EnumButton<>(0, 0, 10, 10, Config.CLIENT.unlockableSlotMode.get(), (btn, value) -> {
                if(Config.CLIENT.unlockableSlotMode.get() != value) {
                    Config.CLIENT.unlockableSlotMode.set(value);
                    btn.setTooltip(this.createLockTooltip(value));
                }
                this.updateUnlockableSlots();
            }));
            lockButton.setTooltip(this.createLockTooltip(Config.CLIENT.unlockableSlotMode.get()));
        }

        if(this.owner || !Config.CLIENT.hideConfigButton.get())
            layout.addChild(Divider.horizontal(10).colour(0xFFE0CDB7));

        if(this.owner)
        {
            MiniButton manageButton = layout.addChild(new MiniButton(0, 0, ICON_MANAGEMENT, button -> {
                Network.getPlay().sendToServer(new MessageRequestManagement());
            }));
            manageButton.setTooltip(Tooltip.create(MANAGEMENT_TOOLTIP));
        }

        if(!Config.CLIENT.hideConfigButton.get())
        {
            MiniButton configButton = layout.addChild(new MiniButton(0, 0, ICON_CONFIG, onPress -> this.openConfigScreen()));
            configButton.setTooltip(Tooltip.create(CONFIG_TOOLTIP));
        }

        return layout;
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
            case PURCHASABLE -> this.setHideLockedSlots(!this.getMenu().getSlotController().canAffordNextSlot(this.player, Math.max(1, this.selectedSlots.size() + 1)));
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
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY)
    {
        // Draws the cost tooltip to unlock the augment bay
        for(var entry : this.augmentsButtons.entrySet())
        {
            if(!this.menu.getAugmentBayController().isSlotUnlocked(entry.getKey().ordinal()) && entry.getValue().isHovered())
            {
                List<ClientTooltipComponent> components = this.createUnlockAugmentBayTooltip();
                ClientServices.CLIENT.drawTooltip(graphics, this.font, components, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE);
                return;
            }
        }
        super.renderTooltip(graphics, mouseX, mouseY);
    }

    protected List<ClientTooltipComponent> createUnlockAugmentBayTooltip()
    {
        Component hintText = CLICK_TO_UNLOCK;
        UnlockableController bayController = this.menu.getAugmentBayController();
        int nextCost = bayController.getNextUnlockCost(1);
        boolean canAfford = bayController.canAffordNextSlot(this.player, 1);
        List<ClientTooltipComponent> components = new ArrayList<>();
        components.add(new ClientTextTooltip(AUGMENT_BAY.copy().withStyle(ChatFormatting.GRAY).getVisualOrderText()));
        switch(bayController.getCostModel().getPaymentType()) {
            case EXPERIENCE -> {
                if(!canAfford) hintText = NOT_ENOUGH_EXP.copy().withStyle(ChatFormatting.RED);
                components.add(new ClientTextTooltip(hintText.getVisualOrderText()));
                components.add(new ExperienceCostTooltip(nextCost));
            }
            case ITEM -> {
                if(!canAfford) hintText = MISSING_ITEMS.copy().withStyle(ChatFormatting.RED);
                components.add(new ClientTextTooltip(hintText.getVisualOrderText()));
                components.add(new ItemCostTooltip(bayController.getPaymentItem(), nextCost));
            }
        }
        return components;
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
    {
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

        // Draw the background labels for the quick action buttons and augment
        this.layouts.forEach(layout -> {
            graphics.blitSprite(LABEL_BACKGROUND, layout.getX() - LABEL_PADDING, layout.getY() - LABEL_PADDING, LABEL_PADDING + layout.getWidth() + LABEL_PADDING, LABEL_PADDING + layout.getHeight() + LABEL_PADDING);
        });

        // Backpack Inventory
        int backpackHeight = BACKPACK_PADDING_TOP + (this.rows * 18) + BACKPACK_PADDING_BOTTOM;
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
        if(!this.hasPopupMenu() && Keys.KEY_BACKPACK.matches(key, scanCode))
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

    public void onAugmentBayUnlocked(Augments.Position position)
    {
        FrameworkButton button = this.augmentsButtons.get(position);
        if(button != null)
        {
            this.spawnSlotUnlockedParticles(button.getX(), button.getY());
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.PLAYER_LEVELUP, 1.3F, 0.25F));
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.CHAIN_BREAK, 1.3F, 0.7F));
        }
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button)
    {
        for(Layout layout : this.layouts)
        {
            if(ScreenUtil.isPointInArea((int) mouseX, (int) mouseY, layout.getX() - LABEL_PADDING, layout.getY() - LABEL_PADDING, LABEL_PADDING + layout.getWidth() + LABEL_PADDING, LABEL_PADDING + layout.getHeight() + LABEL_PADDING))
            {
                return false;
            }
        }

        int backpackX = this.leftPos;
        int backpackY = this.topPos + BACKPACK_TOP;
        int backpackWidth = this.imageWidth;
        int backpackHeight = BACKPACK_PADDING_TOP + (this.rows * 18) + BACKPACK_PADDING_BOTTOM;
        if(ScreenUtil.isPointInArea((int) mouseX, (int) mouseY, backpackX, backpackY, backpackWidth, backpackHeight))
        {
            return false;
        }

        int inventoryX = this.leftPos + (this.imageWidth - INVENTORY_WIDTH) / 2;
        int inventoryY = this.topPos + BACKPACK_TOP + backpackHeight + GAP;
        if(ScreenUtil.isPointInArea((int) mouseX, (int) mouseY, inventoryX, inventoryY, INVENTORY_WIDTH, INVENTORY_HEIGHT))
        {
            return false;
        }

        return true;
    }

    private static class AugmentIcon extends Icon
    {
        private final FrameworkButton button;
        private final BackpackContainerMenu menu;
        private final Augments.Position position;

        private AugmentIcon(FrameworkButton button, BackpackContainerMenu menu, Augments.Position position)
        {
            this.button = button;
            this.menu = menu;
            this.position = position;
        }

        @Override
        public int width()
        {
            return 12;
        }

        @Override
        public int height()
        {
            return 12;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, float partialTick)
        {
            if(!this.menu.getAugmentBayController().isSlotUnlocked(this.position.ordinal()))
            {
                if(this.button.isActive() && this.button.isHovered())
                {
                    graphics.blitSprite(ICON_LOCK, x, y, 12, 12);
                }
                else
                {
                    graphics.setColor(1, 1, 1, 0.5F);
                    graphics.blitSprite(ICON_LOCK, x, y, 12, 12);
                    graphics.setColor(1, 1, 1, 1);
                }
            }
            else
            {
                ResourceLocation sprite = this.menu.getAugments().getAugment(this.position).type().sprite();
                graphics.blitSprite(sprite, x, y, 12, 12);
            }
        }
    }
}
