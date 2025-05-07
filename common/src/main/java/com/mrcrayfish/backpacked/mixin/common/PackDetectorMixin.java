package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.packs.AddonMetadata;
import net.minecraft.server.packs.repository.PackDetector;
import net.minecraft.world.level.validation.ForbiddenSymlinkInfo;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/*
 * Mixin Goal:
 * Resolves an issue in the pack detector when an addon is a directory. Without the patch, the game
 * expects a pack.mcmeta file and will skip loading the addon. This mixin includes an additional check
 * to look for the backpacked_addon.mcmeta file.
 */
@Mixin(PackDetector.class)
public abstract class PackDetectorMixin<T>
{
    @Shadow
    @Nullable
    protected abstract T createDirectoryPack(Path path) throws IOException;

    @Inject(method = "detectPackResources", at = @At(value = "RETURN", ordinal = 3), cancellable = true)
    private void testForAddonMetadata(Path path, List<ForbiddenSymlinkInfo> $$1, CallbackInfoReturnable<T> cir) throws IOException
    {
        if(Files.isRegularFile(path.resolve(AddonMetadata.FILE_NAME)))
        {
            cir.setReturnValue(this.createDirectoryPack(path));
        }
    }
}
