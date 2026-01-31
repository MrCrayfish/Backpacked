package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;

public final class MessageSyncUnlockAugmentBay extends PlayMessage<MessageSyncUnlockAugmentBay>
{
    private Augments.Position position;

    public MessageSyncUnlockAugmentBay() {}

    public MessageSyncUnlockAugmentBay(Augments.Position position)
    {
        this.position = position;
    }

    @Override
    public void encode(MessageSyncUnlockAugmentBay message, FriendlyByteBuf buf)
    {
        buf.writeEnum(message.position);
    }

    @Override
    public MessageSyncUnlockAugmentBay decode(FriendlyByteBuf buf)
    {
        return new MessageSyncUnlockAugmentBay(buf.readEnum(Augments.Position.class));
    }

    @Override
    public void handle(MessageSyncUnlockAugmentBay message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleUnlockAugmentBay(message));
        context.setHandled(true);
    }

    public Augments.Position position()
    {
        return this.position;
    }
}
