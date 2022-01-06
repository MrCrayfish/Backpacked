package com.mrcrayfish.backpacked.client.gui.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class CheckBox extends Button
{
    private static final ResourceLocation GUI = new ResourceLocation(Reference.MOD_ID, "textures/gui/widgets.png");

    private boolean toggled = false;

    public CheckBox(int x, int y, Component title, Button.OnPress onPress)
    {
        this(x, y, title, onPress, NO_TOOLTIP);
    }

    public CheckBox(int x, int y, Component title, Button.OnPress onPress, Button.OnTooltip tooltip)
    {
        super(x, y, 8, 8, title, onPress, tooltip);
    }

    public void setChecked(boolean toggled)
    {
        this.toggled = toggled;
    }

    public boolean isChecked()
    {
        return this.toggled;
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);
        this.blit(matrixStack, this.x, this.y, 0, 0, 8, 8);
        if(this.toggled)
        {
            this.blit(matrixStack, this.x, this.y - 1, 8, 0, 9, 8);
        }
        Minecraft.getInstance().font.draw(matrixStack, this.getMessage().getString(), this.x + 12, this.y, 0xFFFFFF);
    }

    @Override
    public void onClick(double mouseX, double mouseY)
    {
        this.toggled = !this.toggled;
        this.onPress();
    }
}
