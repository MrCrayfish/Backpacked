package com.mrcrayfish.backpacked.mixin.client;

import com.mrcrayfish.backpacked.Constants;
import net.minecraft.client.gui.font.providers.BitmapProvider;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(BitmapProvider.Definition.class)
public abstract class BitmapProviderDefinitionMixin
{
    /* First, define a variable to hold remove rule */
    @Unique
    private boolean backpacked$removeOnePixel;

    /* Second, during the definition init, check if the icons file and store the result */
    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void backpacked$DefinitionInit(ResourceLocation file, int height, int ascent, int[][] map, CallbackInfo ci)
    {
        this.backpacked$removeOnePixel = file.equals(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "gui/icons.png"));
    }

    /* Thirdly, while init the glyph, remove the extra pixel space that is added to the real width */
    @ModifyArgs(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/font/providers/BitmapProvider$Glyph;<init>(FLcom/mojang/blaze3d/platform/NativeImage;IIIIII)V"))
    private void backpacked$RemoveExtraWidth(Args args)
    {
        if(this.backpacked$removeOnePixel)
        {
            Object arg = args.get(6); // 6 is the index for the width param
            if(arg instanceof Integer)
            {
                // Remove the added pixel
                args.set(6, (int) arg - 1);
            }
        }
    }
}
