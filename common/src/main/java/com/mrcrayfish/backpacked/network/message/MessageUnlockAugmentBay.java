package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Author: MrCrayfish
 */
public final class MessageUnlockAugmentBay extends PlayMessage<MessageUnlockAugmentBay> // TODO DONE
{
    private Augments.Position position;

    public MessageUnlockAugmentBay() {}

    public MessageUnlockAugmentBay(Augments.Position position)
    {
        this.position = position;
    }

    @Override
    public void encode(MessageUnlockAugmentBay message, FriendlyByteBuf buf)
    {
        buf.writeEnum(message.position);
    }

    @Override
    public MessageUnlockAugmentBay decode(FriendlyByteBuf buf)
    {
        return new MessageUnlockAugmentBay(buf.readEnum(Augments.Position.class));
    }

    public void handle(MessageUnlockAugmentBay message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleUnlockAugmentBay(message, context));
        context.setHandled(true);
    }

    public Augments.Position position()
    {
        return this.position;
    }
}
