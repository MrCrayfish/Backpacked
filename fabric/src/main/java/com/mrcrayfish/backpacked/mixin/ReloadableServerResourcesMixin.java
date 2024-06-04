package com.mrcrayfish.backpacked.mixin;

import com.mrcrayfish.backpacked.common.backpack.loader.FabricBackpackLoader;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin
{
    @Final
    @Shadow
    private ReloadableServerResources.ConfigurableRegistryLookup registryLookup;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void backpacked$onInitTail(RegistryAccess.Frozen frozen, FeatureFlagSet set, Commands.CommandSelection selection, int i, CallbackInfo ci)
    {
        FabricBackpackLoader.setProvider(this.registryLookup);
    }
}
