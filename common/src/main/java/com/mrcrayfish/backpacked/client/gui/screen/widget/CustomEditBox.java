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
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

// Wrapped in a container to make it easier to add an icon
public class CustomEditBox extends AbstractContainerWidget
{
    private static final int ICON_PADDING = 2;
    private static final int ICON_SIZE = 12;

    private final LinearLayout layout = LinearLayout.horizontal();

    private final @Nullable ImageWidget iconWidget;
    private final @Nullable WidgetSprites background;
    private final Impl editBox;
    private @Nullable Supplier<Boolean> activeSupplier;

    private CustomEditBox(int width, int height, @Nullable ResourceLocation icon, @Nullable WidgetSprites background)
    {
        super(0, 0, width, height, CommonComponents.EMPTY);
        this.background = background;
        this.iconWidget = icon != null ? this.layout.addChild(ImageWidget.sprite(ICON_SIZE, ICON_SIZE, icon), s -> s.padding(ICON_PADDING)) : null;
        this.editBox = this.layout.addChild(new Impl());
        this.setSize(width, height);
    }

    public CustomEditBox setActive(@Nullable Supplier<Boolean> activeSupplier)
    {
        this.activeSupplier = activeSupplier;
        return this;
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

    public EditBox getEditBox()
    {
        return this.editBox;
    }

    // TODO builder
    public static CustomEditBox create(int width, int height)
    {
        return new CustomEditBox(width, height, null, null);
    }

    // TODO builder
    public static CustomEditBox create(int width, int height, ResourceLocation icon, WidgetSprites background)
    {
        return new CustomEditBox(width, height, icon, background);
    }

    public static class Impl extends EditBox
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
