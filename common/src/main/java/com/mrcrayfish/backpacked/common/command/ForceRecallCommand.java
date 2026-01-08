package com.mrcrayfish.backpacked.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mrcrayfish.backpacked.common.augment.data.Recall;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.permissions.Permissions;

/**
 * Author: MrCrayfish
 */
public class ForceRecallCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(Commands.literal("backpacked:force_recall").requires(source -> {
            return source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
        }).executes(context -> {
            MinecraftServer server = context.getSource().getServer();
            server.getAllLevels().forEach(serverLevel -> {
                ((Recall.Access) serverLevel).backpacked$getRecall().forceNextRun();
            });
            context.getSource().sendSuccess(() -> Component.literal("Forcing Recall to deliver backpacks..."), false);
            return 1;
        }));
    }
}
