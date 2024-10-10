package com.mrcrayfish.backpacked.platform;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.platform.services.IScreenHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/**
 * Author: MrCrayfish
 */
public class NeoForgeScreenHelper implements IScreenHelper
{
    @Override
    public void openConfigScreen()
    {
        Minecraft minecraft = Minecraft.getInstance();
        ModList.get().getModContainerById(Constants.MOD_ID).ifPresent(container ->
        {
            Screen screen = container.getCustomExtension(IConfigScreenFactory.class).map(function -> function.createScreen(container, null)).orElse(null);
            if(screen != null)
            {
                minecraft.setScreen(screen);
            }
            else if(minecraft.player != null)
            {
                MutableComponent modName = Component.literal("Configured");
                modName.setStyle(modName.getStyle()
                        .withColor(ChatFormatting.YELLOW)
                        .withUnderlined(true)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("backpacked.chat.open_curseforge_page")))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods/configured")));
                Component message = Component.translatable("backpacked.chat.install_configured", modName);
                minecraft.player.displayClientMessage(message, false);
            }
        });
    }
}
