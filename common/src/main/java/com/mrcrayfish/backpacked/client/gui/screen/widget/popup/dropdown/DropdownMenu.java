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
    private final List<AbstractWidget> items = new ArrayList<>();

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

    public void addItem(MenuItem item)
    {
        item.setParent(this);
        this.layout.addChild(item);
        this.items.add(item);
    }

    @Override
    protected boolean onClick(int mouseX, int mouseY, int button)
    {
        for(AbstractWidget widget : this.items)
        {
            if(widget.mouseClicked(mouseX, mouseY, button))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output)
    {

    }

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

    protected void setParent(@Nullable PopupMenu parent)
    {
        this.parent = parent;
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
        private final DropdownMenu base;
        private final List<MenuItem> items = new ArrayList<>();
        private int minItemWidth = 0;
        private int minItemHeight = 20;
        private @Nullable ResourceLocation background = Utils.rl("backpack/dropdown/background");
        private @Nullable Integer padding;
        private @Nullable Integer spacing;

        private Builder(PopupMenuHandler handler)
        {
            this.base = new DropdownMenu(handler);
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

        public Builder setPadding(int padding)
        {
            this.padding = padding;
            return this;
        }

        public Builder setSpacing(int spacing)
        {
            this.spacing = spacing;
            return this;
        }

        public Builder setAlignment(Alignment alignment)
        {
            this.base.setAlignment(alignment);
            return this;
        }

        public Builder addItem(MenuItem item)
        {
            this.items.add(item);
            return this;
        }

        public DropdownMenu build()
        {
            this.base.items.clear();
            int maxWidth = this.items.stream().mapToInt(MenuItem::calculateWidth).max().orElse(100);
            this.items.forEach(item -> {
                item.setSize(Math.max(maxWidth, this.minItemWidth), this.minItemHeight);
                this.base.addItem(item);
            });
            this.base.setBackground(this.background);
            if(this.padding != null)
            {
                this.base.layout.border(this.padding);
            }
            return this.base;
        }
    }
}
