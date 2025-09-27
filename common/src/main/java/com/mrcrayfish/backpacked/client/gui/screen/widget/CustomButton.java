package com.mrcrayfish.backpacked.client.gui.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.backpacked.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class CustomButton extends AbstractButton
{
    private static final WidgetSprites DEFAULT_SPRITES = new WidgetSprites(
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/button_enabled"),
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/button_disabled"),
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/button_enabled_focused")
    );

    private final @Nullable Icon icon;
    private final int gap;
    private final Consumer<CustomButton> action;
    private final WidgetSprites texture;
    private @Nullable Boolean state;

    private CustomButton(int x, int y, int width, int height, Component text, @Nullable Icon icon, int gap, Consumer<CustomButton> action, WidgetSprites texture, @Nullable Boolean state)
    {
        super(x, y, width, height, text);
        this.icon = icon;
        this.gap = gap;
        this.action = action;
        this.texture = texture;
        this.state = state;
    }

    @Override
    public void onPress()
    {
        if(this.state != null)
        {
            this.state = !this.state;
        }
        this.action.accept(this);
    }

    public boolean isToggled()
    {
        return this.state != null ? this.state : true;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        float alpha = this.state != null ? (this.active ? 1.0F : 0.5F) : this.alpha;
        graphics.setColor(1, 1, 1, alpha);
        boolean state = this.state != null ? this.state : this.active;
        graphics.blitSprite(this.texture.get(state, this.isHovered() && this.active), this.getX(), this.getY(), this.getWidth(), this.getHeight());
        graphics.setColor(1, 1, 1, 1);
        RenderSystem.disableBlend();

        Component message = this.getMessage();
        Font font = Minecraft.getInstance().font;
        int contentWidth = font.width(message);
        int contentHeight = contentWidth > 0 ? font.lineHeight : 0;
        if(this.icon != null)
        {
            // Only add gap if the message is not empty
            if(contentWidth > 0)
            {
                contentWidth += this.gap;
            }
            contentWidth += this.icon.width();
            contentHeight = Math.max(contentHeight, this.icon.height());
        }
        int contentLeft = this.getX() + (this.getWidth() - contentWidth) / 2;
        int contentTop = this.getY() + (this.getHeight() - contentHeight) / 2;

        int textX = contentLeft + (this.icon != null ? this.gap + this.icon.width() : 0);
        int textY = contentTop + (contentHeight - font.lineHeight) / 2 + 1;
        int textColour = this.active ? 0xFFFFFFFF : 0xFF8C7E6D;
        graphics.drawString(font, message, textX, textY, textColour, this.active);

        if(this.icon != null)
        {
            int iconX = contentLeft;
            int iconY = contentTop + (contentHeight - this.icon.height()) / 2;
            RenderSystem.enableBlend();
            graphics.setColor(1, 1, 1, alpha);
            graphics.blitSprite(this.icon.sprite(), iconX, iconY, this.icon.width(), this.icon.height());
            graphics.setColor(1, 1, 1, 1);
            RenderSystem.disableBlend();
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output)
    {
        this.defaultButtonNarrationText(output);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static Builder toggle(boolean initialState)
    {
        return new Builder(initialState);
    }

    public static final class Builder
    {
        private int x;
        private int y;
        private int width = 20;
        private int height = 20;
        private Component message = CommonComponents.EMPTY;
        private @Nullable Icon icon;
        private int gap = 2;
        private Consumer<CustomButton> action = btn -> {};
        private WidgetSprites texture = DEFAULT_SPRITES;
        private @Nullable Boolean state;

        private Builder() {}

        private Builder(@Nullable Boolean state)
        {
            this.state = state;
        }

        public CustomButton build()
        {
            return new CustomButton(this.x, this.y, this.width, this.height, this.message, this.icon, this.gap, this.action, this.texture, this.state);
        }

        public Builder setPosition(int x, int y)
        {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder setSize(int width, int height)
        {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder setMessage(Component message)
        {
            this.message = message;
            return this;
        }

        public Builder setIcon(ResourceLocation sprite, int width, int height)
        {
            this.icon = new StaticIcon(sprite, width, height);
            return this;
        }

        public Builder setIcon(Supplier<ResourceLocation> supplier, int width, int height)
        {
            this.icon = new DynamicIcon(supplier, width, height);
            return this;
        }

        public Builder setGap(int gap)
        {
            this.gap = gap;
            return this;
        }

        public Builder setAction(Consumer<CustomButton> action)
        {
            this.action = action;
            return this;
        }

        public Builder setTexture(WidgetSprites texture)
        {
            this.texture = texture;
            return this;
        }
    }

    private interface Icon
    {
        ResourceLocation sprite();

        int width();

        int height();
    }

    private record StaticIcon(ResourceLocation sprite, int width, int height) implements Icon {}

    private record DynamicIcon(Supplier<ResourceLocation> supplier, int width, int height) implements Icon
    {
        @Override
        public ResourceLocation sprite()
        {
            return this.supplier.get();
        }
    }
}
