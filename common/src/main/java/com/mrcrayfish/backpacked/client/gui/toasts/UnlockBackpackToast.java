package com.mrcrayfish.backpacked.client.gui.toasts;

import com.mrcrayfish.backpacked.client.backpack.ClientBackpack;
import com.mrcrayfish.backpacked.client.gui.screen.CustomiseBackpackScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class UnlockBackpackToast implements Toast
{
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/recipe");
    private static final Component TITLE = Component.translatable("backpacked.toast.unlocked_backpack").withStyle(ChatFormatting.GOLD);

    private final ClientBackpack backpack;
    private final Component name;

    public UnlockBackpackToast(ClientBackpack backpack)
    {
        this.backpack = backpack;
        this.name = Component.translatable(backpack.getTranslationKey()).withStyle(ChatFormatting.DARK_GRAY);
    }

    @Override
    public Visibility render(GuiGraphics graphics, ToastComponent gui, long delta)
    {
        graphics.blitSprite(BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
        graphics.drawString(gui.getMinecraft().font, TITLE, 35, 7, 0xFFFFFF, false);
        graphics.drawString(gui.getMinecraft().font, this.name, 35, 18, 0xFFFFFF, false);
        int tickCount = Math.toIntExact(delta / 50L);
        float partialTick = (float) (delta % 50L) / 50F;
        CustomiseBackpackScreen.drawBackpackInGui(Minecraft.getInstance(), graphics, this.backpack, 18, 16, partialTick, tickCount);
        return delta >= 5000L ? Visibility.HIDE : Visibility.SHOW;
    }
}
