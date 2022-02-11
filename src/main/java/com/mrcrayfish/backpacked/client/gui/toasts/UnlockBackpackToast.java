package com.mrcrayfish.backpacked.client.gui.toasts;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.backpacked.client.gui.screen.CustomiseBackpackScreen;
import com.mrcrayfish.backpacked.client.model.BackpackModel;
import com.mrcrayfish.backpacked.common.Backpack;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Author: MrCrayfish
 */
public class UnlockBackpackToast implements IToast
{
    private static final ITextComponent TITLE = new TranslationTextComponent("backpacked.toast.unlocked_backpack").withStyle(TextFormatting.YELLOW);

    private final ITextComponent name;
    private final BackpackModel model;

    public UnlockBackpackToast(Backpack backpack)
    {
        this.name = new TranslationTextComponent(backpack.getId().getNamespace() + ".backpack." + backpack.getId().getPath());
        this.model = backpack.getModelSupplier().get();
    }

    @Override
    public Visibility render(MatrixStack matrixStack, ToastGui gui, long delta)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        gui.getMinecraft().getTextureManager().bind(TEXTURE);
        gui.blit(matrixStack, 0, 0, 0, 0, 160, 32);
        gui.getMinecraft().font.draw(matrixStack, TITLE, 35, 7, 0xFFFFFF);
        gui.getMinecraft().font.draw(matrixStack, this.name, 35.0F, 18.0F, 0xFFFFFF);
        CustomiseBackpackScreen.drawBackpackModel(matrixStack, this.model, 15, 7, 30F, 0, 0F);
        return delta >= 5000L ? Visibility.HIDE : Visibility.SHOW;
    }
}
