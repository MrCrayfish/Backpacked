package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.packs.AddonPack;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Pack.class)
public class PackMixin
{
    @Inject(method = "readMetaAndCreate", at = @At(value = "HEAD"), cancellable = true)
    private static void readMetaAndCreate(PackLocationInfo info, Pack.ResourcesSupplier supplier, PackType type, PackSelectionConfig config, CallbackInfoReturnable<Pack> cir)
    {
        AddonPack pack = AddonPack.tryAndReadAddonPack(info, supplier, type);
        if(pack != null)
        {
            cir.setReturnValue(pack);
        }
    }
}
