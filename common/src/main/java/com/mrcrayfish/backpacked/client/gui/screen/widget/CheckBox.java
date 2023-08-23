package com.mrcrayfish.backpacked.client.gui.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class CheckBox extends Button
{
    private static final ResourceLocation GUI = new ResourceLocation(Constants.MOD_ID, "textures/gui/widgets.png");

    private boolean toggled = false;

    public CheckBox(int x, int y, Component title, OnPress onPress)
    {
        super(x, y, 8, 8, title, onPress, DEFAULT_NARRATION);
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
    public void renderWidget(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);
        blit(matrixStack, this.getX(), this.getY(), 0, 0, 8, 8);
        if(this.toggled)
        {
            blit(matrixStack, this.getX(), this.getY() - 1, 8, 0, 9, 8);
        }
        Minecraft.getInstance().font.draw(matrixStack, this.getMessage().getString(), this.getX() + 12, this.getY(), 0xFFFFFF);
    }

    @Override
    public void onClick(double mouseX, double mouseY)
    {
        this.toggled = !this.toggled;
        this.onPress();
    }
}
