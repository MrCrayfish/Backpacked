package com.mrcrayfish.backpacked.client.gui.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.framework.api.client.screen.widget.texture.FrameworkTexture;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;

public class MiniButton extends Button // TODO DONE
{
    private final FrameworkTexture icon;

    public MiniButton(int x, int y, FrameworkTexture icon, OnPress onPress)
    {
        super(x, y, 10, 10, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
        this.icon = icon;
    }

    public MiniButton(int x, int y, int width, int height, FrameworkTexture icon, OnPress onPress)
    {
        super(x, y, width, height, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
        this.icon = icon;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.icon.draw(graphics, this.getX(), this.getY(), this.width, this.height);
        if(this.isHovered)
        {
            graphics.fillGradient(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, -2130706433, -2130706433);
        }
    }
}
