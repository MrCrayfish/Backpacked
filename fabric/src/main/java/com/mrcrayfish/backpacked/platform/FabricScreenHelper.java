package com.mrcrayfish.backpacked.platform;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.platform.services.IScreenHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.inventory.Slot;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class FabricScreenHelper implements IScreenHelper
{
    @Override
    public void openConfigScreen()
    {
        if(!FabricLoader.getInstance().isModLoaded("configured"))
        {
            MutableComponent modName = Component.literal("Configured");
            modName.setStyle(modName.getStyle()
                    .withColor(ChatFormatting.YELLOW)
                    .withUnderlined(true)
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("backpacked.chat.open_curseforge_page")))
                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods/configured-fabric")));
            Component message = Component.translatable("backpacked.chat.install_configured", modName);
            Optional.ofNullable(Minecraft.getInstance().player).ifPresent(player -> player.displayClientMessage(message, false));
            return;
        }

        FabricLoader.getInstance().getModContainer(Constants.MOD_ID).ifPresent(container ->
        {
            try
            {
                Class<?> factoryClass = Class.forName("com.mrcrayfish.configured.integration.CatalogueConfigFactory");
                Method createConfigScreen = factoryClass.getDeclaredMethod("createConfigScreen", Screen.class, ModContainer.class);
                createConfigScreen.invoke(null, Minecraft.getInstance().screen, container);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        });
    }
}
