package com.mrcrayfish.backpacked.mixin;

import com.mrcrayfish.backpacked.packs.AddonRepositorySource;
import com.mrcrayfish.backpacked.packs.PackRepositoryHelper;
import com.mrcrayfish.framework.api.FrameworkAPI;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.world.level.validation.DirectoryValidator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;

@Mixin(ServerPacksSource.class)
public class ServerPacksSourceMixin
{
    @Inject(method = "createPackRepository(Ljava/nio/file/Path;Lnet/minecraft/world/level/validation/DirectoryValidator;)Lnet/minecraft/server/packs/repository/PackRepository;", at = @At(value = "RETURN"))
    private static void backpacked$injectBackpackedAddonSource(Path path, DirectoryValidator directoryValidator, CallbackInfoReturnable<PackRepository> cir)
    {
        if(FrameworkAPI.getEnvironment().isClient())
        {
            Path gameDir = FabricLoader.getInstance().getGameDir();
            Path addonDir = gameDir.resolve("resourcepacks");
            PackRepositoryHelper.addSource(cir.getReturnValue(), new AddonRepositorySource(addonDir, PackType.SERVER_DATA, PackSource.FEATURE, directoryValidator));
        }
    }
}
