package com.mrcrayfish.backpacked.common.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mrcrayfish.backpacked.common.Backpack;
import com.mrcrayfish.backpacked.common.BackpackManager;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

/**
 * Author: MrCrayfish
 */
public class BackpackArgument implements ArgumentType<Backpack>
{
    public static BackpackArgument backpacks()
    {
        return new BackpackArgument();
    }

    @Override
    public Backpack parse(StringReader reader) throws CommandSyntaxException
    {
        ResourceLocation id = ResourceLocation.read(reader);
        return BackpackManager.instance().getRegisteredBackpacks().stream().filter(backpack -> backpack.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        BackpackManager.instance().getRegisteredBackpacks().forEach(backpack -> builder.suggest(backpack.getId().toString()));
        return builder.buildFuture();
    }
}
