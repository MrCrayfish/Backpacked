package com.mrcrayfish.backpacked.client.gui.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.backpacked.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Author: MrCrayfish
 */
public class CheckBox extends Button
{
    private static final ResourceLocation GUI = new ResourceLocation(Reference.MOD_ID, "textures/gui/widgets.png");

    private boolean toggled = false;

    public CheckBox(int x, int y, ITextComponent title, Button.IPressable onPress)
    {
        this(x, y, title, onPress, NO_TOOLTIP);
    }

    public CheckBox(int x, int y, ITextComponent title, Button.IPressable onPress, Button.ITooltip tooltip)
    {
        super(x, y, 8, 8, title, onPress, tooltip);
    }

    public void setChecked(boolean toggled)
    {
        this.toggled = toggled;
    }

    public boolean isToggled()
    {
        return this.toggled;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bind(GUI);
        this.blit(matrixStack, this.x, this.y, 0, 0, 8, 8);
        if(this.toggled)
        {
            this.blit(matrixStack, this.x, this.y - 1, 8, 0, 9, 8);
        }
        minecraft.font.draw(matrixStack, this.getMessage().getString(), this.x + 12, this.y, 0xFFFFFF);
    }

    @Override
    public void onClick(double mouseX, double mouseY)
    {
        this.toggled = !this.toggled;
        this.onPress();
    }
}
