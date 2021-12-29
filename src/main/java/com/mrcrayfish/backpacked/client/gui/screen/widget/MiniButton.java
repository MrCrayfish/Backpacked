package com.mrcrayfish.backpacked.client.gui.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class MiniButton extends Button
{
    private final int u, v;
    private final ResourceLocation texture;

    public MiniButton(int x, int y, int u, int v, ResourceLocation texture, IPressable onPress)
    {
        this(x, y, u, v, texture, onPress, NO_TOOLTIP);
    }

    public MiniButton(int x, int y, int u, int v, ResourceLocation texture, IPressable onPress, Button.ITooltip onTooltip)
    {
        super(x, y, 10, 10, StringTextComponent.EMPTY, onPress, onTooltip);
        this.u = u;
        this.v = v;
        this.texture = texture;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTick)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        Minecraft.getInstance().getTextureManager().bind(this.texture);
        this.blit(matrixStack, this.x, this.y, this.u, this.v, this.width, this.height);
        if(this.isHovered)
        {
            this.fillGradient(matrixStack, this.x, this.y, this.x + 10, this.y + 10, -2130706433, -2130706433);
        }
    }
}
