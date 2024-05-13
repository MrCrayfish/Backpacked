package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

/**
 * Author: MrCrayfish
 */
public record MessageSyncUnlockTracker(Set<ResourceLocation> unlockedBackpacks)
{
    public static void encode(MessageSyncUnlockTracker message, FriendlyByteBuf buffer)
    {
        buffer.writeInt(message.unlockedBackpacks.size());
        message.unlockedBackpacks.forEach(buffer::writeResourceLocation);
    }

    public static MessageSyncUnlockTracker decode(FriendlyByteBuf buffer)
    {
        int size = buffer.readInt();
        Set<ResourceLocation> unlockedBackpacks = new HashSet<>();
        for(int i = 0; i < size; i++)
        {
            unlockedBackpacks.add(buffer.readResourceLocation());
        }
        return new MessageSyncUnlockTracker(unlockedBackpacks);
    }

    public static void handle(MessageSyncUnlockTracker message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleSyncUnlockTracker(message));
        context.setHandled(true);
    }
}
