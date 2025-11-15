package com.mrcrayfish.backpacked.client.gui.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

// Wrapped in a container to make it easier to add an icon
public class CustomEditBox extends AbstractContainerWidget
{
    private static final int ICON_PADDING = 2;
    private static final int ICON_SIZE = 12;

    private final LinearLayout layout = LinearLayout.horizontal();
    private final @Nullable ImageWidget iconWidget;
    private final @Nullable WidgetSprites background;
    private final @Nullable Supplier<Boolean> activeSupplier;
    private final EditBox editBox;

    private CustomEditBox(int x, int y, int width, int height, @Nullable ResourceLocation icon, @Nullable WidgetSprites background, String text, @Nullable String suggestion, @Nullable Component hint, @Nullable Consumer<String> callback, @Nullable Predicate<String> valueFilter, @Nullable BiFunction<String, Integer, FormattedCharSequence> styleFormatter, @Nullable Supplier<Boolean> activeSupplier, @Nullable Integer maxTextLength)
    {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.background = background;
        this.activeSupplier = activeSupplier;
        this.iconWidget = icon != null ? this.layout.addChild(ImageWidget.sprite(ICON_SIZE, ICON_SIZE, icon), s -> s.padding(ICON_PADDING)) : null;
        this.editBox = this.layout.addChild(new Impl());
        this.editBox.setSize(icon != null ? width - ICON_PADDING * 2 - ICON_SIZE : width, height);
        if(valueFilter != null)
            this.editBox.setFilter(valueFilter);
        if(maxTextLength != null)
            this.editBox.setMaxLength(maxTextLength);
        this.editBox.setValue(text);
        this.editBox.setSuggestion(suggestion);
        if(hint != null)
            this.editBox.setHint(hint);
        if(callback != null)
            this.editBox.setResponder(callback); // Add after setting initial text to avoid call
        if(styleFormatter != null)
            this.editBox.setFormatter(styleFormatter);
        this.setSize(width, height);
    }

    @Override
    public void setX(int x)
    {
        super.setX(x);
        this.layout.setX(x);
    }

    @Override
    public void setY(int y)
    {
        super.setY(y);
        this.layout.setY(y);
    }

    @Override
    public void setWidth(int width)
    {
        super.setWidth(width);
        this.editBox.setWidth(this.iconWidget != null ? width - ICON_PADDING * 2 - ICON_SIZE : width);
        this.layout.arrangeElements();
    }

    @Override
    public void setHeight(int height)
    {
        super.setHeight(height);
        this.editBox.setHeight(height);
        this.layout.arrangeElements();
    }

    @Override
    public void setSize(int width, int height)
    {
        this.setWidth(width);
        this.setHeight(height);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if(this.activeSupplier != null)
        {
            boolean active = this.activeSupplier.get();
            this.active = active;
            this.editBox.active = active;
        }
        if(this.background != null)
        {
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            graphics.setColor(1, 1, 1, this.editBox.isActive() ? 1.0F : 0.5F);
            ResourceLocation background = this.background.get(this.editBox.isActive(), this.editBox.isFocused());
            graphics.blitSprite(background, this.getX(), this.getY(), this.getWidth(), this.getHeight());
            graphics.setColor(1, 1, 1, 1);
            RenderSystem.disableBlend();
        }
        this.layout.visitWidgets(widget -> widget.render(graphics,  mouseX, mouseY, partialTick));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output)
    {
        this.editBox.updateNarration(output);
    }

    @Override
    public List<? extends GuiEventListener> children()
    {
        return List.of(this.editBox);
    }

    @Override
    public void setFocused(boolean focused)
    {
        super.setFocused(focused);
        this.editBox.setFocused(focused);
    }

    public String getText()
    {
        return this.editBox.getValue();
    }

    public EditBox getEditBox()
    {
        return this.editBox;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private int x;
        private int y;
        private int width = 100;
        private int height = 16;
        private @Nullable ResourceLocation icon;
        private @Nullable WidgetSprites background;
        private String text = "";
        private @Nullable String suggestion;
        private @Nullable Consumer<String> callback;
        private @Nullable Predicate<String> valueFilter;
        private @Nullable BiFunction<String, Integer, FormattedCharSequence> styleFormatter;
        private @Nullable Component hint;
        private @Nullable Supplier<Boolean> activeSupplier;
        private @Nullable Integer maxTextLength;

        public Builder setX(int x)
        {
            this.x = x;
            return this;
        }

        public Builder setY(int y)
        {
            this.y = y;
            return this;
        }

        public Builder setPos(int x, int y)
        {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder setWidth(int width)
        {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height)
        {
            this.height = height;
            return this;
        }

        public Builder setSize(int width, int height)
        {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder setIcon(@Nullable ResourceLocation icon)
        {
            this.icon = icon;
            return this;
        }

        public Builder setBackground(@Nullable WidgetSprites background)
        {
            this.background = background;
            return this;
        }

        public Builder setText(String text)
        {
            this.text = text;
            return this;
        }

        public Builder setSuggestion(@Nullable String suggestion)
        {
            this.suggestion = suggestion;
            return this;
        }

        public Builder setCallback(@Nullable Consumer<String> callback)
        {
            this.callback = callback;
            return this;
        }

        public Builder setValueFilter(@Nullable Predicate<String> valueFilter)
        {
            this.valueFilter = valueFilter;
            return this;
        }

        public Builder setStyleFormatter(@Nullable BiFunction<String, Integer, FormattedCharSequence> styleFormatter)
        {
            this.styleFormatter = styleFormatter;
            return this;
        }

        public Builder setHint(@Nullable Component hint)
        {
            this.hint = hint;
            return this;
        }

        public Builder setActive(Supplier<Boolean> active)
        {
            this.activeSupplier = active;
            return this;
        }

        public Builder setMaxTextLength(int maxTextLength)
        {
            this.maxTextLength = maxTextLength;
            return this;
        }

        public CustomEditBox build()
        {
            return new CustomEditBox(this.x, this.y, this.width, this.height, this.icon, this.background, this.text, this.suggestion, this.hint, this.callback, this.valueFilter, this.styleFormatter, this.activeSupplier, this.maxTextLength);
        }
    }

    private static class Impl extends EditBox
    {
        private Impl()
        {
            super(Minecraft.getInstance().font, 0, 0, CommonComponents.EMPTY);
        }

        @Override
        public boolean isBordered()
        {
            return false; // Hack to disable the default background
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            // Right-clicking will clear the edit box
            if(this.active && this.visible && button == 1 && this.clicked(mouseX, mouseY))
            {
                this.setValue("");
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }
}
