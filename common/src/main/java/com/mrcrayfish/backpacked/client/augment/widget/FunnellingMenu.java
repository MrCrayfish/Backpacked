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
import net.minecraft.client.gui.components.WidgetSprites;
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
    public FunnellingMenu(PopupMenuHandler handler, Supplier<FunnellingAugment> supplier, Consumer<FunnellingAugment> updater)
    {
        super(handler, menu -> {
            PaddedLinearLayout layout = (PaddedLinearLayout) PaddedLinearLayout.vertical().padding(6).spacing(2);
            TextWidget title = layout.addChild(new TextWidget(Component.literal("Filters"), Minecraft.getInstance().font).setColour(0xFF61503D));
            layout.addChild(Divider.horizontal(Math.max(160, 10 + title.getWidth() + 10)).colour(0xFFE0CDB7));
            FilterList list = new FilterList(supplier, updater);
            CustomEditBox searchField = CustomEditBox.create(160, 16, Utils.rl("backpack/editbox/search"), new WidgetSprites(
                Utils.rl("backpack/editbox/background"),
                Utils.rl("backpack/editbox/background_focused")
            ));
            searchField.getEditBox().setHint(Component.literal("Search..."));
            searchField.getEditBox().setResponder(list::filter);
            layout.addChild(searchField);
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

        private final Consumer<FunnellingAugment> updater;
        private FunnellingAugment augment;

        public FilterList(Supplier<FunnellingAugment> supplier, Consumer<FunnellingAugment> updater)
        {
            super(160, 104, 0, 0, 18);
            this.updater = updater;
            this.setRenderHeader(false, 0);
            this.setListBackground(LIST_BACKGROUND_SPRITE);
            this.setItemSprites(ITEM_SPRITES);
            this.setScrollBarSprites(SCROLL_BAR_SPRITES);
            this.setContentPadding(2);
            this.setItemSpacing(2);
            this.setScrollBarWidth(10);
            this.setScrollBarStyle(ScrollBarStyle.DETACHED);
            this.augment = supplier.get();
            BuiltInRegistries.ITEM.forEach(item -> {
                this.addEntry(new FilterItem(item, this.augment.isFilter(item)));
            });
            this.children().sort(Comparator.comparing(item -> item.label.getString()));
        }

        private void filter(String text)
        {
            text = text.toLowerCase();
            boolean empty = text.trim().isBlank();
            this.clearEntries();
            String filter = text;
            BuiltInRegistries.ITEM.forEach(item -> {
                if(empty || item.getDescription().getString().toLowerCase(Locale.ROOT).contains(filter)) {
                    this.addEntry(new FilterItem(item, this.augment.isFilter(item)));
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
                if(item.toggled)
                {
                    this.augment = this.augment.addFilter(item.id);
                }
                else
                {
                    this.augment = this.augment.removeFilter(item.id);
                }
                this.updater.accept(this.augment);
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
        }

        public static final class FilterItem extends ObjectSelectionList.Entry<FilterItem>
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
                if(font.width(rawName) > 100)
                {
                    rawName = font.plainSubstrByWidth(rawName, 100 - font.width("...")).trim() + "...";
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
