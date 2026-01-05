package com.mrcrayfish.backpacked.client.gui.screen.widget;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.gui.StateSprites;
import com.mrcrayfish.backpacked.common.FilterableItems;
import com.mrcrayfish.backpacked.util.ScreenUtil;
import com.mrcrayfish.backpacked.util.Utils;
import com.mrcrayfish.framework.api.client.screen.widget.FrameworkSelectionList;
import com.mrcrayfish.framework.api.client.screen.widget.layout.Border;
import com.mrcrayfish.framework.api.client.screen.widget.layout.Padding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class ItemGrid<T extends FilterableItems<T>> extends FrameworkSelectionList
{
    private static final ResourceLocation LIST_BACKGROUND_SPRITE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/list/background");
    private static final StateSprites ITEM_SPRITES = new StateSprites(
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/list/item"),
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/list/item_hovered"),
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/list/item_selected"),
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/list/item_selected")
    );
    private static final ScrollerSprites SCROLLER_SPRITES = ScrollerSprites.of(
        Utils.rl("backpack/list/scroll_bar"),
        Utils.rl("backpack/list/scroll_bar"),
        Utils.rl("backpack/list/scroll_bar_hovered"),
        Utils.rl("backpack/list/scroll_bar_selected")
    );

    private final Supplier<T> supplier;
    private final Consumer<T> updater;
    private final int itemSize;
    private final int spacing;
    private final List<net.minecraft.world.item.Item> items;
    private String searchQuery;
    private boolean selectedOnly;
    private @Nullable ItemStack hoveredStack;

    private ItemGrid(Supplier<T> supplier, Consumer<T> updater, int width, int height, int itemSize, int spacing, String lastQuery, Predicate<net.minecraft.world.item.Item> predicate)
    {
        super(width, height, 0, 0, itemSize);
        this.supplier = supplier;
        this.updater = updater;
        this.itemSize = itemSize;
        this.spacing = spacing;
        this.items = BuiltInRegistries.ITEM.stream().filter(predicate).collect(ImmutableList.toImmutableList());
        this.setRenderHeader(false, 0);
        this.listBackground = LIST_BACKGROUND_SPRITE;
        this.scrollBarBackground = LIST_BACKGROUND_SPRITE;
        this.scrollBarBorder = Border.of(1);
        this.scrollBarPadding = Padding.of(spacing);
        this.scrollBarSpacing = spacing;
        this.scrollerSprites = SCROLLER_SPRITES;
        this.listBorder = Border.of(1);
        this.listPadding = Padding.of(spacing);
        this.itemSpacing = spacing;
        this.scrollerWidth = 10;
        this.scrollBarStyle = ScrollBarStyle.DETACHED;
        this.scrollBarAlwaysVisible = true;
        this.searchQuery = lastQuery;
        this.updateList();
    }

    private void updateList()
    {
        String search = this.searchQuery.toLowerCase().trim();
        boolean empty = search.trim().isBlank();
        this.clearEntries();

        // Gather the items that should be visible
        List<net.minecraft.world.item.Item> visibleItems = new ArrayList<>();
        this.items.forEach(item -> {
            if(item == Items.AIR)
                return;
            if(!this.selectedOnly || this.supplier.get().isFilteringItem(item)) {
                if(empty || item.getDescription().getString().toLowerCase(Locale.ROOT).contains(search)) {
                    visibleItems.add(item);
                }
            }
        });

        // Sorts all items based on name
        if(!empty)
        {
            visibleItems.sort(Comparator.<net.minecraft.world.item.Item>comparingInt(item -> {
                String name = item.getDescription().getString().toLowerCase(Locale.ROOT);
                if(name.equals(search)) {
                    return 0;
                } else if(name.startsWith(search)) {
                    return 1;
                }
                return 2;
            }).thenComparing(item -> item.getDescription().getString()));
        }
        else
        {
            visibleItems.sort(Comparator.comparing(item -> item.getDescription().getString()));
        }


        // Pull chunks of items from the list and distribute them into a row item
        int chunkSize = (this.getRowWidth() + this.spacing) / (this.itemSize + this.spacing);
        for(int i = 0; i < Mth.positiveCeilDiv(visibleItems.size(), chunkSize); i++)
        {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, visibleItems.size());
            this.addEntry(new Row<>(this, visibleItems.subList(start, end), this.supplier, this.updater));
        }
    }

    public void setSelectedOnly(boolean selectedOnly)
    {
        this.selectedOnly = selectedOnly;
        this.updateList();
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        this.hoveredStack = null;
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
        if(ScreenUtil.isPointInArea(mouseX, mouseY, this.getX(), this.getY() + this.listBorder.top(), this.getWidth(), this.getHeight() - this.listBorder.top() - this.listBorder.bottom()) && this.hoveredStack != null)
        {
            graphics.renderTooltip(Minecraft.getInstance().font, this.hoveredStack, mouseX, mouseY);
        }
    }

    @Override
    public void setSelected(@Nullable Item item) {}

    public void setSearchQuery(String searchQuery)
    {
        if(!this.searchQuery.equals(searchQuery))
        {
            this.setClampedScrollAmount(0);
            this.searchQuery = searchQuery;
            this.updateList();
        }
    }

    public void setActive(Supplier<Boolean> activeSupplier)
    {
        this.activeSupplier = activeSupplier;
    }

    protected static final class Row<R extends FilterableItems<R>> extends Item
    {
        private final ItemGrid<R> parent;
        private final List<ItemStack> display;
        private final Supplier<R> supplier;
        private final Consumer<R> updater;
        private int top, left;

        private Row(ItemGrid<R> parent, List<net.minecraft.world.item.Item> items, Supplier<R> augment, Consumer<R> updater)
        {
            this.parent = parent;
            this.display = items.stream().map(ItemStack::new).collect(ImmutableList.toImmutableList());
            this.supplier = augment;
            this.updater = updater;
        }

        @Override
        protected void renderContent(GuiGraphics graphics, int index, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, boolean selected, float partialTick)
        {
            // This will change in 1.21.8
            this.top = y;
            this.left = x;
            int itemSize = this.parent.itemSize;
            int spacing = this.parent.spacing;
            int halfSpacing = spacing / 2;
            boolean active = this.parent.isActive();
            for(int i = 0; i < this.display.size(); i++)
            {
                ItemStack stack = this.display.get(i);
                int offset = i * (itemSize + spacing);
                boolean itemSelected = this.supplier.get().isFilteringItem(stack.getItem());
                boolean itemHovered = active && ScreenUtil.isPointInArea(mouseX, mouseY, left + offset - halfSpacing, top - halfSpacing, itemSize + spacing, itemSize + spacing);
                RenderSystem.enableBlend();
                RenderSystem.enableDepthTest();
                graphics.setColor(1, 1, 1, active ? 1.0F : 0.5F);
                graphics.blitSprite(ITEM_SPRITES.get(itemSelected, itemHovered), left + offset, top, itemSize, itemSize);
                graphics.setColor(1, 1, 1, 1);
                RenderSystem.disableBlend();
                graphics.renderFakeItem(stack, left + offset + (itemSize - 16) / 2, top + (itemSize - 16) / 2);
                if(itemHovered)
                {
                    this.parent.hoveredStack = stack;
                }
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
                int itemSize = this.parent.itemSize;
                int spacing = this.parent.spacing;
                int halfSpacing = spacing / 2;
                R augment = this.supplier.get();
                for(int i = 0; i < this.display.size(); i++)
                {
                    int offset = i * (itemSize + spacing);
                    if(!ScreenUtil.isPointInArea((int) mouseX, (int) mouseY, this.left + offset - halfSpacing, this.top - halfSpacing, itemSize + spacing, itemSize + spacing))
                        continue;

                    ItemStack stack = this.display.get(i);
                    if(!augment.isFilteringItem(stack.getItem()))
                    {
                        if(!augment.isFilterFull())
                        {
                            this.updater.accept(augment.addItemFilter(stack.getItem()));
                            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                            return true;
                        }
                    }
                    else
                    {
                        this.updater.accept(augment.removeItemFilter(stack.getItem()));
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public static <T extends FilterableItems<T>> Builder<T> builder(Supplier<T> supplier, Consumer<T> updater)
    {
        return new Builder<>(supplier, updater);
    }

    public static class Builder<T extends FilterableItems<T>>
    {
        private final Supplier<T> supplier;
        private final Consumer<T> updater;
        private int width = 64;
        private int height = 64;
        private int itemSize = 18;
        private int spacing = 2;
        private String initialQuery = "";
        private Predicate<net.minecraft.world.item.Item> predicate = item -> true;

        private Builder(Supplier<T> supplier, Consumer<T> updater)
        {
            this.supplier = supplier;
            this.updater = updater;
        }

        public Builder<T> setWidth(int width)
        {
            this.width = width;
            return this;
        }

        public Builder<T> setHeight(int height)
        {
            this.height = height;
            return this;
        }

        public Builder<T> setItemSize(int itemSize)
        {
            this.itemSize = itemSize;
            return this;
        }

        public Builder<T> setSpacing(int spacing)
        {
            this.spacing = spacing;
            return this;
        }

        public Builder<T> setInitialQuery(String initialQuery)
        {
            this.initialQuery = initialQuery;
            return this;
        }

        public Builder<T> setPredicate(Predicate<net.minecraft.world.item.Item> predicate)
        {
            this.predicate = predicate;
            return this;
        }

        public ItemGrid<T> build()
        {
            return new ItemGrid<>(this.supplier, this.updater, this.width, this.height, this.itemSize, this.spacing, this.initialQuery, this.predicate);
        }
    }
}
