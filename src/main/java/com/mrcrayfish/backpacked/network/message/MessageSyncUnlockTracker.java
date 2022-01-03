package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageSyncUnlockTracker implements IMessage<MessageSyncUnlockTracker>
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
    public void handle(MessageSyncUnlockTracker message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> ClientPlayHandler.handleSyncUnlockTracker(message));
        supplier.get().setPacketHandled(true);
    }

    public Set<ResourceLocation> getUnlockedBackpacks()
    {
        return this.unlockedBackpacks;
    }
}
