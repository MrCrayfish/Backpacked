package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Author: MrCrayfish
 */
public record MessageRequestCustomisation()
{
    public static void encode(MessageRequestCustomisation message, FriendlyByteBuf buffer) {}

    public static MessageRequestCustomisation decode(FriendlyByteBuf buffer)
    {
        return new MessageRequestCustomisation();
    }

    public static void handle(MessageRequestCustomisation message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleRequestCustomisation(message, context));
        context.setHandled(true);
    }
}
