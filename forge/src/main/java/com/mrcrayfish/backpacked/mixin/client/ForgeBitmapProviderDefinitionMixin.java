package com.mrcrayfish.backpacked.mixin.client;

import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.client.gui.font.providers.BitmapProvider;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BitmapProvider.Definition.class)
public abstract class ForgeBitmapProviderDefinitionMixin
{
    /* First, define a variable to hold remove rule */
    @Unique
    private boolean backpacked$removeOnePixel;

    /* Second, during the definition init, check if the icons file and store the result */
    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void backpacked$DefinitionInit(ResourceLocation file, int height, int ascent, int[][] map, CallbackInfo ci)
    {
        this.backpacked$removeOnePixel = file.equals(Utils.rl("gui/icons.png"));
    }

    /* Thirdly, while init the glyph, remove the extra pixel space that is added to the real width */
    @ModifyArg(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/font/providers/BitmapProvider$Glyph;<init>(FLcom/mojang/blaze3d/platform/NativeImage;IIIIII)V"), index = 6)
    private int backpacked$RemoveExtraWidth(int original)
    {
        if(this.backpacked$removeOnePixel)
        {
            return original - 1;
        }
        return original;
    }
}
