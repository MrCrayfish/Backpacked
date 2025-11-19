package com.mrcrayfish.backpacked.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mrcrayfish.backpacked.common.augment.data.Recall;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.common.command.arguments.BackpackArgument;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/**
 * Author: MrCrayfish
 */
public class FlushRecallQueueCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(Commands.literal("flush_recall_queue").requires(source -> {
            return source.hasPermission(2);
        }).executes(context -> {
            MinecraftServer server = context.getSource().getServer();
            int[] count = {0};
            server.getAllLevels().forEach(serverLevel -> {
                count[0] += ((Recall.Access) serverLevel).backpacked$getRecall().flush(server);
            });
            context.getSource().sendSuccess(() -> Component.literal("Flushed %s backpacks".formatted(count[0])), false);
            return 1;
        }));
    }
}
