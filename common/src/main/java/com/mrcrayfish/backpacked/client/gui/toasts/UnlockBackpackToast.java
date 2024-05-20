package com.mrcrayfish.backpacked.client.gui.toasts;

import com.mrcrayfish.backpacked.common.backpack.Backpack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;

/**
 * Author: MrCrayfish
 */
public class UnlockBackpackToast implements Toast
{
    private static final Component TITLE = Component.translatable("backpacked.toast.unlocked_backpack").withStyle(ChatFormatting.YELLOW);

    private final Component name;

    public UnlockBackpackToast(Backpack backpack)
    {
        this.name = Component.translatable(backpack.getId().getNamespace() + ".backpack." + backpack.getId().getPath());
    }

    @Override
    public Visibility render(GuiGraphics graphics, ToastComponent gui, long delta)
    {
        graphics.blit(TEXTURE, 0, 0, 0, 0, 160, 32);
        graphics.drawString(gui.getMinecraft().font, TITLE, 35, 7, 0xFFFFFF, false);
        graphics.drawString(gui.getMinecraft().font, this.name, 35, 18, 0xFFFFFF, false);
        // TODO fix this
        //CustomiseBackpackScreen.drawBackpackModel(graphics, this.model, 15, 7, 30F, 0, 0F);
        return delta >= 5000L ? Visibility.HIDE : Visibility.SHOW;
    }
}
