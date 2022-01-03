package com.mrcrayfish.backpacked.core;

import com.mojang.brigadier.CommandDispatcher;
import com.mrcrayfish.backpacked.common.command.UnlockBackpackCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmlserverevents.FMLServerAboutToStartEvent;

/**
 * Author: MrCrayfish
 */
public class ModCommands
{
    @SubscribeEvent
    public void onServerStart(FMLServerAboutToStartEvent event)
    {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getServer().getCommands().getDispatcher();
        UnlockBackpackCommand.register(dispatcher);
    }
}
