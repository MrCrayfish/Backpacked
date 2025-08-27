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
import net.minecraft.util.Mth;

import java.util.function.Consumer;

public class CustomButton extends AbstractButton
{
    private static final WidgetSprites SPRITES = new WidgetSprites(
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/button_enabled"),
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/button_disabled"),
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/button_enabled_focused")
    );
    private final Consumer<CustomButton> onPress;

    public CustomButton(int x, int y, int width, int height, Component text, Consumer<CustomButton> onPress)
    {
        super(x, y, width, height, text);
        this.onPress = onPress;
    }

    @Override
    public void onPress()
    {
        this.onPress.accept(this);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        graphics.setColor(1, 1, 1, this.alpha);
        graphics.blitSprite(SPRITES.get(this.active, this.isHovered()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
        graphics.setColor(1, 1, 1, 1);

        Font font = Minecraft.getInstance().font;
        Component message = this.getMessage();
        int textColour = this.active ? 0xFFFFFFFF : 0xFF8C7E6D;
        int textX = this.getX() + (this.getWidth() - font.width(message)) / 2;
        int textY = this.getY() + (this.getHeight() - font.lineHeight) / 2 + 1;
        graphics.drawString(font, message, textX, textY, textColour, this.active);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output)
    {
        this.defaultButtonNarrationText(output);
    }
}
