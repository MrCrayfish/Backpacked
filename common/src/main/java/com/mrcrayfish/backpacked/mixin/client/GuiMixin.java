package com.mrcrayfish.backpacked.mixin.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin
{
    @Shadow
    private int overlayMessageTime;

    @Inject(method = "setOverlayMessage", at = @At(value = "TAIL"))
    private void backpacked$IncreaseDisplayTime(Component message, boolean animate, CallbackInfo ci)
    {
        if(message.getContents() instanceof TranslatableContents contents)
        {
            if(contents.getKey().equals("backpacked.gui.after_equipped_message"))
            {
                // Add an extra two seconds to the display time
                this.overlayMessageTime += 40;
            }
        }
    }
}
