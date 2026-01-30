package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public final class MessageSyncUnlockSlot extends PlayMessage<MessageSyncUnlockSlot> // TODO DONE
{
    private List<Integer> unlockedSlotIndexes;

    public MessageSyncUnlockSlot() {}

    public MessageSyncUnlockSlot(List<Integer> unlockedSlotIndexes)
    {
        this.unlockedSlotIndexes = unlockedSlotIndexes;
    }

    @Override
    public void encode(MessageSyncUnlockSlot message, FriendlyByteBuf buf)
    {
        buf.writeCollection(message.unlockedSlotIndexes, FriendlyByteBuf::writeInt);
    }

    @Override
    public MessageSyncUnlockSlot decode(FriendlyByteBuf buf)
    {
        List<Integer> unlockedSlotIndexes = buf.readCollection(ArrayList::new, FriendlyByteBuf::readInt);
        return new MessageSyncUnlockSlot(unlockedSlotIndexes);
    }

    @Override
    public void handle(MessageSyncUnlockSlot message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleUnlockSlot(message));
        context.setHandled(true);
    }

    public List<Integer> unlockedSlotIndexes()
    {
        return this.unlockedSlotIndexes;
    }
}
