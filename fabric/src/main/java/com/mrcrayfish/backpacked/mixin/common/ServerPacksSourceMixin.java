package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.packs.AddonRepositorySource;
import com.mrcrayfish.backpacked.packs.PackRepositoryHelper;
import com.mrcrayfish.framework.api.util.EnvironmentHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.ServerPacksSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;

@Mixin(ServerPacksSource.class)
public class ServerPacksSourceMixin
{
    @Inject(method = "createPackRepository(Ljava/nio/file/Path;)Lnet/minecraft/server/packs/repository/PackRepository;", at = @At(value = "RETURN"))
    private static void backpacked$injectBackpackedAddonSource(Path path, CallbackInfoReturnable<PackRepository> cir)
    {
        if(EnvironmentHelper.getEnvironment().isClient())
        {
            Path addonDir = Backpacked.resourcepackDir;
            if(addonDir != null)
            {
                PackRepositoryHelper.addSource(cir.getReturnValue(), new AddonRepositorySource(addonDir, PackType.SERVER_DATA, PackSource.FEATURE));
            }
        }
    }
}
