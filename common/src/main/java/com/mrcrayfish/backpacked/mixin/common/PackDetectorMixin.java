package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.packs.AddonMetadata;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/*
 * Mixin Goal:
 * Resolves an issue in the pack detector when an addon is a directory. Without the patch, the game
 * expects a pack.mcmeta file and will skip loading the addon. This mixin includes an additional check
 * to look for the backpacked_addon.mcmeta file.
 */
@Mixin(FolderRepositorySource.class)
public abstract class PackDetectorMixin
{
    @Inject(method = "detectPackResources", at = @At(value = "INVOKE", target = "Ljava/nio/file/attribute/BasicFileAttributes;isDirectory()Z"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private static void testForAddonMetadata(Path path, boolean builtIn, CallbackInfoReturnable<Pack.ResourcesSupplier> cir, BasicFileAttributes attributes)
    {
        if(attributes.isDirectory() && Files.isRegularFile(path.resolve(AddonMetadata.FILE_NAME)))
        {
            cir.setReturnValue(id -> new PathPackResources(id, path, builtIn));
        }
    }
}
