package com.mrcrayfish.backpacked.mixin.client;

import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.client.gui.font.providers.BitmapProvider;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BitmapProvider.Definition.class)
public abstract class BitmapProviderDefinitionMixin
{
    /* First, define a variable to hold remove rule */
    @Unique
    private boolean backpacked$removeOnePixel;

    /* Second, during the definition init, check if the icons file and store the result */
    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void backpacked$DefinitionInit(Identifier file, int height, int ascent, int[][] map, CallbackInfo ci)
    {
        this.backpacked$removeOnePixel = file.equals(Utils.id("gui/icons.png"));
    }

    /* Thirdly, while init the glyph, remove the extra pixel space that is added to the real width */
    // MC 26.2: the Glyph constructor's NativeImage param is now wrapped in a package-private ImageDataHolder type.
    // @ModifyArgs's generic Args wrapper has to box every constructor param (including that inaccessible type),
    // which throws IllegalAccessError at runtime. @ModifyArg only touches the one int param we care about (index 6,
    // "advance"), so it never needs to reference ImageDataHolder at all.
    @ModifyArg(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/font/providers/BitmapProvider$Glyph;<init>(FLnet/minecraft/client/gui/font/providers/BitmapProvider$ImageDataHolder;IIIIII)V"), index = 6)
    private int backpacked$RemoveExtraWidth(int advance)
    {
        return this.backpacked$removeOnePixel ? advance - 1 : advance;
    }
}
