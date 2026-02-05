package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.packs.AddonRepositorySource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Pack.class)
public class PackMixin
{
    @Inject(method = "readMetaAndCreate", at = @At(value = "HEAD"), cancellable = true)
    private static void readMetaAndCreate(String id, Component $$1, boolean $$2, Pack.ResourcesSupplier supplier, PackType type, Pack.Position $$5, PackSource source, CallbackInfoReturnable<Pack> cir)
    {
        Optional<Optional<Pack>> result = AddonRepositorySource.tryAndReadAddonPack(id, supplier, type, source);
        result.ifPresent(value -> cir.setReturnValue(value.orElse(null)));
    }
}
