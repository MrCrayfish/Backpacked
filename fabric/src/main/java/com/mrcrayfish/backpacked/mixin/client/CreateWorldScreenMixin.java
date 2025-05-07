package com.mrcrayfish.backpacked.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mrcrayfish.backpacked.packs.AddonRepositorySource;
import com.mrcrayfish.backpacked.packs.PackRepositoryHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin
{
    @Inject(method = "openFresh", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/worldselection/CreateWorldScreen;createDefaultLoadConfig(Lnet/minecraft/server/packs/repository/PackRepository;Lnet/minecraft/world/level/WorldDataConfiguration;)Lnet/minecraft/server/WorldLoader$InitConfig;"))
    private static void injectAddonSources(Minecraft minecraft, Screen screen, CallbackInfo ci, @Local PackRepository repository)
    {
        Path gameDir = FabricLoader.getInstance().getGameDir();
        Path addonDir = gameDir.resolve("resourcepacks");
        PackRepositoryHelper.addSource(repository, new AddonRepositorySource(addonDir, PackType.SERVER_DATA, PackSource.FEATURE, minecraft.directoryValidator()));
    }
}
