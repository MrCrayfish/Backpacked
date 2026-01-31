package com.mrcrayfish.backpacked.network.message;

import com.google.common.base.Preconditions;
import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public final class MessageUnlockSlot extends PlayMessage<MessageUnlockSlot>
{
    private List<Integer> slotIndexes;

    public MessageUnlockSlot() {}

    public MessageUnlockSlot(List<Integer> slotIndexes)
    {
        this.slotIndexes = slotIndexes;
    }

    @Override
    public void encode(MessageUnlockSlot message, FriendlyByteBuf buf)
    {
        Preconditions.checkArgument(message.slotIndexes.size() <= 255);
        buf.writeCollection(message.slotIndexes, FriendlyByteBuf::writeVarInt);
    }

    @Override
    public MessageUnlockSlot decode(FriendlyByteBuf buf)
    {
        List<Integer> slotIndexes = buf.readCollection(ArrayList::new, FriendlyByteBuf::readVarInt);
        return new MessageUnlockSlot(slotIndexes);
    }

    @Override
    public void handle(MessageUnlockSlot message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleUnlockSlot(message, context));
        context.setHandled(true);
    }

    public List<Integer> slotIndexes()
    {
        return this.slotIndexes;
    }
}
