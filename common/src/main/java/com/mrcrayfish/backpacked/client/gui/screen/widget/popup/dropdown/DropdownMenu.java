package com.mrcrayfish.backpacked.client.gui.screen.widget.popup.dropdown;

import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.Alignment;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.client.gui.screen.layout.BorderedLinearLayout;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class DropdownMenu extends PopupMenu
{
    private final BorderedLinearLayout layout = (BorderedLinearLayout)
        BorderedLinearLayout.vertical().border(3).spacing(2);

    private DropdownMenu(PopupMenuHandler handler)
    {
        super(handler);
    }

    @Override
    protected Layout layout()
    {
        return this.layout;
    }

    @Override
    protected int border()
    {
        return this.layout.getBorder();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer)
    {
        this.layout.visitWidgets(consumer);
    }

    @Override
    protected void showChild(PopupMenu menu, ScreenRectangle rect)
    {
        super.showChild(menu, rect);
    }

    @Override
    protected void setParent(@Nullable PopupMenu parent)
    {
        super.setParent(parent);
    }

    protected void addItem(MenuItem item)
    {
        item.parent = this;
        item.visitChildMenus(this::adoptChild);
        this.layout.addChild(item);
        this.invalidateWidgets();
    }

    protected boolean hasChild()
    {
        return this.child != null;
    }

    protected boolean isChild(PopupMenu child)
    {
        return this.child == child;
    }

    public static Builder builder(PopupMenuHandler handler)
    {
        return new Builder(handler);
    }

    public static class Builder
    {
        private final PopupMenuHandler handler;
        private final List<MenuItem> items = new ArrayList<>();
        private int minItemWidth = 0;
        private int minItemHeight = 20;
        private @Nullable Alignment alignment;
        private @Nullable ResourceLocation background = Utils.rl("backpack/dropdown/background");
        private @Nullable Integer border;
        private @Nullable Integer spacing;

        private Builder(PopupMenuHandler handler)
        {
            this.handler = handler;
        }

        public Builder setMinItemSize(int width, int height)
        {
            this.minItemWidth = width;
            this.minItemHeight = height;
            return this;
        }

        public Builder setBackground(@Nullable ResourceLocation background)
        {
            this.background = background;
            return this;
        }

        public Builder setBorder(int border)
        {
            this.border = border;
            return this;
        }

        public Builder setSpacing(int spacing)
        {
            this.spacing = spacing;
            return this;
        }

        public Builder setAlignment(Alignment alignment)
        {
            this.alignment = alignment;
            return this;
        }

        public Builder addItem(MenuItem item)
        {
            this.items.add(item);
            return this;
        }

        public DropdownMenu build()
        {
            DropdownMenu menu = new DropdownMenu(this.handler);
            int maxWidth = this.items.stream().mapToInt(MenuItem::calculateWidth).max().orElse(100);
            this.items.forEach(item -> {
                item.setSize(Math.max(maxWidth, this.minItemWidth), this.minItemHeight);
                menu.addItem(item);
            });
            if(this.alignment != null)
            {
                menu.setAlignment(this.alignment);
            }
            menu.setBackground(this.background);
            if(this.border != null)
            {
                menu.layout.border(this.border);
            }
            if(this.spacing != null)
            {
                menu.layout.spacing(this.spacing);
            }
            return menu;
        }
    }
}
