package com.mrcrayfish.backpacked.common.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.client.ClientRegistry;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
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
        return BackpackManager.instance().getBackpacks().stream().filter(backpack -> backpack.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        List<String> disabled = Config.BACKPACK.cosmetics.disabledCosmetics.get();
        ClientRegistry.instance().getBackpacks().forEach(backpack -> {
            String id = backpack.getId().toString();
            if(!disabled.contains(id)) {
                builder.suggest(id);
            }
        });
        return builder.buildFuture();
    }
}
