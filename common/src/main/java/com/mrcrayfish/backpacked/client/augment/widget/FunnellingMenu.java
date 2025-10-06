package com.mrcrayfish.backpacked.client.augment.widget;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.augment.AugmentSettingsMenu;
import com.mrcrayfish.backpacked.client.gui.StateSprites;
import com.mrcrayfish.backpacked.client.gui.screen.layout.PaddedLinearLayout;
import com.mrcrayfish.backpacked.client.gui.screen.widget.*;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.augment.impl.FunnellingAugment;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FunnellingMenu extends AugmentSettingsMenu
{
    private static final Component SEARCH_HINT = Component.translatable("backpacked.gui.search_hint");
    private static final Component ACTIVE_LABEL = Component.translatable("backpacked.gui.active");

    private static String lastQuery = "";
    private static boolean lastFilter = false;

    public FunnellingMenu(PopupMenuHandler handler, Supplier<FunnellingAugment> supplier, Consumer<FunnellingAugment> updater)
    {
        super(handler, menu -> {
            PaddedLinearLayout layout = (PaddedLinearLayout) PaddedLinearLayout.vertical().padding(6).spacing(2);
            TextWidget title = layout.addChild(new TextWidget(Component.literal("Filters"), Minecraft.getInstance().font).setColour(0xFF61503D));
            Divider divider = layout.addChild(Divider.horizontal(Math.max(170, 10 + title.getWidth() + 10)).colour(0xFFE0CDB7));

            FilterList list = new FilterList(supplier, updater, divider.getWidth());
            list.setQuery(lastQuery);
            list.setFilterByToggled(lastFilter);
            list.updateList();

            int filterButtonWidth = 55;
            LinearLayout header = LinearLayout.horizontal().spacing(3);
            CustomEditBox searchField = CustomEditBox.create(divider.getWidth() - 3 - filterButtonWidth, 16, Utils.rl("backpack/editbox/search"), new WidgetSprites(
                Utils.rl("backpack/editbox/background"),
                Utils.rl("backpack/editbox/background_focused")
            ));
            searchField.getEditBox().setValue(lastQuery);
            searchField.getEditBox().setHint(SEARCH_HINT);
            searchField.getEditBox().setResponder(s -> {
                lastQuery = s;
                list.setQuery(s);
            });
            header.addChild(searchField, LayoutSettings::alignVerticallyMiddle);
            header.addChild(CustomButton.toggle(lastFilter).setSize(filterButtonWidth, 18)
                .setMessage(ACTIVE_LABEL)
                .setGap(4)
                .setAction(btn -> {
                    lastFilter = btn.isToggled();
                    list.setFilterByToggled(btn.isToggled());
                }).setIcon(btn -> {
                    return btn.isToggled() ? FilterList.FilterItem.TOGGLE_ON : FilterList.FilterItem.TOGGLE_OFF;
                }, 6, 6).setTexture(new WidgetSprites(
                    ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/button_enabled"),
                    ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/button_enabled_focused")
                )).build());
            layout.addChild(header);
            layout.addChild(list);
            layout.addChild(CustomButton.values(supplier.get().mode(), mode -> {
                updater.accept(supplier.get().setMode(mode));
            }).setSize(divider.getWidth(), 18).build());
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

        private final Supplier<FunnellingAugment> supplier;
        private final Consumer<FunnellingAugment> updater;
        private String query = "";
        private boolean filterByToggled = false;

        public FilterList(Supplier<FunnellingAugment> supplier, Consumer<FunnellingAugment> updater, int width)
        {
            super(width, 104, 0, 0, 18);
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
            FunnellingAugment augment = supplier.get();
            BuiltInRegistries.ITEM.forEach(item -> {
                this.addEntry(new FilterItem(item, augment.isFilter(item)));
            });
            this.children().sort(Comparator.comparing(item -> item.label.getString()));
        }

        private void updateList()
        {
            String search = this.query.toLowerCase();
            boolean empty = search.trim().isBlank();
            this.clearEntries();
            FunnellingAugment augment = this.supplier.get();
            BuiltInRegistries.ITEM.forEach(item -> {
                if(empty || item.getDescription().getString().toLowerCase(Locale.ROOT).contains(search)) {
                    boolean toggled = augment.isFilter(item);
                    if(!this.filterByToggled || toggled) {
                        this.addEntry(new FilterItem(item, toggled));
                    }
                }
            });
            this.children().sort(Comparator.comparing(item -> item.label.getString()));
        }

        @Override
        public void setSelected(@Nullable FunnellingMenu.FilterList.FilterItem item)
        {
            if(item != null)
            {
                item.toggled = !item.toggled;
                FunnellingAugment augment = this.supplier.get();
                if(item.toggled)
                {
                    this.updater.accept(augment.addFilter(item.id));
                }
                else
                {
                    this.updater.accept(augment.removeFilter(item.id));
                }
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
        }

        private void setQuery(String query)
        {
            this.query = query;
            this.updateList();
        }

        private void setFilterByToggled(boolean toggled)
        {
            this.filterByToggled = toggled;
            this.updateList();
        }

        public final class FilterItem extends ObjectSelectionList.Entry<FilterItem>
        {
            private static final ResourceLocation TOGGLE_OFF = Utils.rl("backpack/toggle_off");
            private static final ResourceLocation TOGGLE_ON = Utils.rl("backpack/toggle_on");

            private final ResourceLocation id;
            private final ItemStack display;
            private final Component label;
            private boolean toggled;

            public FilterItem(Item item, boolean toggled)
            {
                this.id = BuiltInRegistries.ITEM.getKey(item);
                this.display = new ItemStack(item);
                this.label = this.trimName(this.display.getItem().getDescription());
                this.toggled = toggled;
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
                graphics.drawString(Minecraft.getInstance().font, this.label, left + 20, top + 5, 0xFFFFFFFF);
                graphics.renderFakeItem(this.display, left + 2, top + 1);
                graphics.blitSprite(this.toggled ? TOGGLE_ON : TOGGLE_OFF, left + rowWidth - 6 - 6, top + 6, 6, 6);
            }

            @Override
            public Component getNarration()
            {
                return Component.literal(this.id.toString());
            }
        }
    }
}
