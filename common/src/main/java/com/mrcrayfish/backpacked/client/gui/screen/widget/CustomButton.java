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

    private CustomButton(int x, int y, int width, int height, Component text, @Nullable Icon icon, int gap, Consumer<CustomButton> action, WidgetSprites texture)
    {
        super(x, y, width, height, text);
        this.icon = icon;
        this.gap = gap;
        this.action = action;
        this.texture = texture;
    }

    @Override
    public void onPress()
    {
        this.action.accept(this);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        graphics.setColor(1, 1, 1, this.alpha);
        graphics.blitSprite(this.texture.get(this.active, this.isHovered()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
        graphics.setColor(1, 1, 1, 1);
        RenderSystem.disableBlend();

        Component message = this.getMessage();
        Font font = Minecraft.getInstance().font;
        int contentWidth = font.width(message);
        int contentHeight = font.lineHeight;
        if(this.icon != null)
        {
            contentWidth += this.gap + this.icon.width;
            contentHeight = Math.max(contentHeight, this.icon.height);
        }
        int contentLeft = this.getX() + (this.getWidth() - contentWidth) / 2;
        int contentTop = this.getY() + (this.getHeight() - contentHeight) / 2;

        int textX = contentLeft + (this.icon != null ? this.gap + this.icon.width : 0);
        int textY = contentTop + (contentHeight - font.lineHeight) / 2 + 1;
        int textColour = this.active ? 0xFFFFFFFF : 0xFF8C7E6D;
        graphics.drawString(font, message, textX, textY, textColour, this.active);

        if(this.icon != null)
        {
            int iconX = contentLeft;
            int iconY = contentTop + (contentHeight - this.icon.height) / 2;
            RenderSystem.enableBlend();
            graphics.setColor(1, 1, 1, this.active ? 1 : 0.25F);
            graphics.blitSprite(this.icon.sprite, iconX, iconY, this.icon.width, this.icon.height);
            graphics.setColor(1, 1, 1, 1);
            RenderSystem.disableBlend();
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output)
    {
        this.defaultButtonNarrationText(output);
    }

    public record Icon(ResourceLocation sprite, int width, int height) {}

    public static Builder builder()
    {
        return new Builder();
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

        public CustomButton build()
        {
            return new CustomButton(this.x, this.y, this.width, this.height, this.message, this.icon, this.gap, this.action, this.texture);
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
            this.icon = new Icon(sprite, width, height);
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
}
