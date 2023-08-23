package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Author: MrCrayfish
 */
public class MessageRequestCustomisation extends PlayMessage<MessageRequestCustomisation>
{
    @Override
    public void encode(MessageRequestCustomisation message, FriendlyByteBuf buffer) {}

    @Override
    public MessageRequestCustomisation decode(FriendlyByteBuf buffer)
    {
        return new MessageRequestCustomisation();
    }

    @Override
    public void handle(MessageRequestCustomisation message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleRequestCustomisation(message, context.getPlayer()));
        context.setHandled(true);
    }
}
