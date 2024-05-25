package com.mrcrayfish.backpacked.client.gui.toasts;

import com.mrcrayfish.backpacked.client.gui.screen.CustomiseBackpackScreen;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.core.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class UnlockBackpackToast implements Toast
{
    private static final ResourceLocation BACKGROUND_SPRITE = new ResourceLocation("toast/recipe");
    private static final Component TITLE = Component.translatable("backpacked.toast.unlocked_backpack").withStyle(ChatFormatting.YELLOW);

    private final Backpack backpack;
    private final Component name;

    public UnlockBackpackToast(Backpack backpack)
    {
        this.backpack = backpack;
        this.name = Component.translatable(backpack.getTranslationKey()).withStyle(ChatFormatting.DARK_GRAY);
    }

    @Override
    public Visibility render(GuiGraphics graphics, ToastComponent gui, long delta)
    {
        graphics.blit(TEXTURE, 0, 0, 0, 0, 160, 32);
        graphics.drawString(gui.getMinecraft().font, TITLE, 35, 7, 0xFFFFFF, false);
        graphics.drawString(gui.getMinecraft().font, this.name, 35, 18, 0xFFFFFF, false);
        CustomiseBackpackScreen.drawBackpackInGui(Minecraft.getInstance(), graphics, new ItemStack(ModItems.BACKPACK.get()), this.backpack, 18, 16, 0);
        return delta >= 5000L ? Visibility.HIDE : Visibility.SHOW;
    }
}
