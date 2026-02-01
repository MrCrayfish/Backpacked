package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Author: MrCrayfish
 */
public final class MessageRequestCustomisation extends PlayMessage<MessageRequestCustomisation>
{
    private int backpackIndex;

    public MessageRequestCustomisation() {}

    public MessageRequestCustomisation(int backpackIndex)
    {
        this.backpackIndex = backpackIndex;
    }

    @Override
    public void encode(MessageRequestCustomisation message, FriendlyByteBuf buf)
    {
        buf.writeInt(message.backpackIndex);
    }

    @Override
    public MessageRequestCustomisation decode(FriendlyByteBuf buf)
    {
        return new MessageRequestCustomisation(buf.readInt());
    }

    @Override
    public void handle(MessageRequestCustomisation message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleRequestCustomisation(message, context));
        context.setHandled(true);
    }

    public int backpackIndex()
    {
        return this.backpackIndex;
    }
}
