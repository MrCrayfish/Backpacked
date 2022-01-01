package com.mrcrayfish.backpacked.core;

import com.mojang.brigadier.CommandDispatcher;
import com.mrcrayfish.backpacked.common.command.UnlockBackpackCommand;
import net.minecraft.command.CommandSource;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

/**
 * Author: MrCrayfish
 */
public class ModCommands
{
    @SubscribeEvent
    public void onServerStart(FMLServerAboutToStartEvent event)
    {
        CommandDispatcher<CommandSource> dispatcher = event.getServer().getCommands().getDispatcher();
        UnlockBackpackCommand.register(dispatcher);
    }
}
