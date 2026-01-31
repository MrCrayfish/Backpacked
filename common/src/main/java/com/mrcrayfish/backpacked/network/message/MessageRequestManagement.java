package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;

public final class MessageRequestManagement extends PlayMessage<MessageRequestManagement>
{
    @Override
    public void encode(MessageRequestManagement message, FriendlyByteBuf buf) {}

    @Override
    public MessageRequestManagement decode(FriendlyByteBuf buf)
    {
        return new MessageRequestManagement();
    }

    @Override
    public void handle(MessageRequestManagement message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleRequestManagement(message, context));
        context.setHandled(true);
    }
}
