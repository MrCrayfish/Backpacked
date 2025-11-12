package com.mrcrayfish.backpacked.client.augment.widget;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.augment.AugmentSettingsMenu;
import com.mrcrayfish.backpacked.client.gui.StateSprites;
import com.mrcrayfish.backpacked.client.gui.screen.widget.*;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.augment.impl.SeedflowAugment;
import com.mrcrayfish.backpacked.common.augment.impl.SeedflowAugment;
import com.mrcrayfish.backpacked.util.ScreenUtil;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SeedflowMenu extends AugmentSettingsMenu
{
    private static final Component OPTIONS_LABEL = Component.translatable("backpacked.gui.options");
    private static final Component SEARCH_HINT = Component.translatable("backpacked.gui.search_hint");
    private static final Component PLANT_NEARBY_LABEL = Component.translatable("augment.backpacked.seedflow.plant_nearby");
    private static final Component PLANT_NEARBY_TOOLTIP = Component.translatable("augment.backpacked.seedflow.plant_nearby.tooltip");
    private static final Component USE_FILTERS_LABEL = Component.translatable("augment.backpacked.seedflow.use_filters");
    private static final Component USE_FILTERS_TOOLTIP = Component.translatable("augment.backpacked.seedflow.use_filters.tooltip");

    private static final int MIN_CONTENT_WIDTH = 162;

    private static String lastQuery = "";

    public SeedflowMenu(PopupMenuHandler handler, Supplier<SeedflowAugment> supplier, Consumer<SeedflowAugment> updater)
    {
        super(handler, menu -> {
            LinearLayout layout = LinearLayout.vertical().spacing(2);
            TitleWidget optionsTitle = layout.addChild(new TitleWidget(OPTIONS_LABEL, Minecraft.getInstance().font));
            Divider divider1 = layout.addChild(Divider.horizontal(Math.max(MIN_CONTENT_WIDTH, optionsTitle.getWidth())).colour(0xFFE0CDB7));
            optionsTitle.setWidth(divider1.getWidth());

            CustomButton autoPlantBtn = CustomButton.state(() -> {
                    return supplier.get().plantNearby();
                }, newValue -> {
                    updater.accept(supplier.get().setPlantNearby(newValue));
                })
                .setMessage(() -> CommonComponents.optionStatus(supplier.get().plantNearby()))
                .setSize(60, 18).build();
            layout.addChild(createOption(PLANT_NEARBY_LABEL, PLANT_NEARBY_TOOLTIP, autoPlantBtn, divider1.getWidth()));

            CustomButton randomizeBtn = CustomButton.state(() -> {
                    return supplier.get().randomizeSeeds();
                }, newValue -> {
                    updater.accept(supplier.get().setRandomizeSeeds(newValue));
                })
                .setMessage(() -> CommonComponents.optionStatus(supplier.get().randomizeSeeds()))
                .setSize(60, 18).setActive(() -> supplier.get().plantNearby()).build();
            layout.addChild(createOption(Component.literal("Randomize Seeds"), PLANT_NEARBY_TOOLTIP, randomizeBtn, divider1.getWidth()));

            CustomButton useFiltersBtn = CustomButton.state(() -> {
                    return supplier.get().useFilters();
                }, newValue -> {
                    updater.accept(supplier.get().setUseFilters(newValue));
                })
                .setMessage(() -> CommonComponents.optionStatus(supplier.get().useFilters()))
                .setSize(60, 18).setActive(() -> supplier.get().plantNearby()).build();
            layout.addChild(createOption(USE_FILTERS_LABEL, USE_FILTERS_TOOLTIP, useFiltersBtn, divider1.getWidth()));

            layout.addChild(Divider.horizontal(MIN_CONTENT_WIDTH).colour(0xFFE0CDB7));

            FilterList list = new FilterList(supplier, updater, divider1.getWidth(), lastQuery);
            list.setActive(() -> {
                SeedflowAugment augment = supplier.get();
                return augment.plantNearby() && augment.useFilters();
            });
            CustomEditBox searchField = layout.addChild(CustomEditBox.create(divider1.getWidth(), 16, Utils.rl("backpack/editbox/search"), new WidgetSprites(
                Utils.rl("backpack/editbox/background"),
                Utils.rl("backpack/editbox/background_disabled"),
                Utils.rl("backpack/editbox/background_focused")
            ))).setActive(() -> {
                SeedflowAugment augment = supplier.get();
                return augment.plantNearby() && augment.useFilters();
            });
            searchField.getEditBox().setValue(lastQuery);
            searchField.getEditBox().setHint(SEARCH_HINT);
            searchField.getEditBox().setResponder(list::setSearchQuery);

            layout.addChild(list);

            return layout;
        });
    }

    private static final class FilterList extends CustomSelectionList<FilterList.ItemsRow>
    {
        private static final ResourceLocation LIST_BACKGROUND_SPRITE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/list/background");
        private static final StateSprites ITEM_SPRITES = new StateSprites(
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/list/item"),
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/list/item_hovered"),
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/list/item_selected"),
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/list/item_selected")
        );
        private static final StateSprites SCROLL_BAR_SPRITES = new StateSprites(
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/list/scroll_bar"),
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/list/scroll_bar_hovered"),
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/list/scroll_bar_selected")
        );
        private static final int ITEM_SPACING = 2;

        private final Supplier<SeedflowAugment> supplier;
        private final Consumer<SeedflowAugment> updater;
        private final List<Item> items;
        private String searchQuery;

        public FilterList(Supplier<SeedflowAugment> supplier, Consumer<SeedflowAugment> updater, int width, String lastQuery)
        {
            super(width, 64, 0, 0, 18);
            this.supplier = supplier;
            this.updater = updater;
            this.items = BuiltInRegistries.ITEM.stream().filter(SeedflowAugment.ITEM_PLACES_AGEABLE_CROP).collect(ImmutableList.toImmutableList());
            this.setRenderHeader(false, 0);
            this.setListBackground(LIST_BACKGROUND_SPRITE);
            this.setScrollBarSprites(SCROLL_BAR_SPRITES);
            this.setContentPadding(2);
            this.setItemSpacing(ITEM_SPACING);
            this.setScrollBarWidth(10);
            this.setScrollBarStyle(ScrollBarStyle.DETACHED);
            this.setScrollBarAlwaysVisible(true);
            this.searchQuery = lastQuery;
            this.updateList();
        }

        private void updateList()
        {
            String search = this.searchQuery.toLowerCase();
            boolean empty = search.trim().isBlank();
            this.clearEntries();

            // Gather the items that should be visible
            List<Item> visibleItems = new ArrayList<>();
            this.items.forEach(item -> {
                if(empty || item.getDescription().getString().toLowerCase(Locale.ROOT).contains(search)) {
                    visibleItems.add(item);
                }
            });

            // Sorts all items based on name
            visibleItems.sort(Comparator.comparing(item -> item.getDescription().getString()));

            // Pull chunks of items from the list and distribute them into a row item
            int chunkSize = (this.getRowWidth() + ITEM_SPACING) / 20;
            for(int i = 0; i < Mth.positiveCeilDiv(visibleItems.size(), chunkSize); i++)
            {
                int start = i * chunkSize;
                int end = Math.min(start + chunkSize, visibleItems.size());
                this.addEntry(new ItemsRow(visibleItems.subList(start, end), this.supplier, this.updater));
            }
        }

        @Override
        public void setSelected(@Nullable SeedflowMenu.FilterList.ItemsRow item) {}

        private void setSearchQuery(String searchQuery)
        {
            lastQuery = searchQuery;
            this.searchQuery = searchQuery;
            this.updateList();
        }

        public final class ItemsRow extends ObjectSelectionList.Entry<ItemsRow>
        {
            private final List<ItemStack> display;
            private final Supplier<SeedflowAugment> supplier;
            private final Consumer<SeedflowAugment> updater;
            private int top, left;

            public ItemsRow(List<Item> items, Supplier<SeedflowAugment> augment, Consumer<SeedflowAugment> updater)
            {
                this.display = items.stream().map(ItemStack::new).collect(ImmutableList.toImmutableList());
                this.supplier = augment;
                this.updater = updater;
            }

            @Override
            public void render(GuiGraphics graphics, int index, int top, int left, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered, float partialTicks)
            {
                // This will change in 1.21.8
                this.top = top;
                this.left = left;
                boolean active = FilterList.super.isActive();
                for(int i = 0; i < this.display.size(); i++)
                {
                    ItemStack stack = this.display.get(i);
                    int offset = i * (18 + ITEM_SPACING);
                    boolean itemSelected = this.supplier.get().isFilter(stack.getItem());
                    boolean itemHovered = active && ScreenUtil.isPointInArea(mouseX, mouseY, left + offset, top, 18, 18);
                    RenderSystem.enableBlend();
                    RenderSystem.enableDepthTest();
                    graphics.setColor(1, 1, 1, active ? 1.0F : 0.5F);
                    graphics.blitSprite(ITEM_SPRITES.get(itemSelected, itemHovered), left + offset, top, 18, 18);
                    graphics.setColor(1, 1, 1, 1);
                    RenderSystem.disableBlend();
                    graphics.renderFakeItem(stack, left + offset + 1, top + 1);
                }
            }

            @Override
            public Component getNarration()
            {
                return CommonComponents.EMPTY;
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button)
            {
                if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
                {
                    SeedflowAugment augment = this.supplier.get();
                    for(int i = 0; i < this.display.size(); i++)
                    {
                        int offset = i * (18 + ITEM_SPACING);
                        if(!ScreenUtil.isPointInArea((int) mouseX, (int) mouseY, this.left + offset, this.top, 18, 18))
                            continue;

                        ItemStack stack = this.display.get(i);
                        if(!augment.isFilter(stack.getItem()))
                        {
                            if(!augment.isFilterLimit())
                            {
                                this.updater.accept(augment.addFilter(BuiltInRegistries.ITEM.getKey(stack.getItem())));
                                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                                return true;
                            }
                        }
                        else
                        {
                            this.updater.accept(augment.removeFilter(BuiltInRegistries.ITEM.getKey(stack.getItem())));
                            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                            return true;
                        }
                    }
                }
                return false;
            }
        }
    }
}
