package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

/**
 * Author: MrCrayfish
 */
public class MessageSyncUnlockTracker extends PlayMessage<MessageSyncUnlockTracker>
{
    private Set<ResourceLocation> unlockedBackpacks;

    public MessageSyncUnlockTracker() {}

    public MessageSyncUnlockTracker(Set<ResourceLocation> unlockedBackpacks)
    {
        this.unlockedBackpacks = unlockedBackpacks;
    }

    @Override
    public void encode(MessageSyncUnlockTracker message, FriendlyByteBuf buffer)
    {
        buffer.writeInt(message.unlockedBackpacks.size());
        message.unlockedBackpacks.forEach(buffer::writeResourceLocation);
    }

    @Override
    public MessageSyncUnlockTracker decode(FriendlyByteBuf buffer)
    {
        int size = buffer.readInt();
        Set<ResourceLocation> unlockedBackpacks = new HashSet<>();
        for(int i = 0; i < size; i++)
        {
            unlockedBackpacks.add(buffer.readResourceLocation());
        }
        return new MessageSyncUnlockTracker(unlockedBackpacks);
    }

    @Override
    public void handle(MessageSyncUnlockTracker message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleSyncUnlockTracker(message));
        context.setHandled(true);
    }

    public Set<ResourceLocation> unlockedBackpacks()
    {
        return this.unlockedBackpacks;
    }
}
