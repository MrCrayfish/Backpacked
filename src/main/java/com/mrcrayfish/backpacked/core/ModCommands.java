package com.mrcrayfish.backpacked.core;

import com.mojang.brigadier.CommandDispatcher;
import com.mrcrayfish.backpacked.common.command.UnlockBackpackCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Author: MrCrayfish
 */
public class ModCommands
{
    @SubscribeEvent
    public void onServerStart(ServerAboutToStartEvent event)
    {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getServer().getCommands().getDispatcher();
        UnlockBackpackCommand.register(dispatcher);
    }
}
