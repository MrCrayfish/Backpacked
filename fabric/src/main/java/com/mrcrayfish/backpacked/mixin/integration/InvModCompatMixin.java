package com.mrcrayfish.backpacked.mixin.integration;

import com.mrcrayfish.backpacked.integration.YoureInGraveDangerSupport;
import org.spongepowered.asm.mixin.Mixin;
import com.b1n_ry.yigd.compat.InvModCompat;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*
 * This is temporary. Issue has been posted at https://github.com/B1n-ry/Youre-in-grave-danger/issues/216
 * requesting a safe method to register custom InvModCompat implementations.
 */

@Pseudo
@Mixin(InvModCompat.class)
public interface InvModCompatMixin
{
    @Inject(method = "reloadModCompat", at = @At(value = "TAIL"), remap = false)
    private static void backpacked$Register(CallbackInfo ci)
    {
        YoureInGraveDangerSupport.reload();
    }
}
