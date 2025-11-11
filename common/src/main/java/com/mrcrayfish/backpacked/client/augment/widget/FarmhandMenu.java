package com.mrcrayfish.backpacked.client.augment.widget;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.augment.AugmentSettingsMenu;
import com.mrcrayfish.backpacked.client.gui.StateSprites;
import com.mrcrayfish.backpacked.client.gui.screen.widget.*;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.augment.impl.FarmhandAugment;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BushBlock;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FarmhandMenu extends AugmentSettingsMenu
{
    private static final Component OPTIONS_LABEL = Component.translatable("backpacked.gui.options");
    private static final Component FILTERS_LABEL = Component.translatable("backpacked.gui.seeds");
    private static final Component SEARCH_HINT = Component.translatable("backpacked.gui.search_hint");
    private static final Component ACTIVE_LABEL = Component.translatable("backpacked.gui.active");
    private static final Component SHOW_ALL_LABEL = Component.translatable("backpacked.gui.show_all");
    private static final Component ACTIVATED_ONLY_LABEL = Component.translatable("backpacked.gui.activated_only");
    private static final Component PLANT_NEARBY_LABEL = Component.translatable("augment.backpacked.farmhand.plant_nearby");
    private static final Component PLANT_NEARBY_TOOLTIP = Component.translatable("augment.backpacked.farmhand.plant_nearby.tooltip");
    private static final Component REPLANT_HARVESTED_LABEL = Component.translatable("augment.backpacked.farmhand.replant_harvested");
    private static final Component REPLANT_HARVESTED_TOOLTIP = Component.translatable("augment.backpacked.farmhand.replant_harvested.tooltip");

    private static final int MIN_CONTENT_WIDTH = 160;

    private static String lastQuery = "";
    private static boolean lastFilter = false;

    public FarmhandMenu(PopupMenuHandler handler, Supplier<FarmhandAugment> supplier, Consumer<FarmhandAugment> updater)
    {
        super(handler, menu -> {
            LinearLayout layout = LinearLayout.vertical().spacing(2);
            TitleWidget optionsTitle = layout.addChild(new TitleWidget(OPTIONS_LABEL, Minecraft.getInstance().font));

            Divider divider1 = layout.addChild(Divider.horizontal(Math.max(MIN_CONTENT_WIDTH, optionsTitle.getWidth())).colour(0xFFE0CDB7));
            optionsTitle.setWidth(divider1.getWidth());

            layout.addChild(createOption(PLANT_NEARBY_LABEL, PLANT_NEARBY_TOOLTIP, CustomButton.state(() -> {
                    return supplier.get().plantNearby();
                }, newValue -> {
                    updater.accept(supplier.get().setPlantNearby(newValue));
                })
                .setMessage(() -> CommonComponents.optionStatus(supplier.get().plantNearby()))
                .setSize(60, 18).build(), divider1.getWidth()));

            layout.addChild(createOption(REPLANT_HARVESTED_LABEL, REPLANT_HARVESTED_TOOLTIP, CustomButton.state(() -> {
                    return supplier.get().replantHarvested();
                }, newValue -> {
                    updater.accept(supplier.get().setReplantHarvested(newValue));
                })
                .setMessage(() -> CommonComponents.optionStatus(supplier.get().replantHarvested()))
                .setSize(60, 18).build(), divider1.getWidth()));

            layout.addChild(Divider.horizontal(MIN_CONTENT_WIDTH).colour(0xFFE0CDB7));
            layout.addChild(new TitleWidget(FILTERS_LABEL, Minecraft.getInstance().font)).setWidth(MIN_CONTENT_WIDTH);
            layout.addChild(Divider.horizontal(MIN_CONTENT_WIDTH).colour(0xFFE0CDB7));

            int filterButtonWidth = 55;
            FilterList list = new FilterList(supplier, updater, divider1.getWidth(), lastQuery, lastFilter);
            LinearLayout header = LinearLayout.horizontal().spacing(3);
            CustomEditBox searchField = CustomEditBox.create(divider1.getWidth() - 3 - filterButtonWidth, 16, Utils.rl("backpack/editbox/search"), new WidgetSprites(
                Utils.rl("backpack/editbox/background"),
                Utils.rl("backpack/editbox/background_disabled"),
                Utils.rl("backpack/editbox/background_focused")
            ));
            searchField.getEditBox().setValue(lastQuery);
            searchField.getEditBox().setHint(SEARCH_HINT);
            searchField.getEditBox().setResponder(list::setSearchQuery);
            header.addChild(searchField, LayoutSettings::alignVerticallyMiddle);
            header.addChild(CustomButton.state(list::isActivatedOnly, list::setActivatedOnly)
                .setSize(filterButtonWidth, 18)
                .setGap(4)
                .setMessage(ACTIVE_LABEL)
                .setContentRenderer(CustomButton.ToggleContentRenderer.INSTANCE)
                .setTooltip(btn -> Tooltip.create(list.isActivatedOnly() ? ACTIVATED_ONLY_LABEL : SHOW_ALL_LABEL))
                .setTexture(new WidgetSprites(
                    ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/button_enabled"),
                    ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/button_enabled_focused")
                )).build());
            layout.addChild(header);
            layout.addChild(list);
            return layout;
        });
    }

    private static final class FilterList extends CustomSelectionList<FilterList.FilterItem>
    {
        private static final ResourceLocation LIST_BACKGROUND_SPRITE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/list/background");
        private static final StateSprites ITEM_SPRITES = new StateSprites(
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/list/item"),
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/list/item_hovered"),
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/list/item_selected")
        );
        private static final StateSprites SCROLL_BAR_SPRITES = new StateSprites(
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/list/scroll_bar"),
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/list/scroll_bar_hovered"),
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/list/scroll_bar_selected")
        );

        private final Supplier<FarmhandAugment> supplier;
        private final Consumer<FarmhandAugment> updater;
        private String searchQuery = "";
        private boolean activatedOnly = false;

        public FilterList(Supplier<FarmhandAugment> supplier, Consumer<FarmhandAugment> updater, int width, String lastQuery, boolean lastFilter)
        {
            super(width, 64, 0, 0, 18);
            this.supplier = supplier;
            this.updater = updater;
            this.setRenderHeader(false, 0);
            this.setListBackground(LIST_BACKGROUND_SPRITE);
            this.setItemSprites(ITEM_SPRITES);
            this.setScrollBarSprites(SCROLL_BAR_SPRITES);
            this.setContentPadding(2);
            this.setItemSpacing(2);
            this.setScrollBarWidth(10);
            this.setScrollBarStyle(ScrollBarStyle.DETACHED);
            this.searchQuery = lastQuery;
            this.activatedOnly = lastFilter;
            this.updateList();
        }

        private void updateList()
        {
            String search = this.searchQuery.toLowerCase();
            boolean empty = search.trim().isBlank();
            this.clearEntries();
            BuiltInRegistries.ITEM.forEach(item -> {
                if(FarmhandAugment.ITEM_PLACES_AGEABLE_CROP.test(item)) {
                    if(empty || item.getDescription().getString().toLowerCase(Locale.ROOT).contains(search)) {
                        if(!this.activatedOnly || this.supplier.get().isFilter(item)) {
                            this.addEntry(new FilterItem(item, this.supplier));
                        }
                    }
                }
            });
            this.children().sort(Comparator.comparing(item -> item.label.getString()));
        }

        @Override
        public void setSelected(@Nullable FilterItem item)
        {
            if(item != null)
            {
                FarmhandAugment augment = this.supplier.get();
                if(!item.isToggled())
                {
                    if(!augment.isFilterLimit())
                    {
                        this.updater.accept(augment.addFilter(item.id));
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    }
                }
                else
                {
                    this.updater.accept(augment.removeFilter(item.id));
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                }
            }
        }

        private void setSearchQuery(String searchQuery)
        {
            lastQuery = searchQuery;
            this.searchQuery = searchQuery;
            this.updateList();
        }

        private void setActivatedOnly(boolean activatedOnly)
        {
            lastFilter = activatedOnly;
            this.activatedOnly = activatedOnly;
            this.updateList();
            this.setScrollAmount(0);
        }

        public boolean isActivatedOnly()
        {
            return this.activatedOnly;
        }

        public final class FilterItem extends ObjectSelectionList.Entry<FilterItem>
        {
            private static final ResourceLocation TOGGLE_OFF = Utils.rl("backpack/toggle_off");
            private static final ResourceLocation TOGGLE_ON = Utils.rl("backpack/toggle_on");

            private final ResourceLocation id;
            private final ItemStack display;
            private final Component label;
            private final Supplier<FarmhandAugment> augment;

            public FilterItem(Item item, Supplier<FarmhandAugment> augment)
            {
                this.id = BuiltInRegistries.ITEM.getKey(item);
                this.display = new ItemStack(item);
                this.label = this.trimName(this.display.getItem().getDescription());
                this.augment = augment;
            }

            private Component trimName(Component name)
            {
                Font font = Minecraft.getInstance().font;
                String rawName = name.getString();
                int maxWidth = FilterList.this.getRowWidth() - 20 - 15;
                if(font.width(rawName) > maxWidth)
                {
                    rawName = font.plainSubstrByWidth(rawName, maxWidth - font.width("...")).trim() + "...";
                }
                return Component.literal(rawName);
            }

            @Override
            public void render(GuiGraphics graphics, int index, int top, int left, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered, float partialTicks)
            {
                boolean showToggle = this.isToggled() || !this.augment.get().isFilterLimit();
                int labelColour = showToggle ? 0xFFFFFFFF : 0x88FFFFFF;
                graphics.drawString(Minecraft.getInstance().font, this.label, left + 20, top + 5, labelColour);
                graphics.renderFakeItem(this.display, left + 2, top + 1);
                if(showToggle)
                {
                    graphics.blitSprite(this.isToggled() ? TOGGLE_ON : TOGGLE_OFF, left + rowWidth - 6 - 6, top + 6, 6, 6);
                }
            }

            @Override
            public Component getNarration()
            {
                return this.display.getDisplayName();
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button)
            {
                if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
                {
                    return this.isToggled() || !this.augment.get().isFilterLimit();
                }
                return false;
            }

            public boolean isToggled()
            {
                return this.augment.get().isFilter(this.display.getItem());
            }
        }
    }
}
