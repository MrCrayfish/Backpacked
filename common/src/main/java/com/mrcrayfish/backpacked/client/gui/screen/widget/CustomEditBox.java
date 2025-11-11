package com.mrcrayfish.backpacked.client.gui.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.backpacked.util.ScreenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
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

    private final Impl editBox;
    private @Nullable Supplier<Boolean> activeSupplier;

    private CustomEditBox(int width, int height, @Nullable ResourceLocation icon, @Nullable WidgetSprites background)
    {
        super(0, 0, width, height, CommonComponents.EMPTY);
        this.editBox = new Impl(this, icon, background);
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
        int editBoxX = x;
        if(this.editBox.icon != null)
            editBoxX += this.getIconOffset();
        this.editBox.setX(editBoxX);
    }

    @Override
    public void setY(int y)
    {
        super.setY(y);
        this.editBox.setY(y);
    }

    @Override
    public void setWidth(int width)
    {
        super.setWidth(width);
        int editBoxWidth = width;
        if(this.editBox.icon != null)
            editBoxWidth -= this.getIconOffset();
        this.editBox.setWidth(editBoxWidth);
    }

    @Override
    public void setHeight(int height)
    {
        super.setHeight(height);
        this.editBox.setHeight(height);
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
            this.editBox.active = this.activeSupplier.get();
        }
        this.editBox.render(graphics, mouseX, mouseY, partialTick);
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

    private int getIconOffset()
    {
        return ICON_PADDING + ICON_SIZE + ICON_PADDING - 3;
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
        private final CustomEditBox parent;
        private final @Nullable ResourceLocation icon;
        private final @Nullable WidgetSprites background;

        private Impl(CustomEditBox parent, @Nullable ResourceLocation icon, @Nullable WidgetSprites background)
        {
            super(Minecraft.getInstance().font, 0, 0, CommonComponents.EMPTY);
            this.parent = parent;
            this.icon = icon;
            this.background = background;
        }

        @Override
        public boolean isBordered()
        {
            return super.isBordered() && this.background == null;
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
        {
            if(this.isVisible())
            {
                if(this.background != null)
                {
                    RenderSystem.enableBlend();
                    RenderSystem.enableDepthTest();
                    graphics.setColor(1, 1, 1, this.isActive() ? 1.0F : 0.5F);
                    ResourceLocation background = this.background.get(this.isActive(), this.isFocused());
                    int iconOffset = this.icon != null ? this.parent.getIconOffset() : 0;
                    graphics.blitSprite(background, this.getX() - iconOffset, this.getY(), this.getWidth() + iconOffset, this.getHeight());
                    graphics.setColor(1, 1, 1, 1);
                    RenderSystem.disableBlend();
                }
                if(this.icon != null)
                {
                    int iconY = this.getY() + (this.getHeight() - ICON_SIZE) / 2;
                    graphics.blitSprite(this.icon, this.getX() - this.parent.getIconOffset() + ICON_PADDING, iconY, ICON_SIZE, ICON_SIZE);
                }
                super.renderWidget(graphics, mouseX, mouseY, partialTick);
            }
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

        @Override
        protected boolean clicked(double mouseX, double mouseY)
        {
            if(!this.active || !this.visible)
                return false;
            int editBoxX = this.getX();
            int editBoxWidth = this.getWidth();
            // Allow the area of the icon to be considered valid
            if(this.icon != null)
            {
                editBoxX -= this.parent.getIconOffset();
                editBoxWidth += this.parent.getIconOffset();
            }
            return ScreenUtil.isPointInArea((int) mouseX, (int) mouseY, editBoxX, this.getY(), editBoxWidth, this.getHeight());
        }
    }
}
