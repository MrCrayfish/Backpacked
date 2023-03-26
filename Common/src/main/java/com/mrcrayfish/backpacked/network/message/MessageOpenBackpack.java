package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Author: MrCrayfish
 */
public class MessageOpenBackpack extends PlayMessage<MessageOpenBackpack>
{
    @Override
    public void encode(MessageOpenBackpack message, FriendlyByteBuf buffer) {}

    @Override
    public MessageOpenBackpack decode(FriendlyByteBuf buffer)
    {
        return new MessageOpenBackpack();
    }

    @Override
    public void handle(MessageOpenBackpack message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleOpenBackpack(message, context.getPlayer()));
        context.setHandled(true);
    }
}
