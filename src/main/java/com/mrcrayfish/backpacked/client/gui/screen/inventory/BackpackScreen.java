package com.mrcrayfish.backpacked.client.gui.screen.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Author: MrCrayfish
 */
@OnlyIn(Dist.CLIENT)
public class BackpackScreen extends ContainerScreen<BackpackContainer>
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

    private final int rows;

    public BackpackScreen(BackpackContainer backpackContainer, PlayerInventory playerInventory, ITextComponent titleIn)
    {
        super(backpackContainer, playerInventory, titleIn);
        this.rows = backpackContainer.getRows();
        this.ySize = 114 + this.rows * 18;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack); //Draw background
        super.render(matrixStack, mouseX, mouseY, partialTicks); //Super
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY); //Render hovered tooltips
    }

    //Render
    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI_TEXTURE);
        this.blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.rows * 18 + 17);
        this.blit(matrixStack, this.guiLeft, this.guiTop + this.rows * 18 + 17, 0, 126, this.xSize, 96);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        this.font.func_243248_b(matrixStack, this.title, 8.0F, 6.0F, 0x404040);
        this.font.func_243248_b(matrixStack, this.playerInventory.getDisplayName(), 8.0F, (float) (this.ySize - 96 + 2), 0x404040);
    }
}
