package com.mrcrayfish.backpacked.core;

import com.mojang.brigadier.CommandDispatcher;
import com.mrcrayfish.backpacked.common.command.FlushRecallQueueCommand;
import com.mrcrayfish.backpacked.common.command.ForceRecallCommand;
import com.mrcrayfish.backpacked.common.command.UnlockBackpackCommand;
import com.mrcrayfish.framework.api.event.ServerEvents;
import net.minecraft.commands.CommandSourceStack;

/**
 * Author: MrCrayfish
 */
public class ModCommands
{
    public static void init()
    {
        ServerEvents.STARTING.register(server ->
        {
            CommandDispatcher<CommandSourceStack> dispatcher = server.getCommands().getDispatcher();
            UnlockBackpackCommand.register(dispatcher);
            FlushRecallQueueCommand.register(dispatcher);
            ForceRecallCommand.register(dispatcher);
        });
    }
}
