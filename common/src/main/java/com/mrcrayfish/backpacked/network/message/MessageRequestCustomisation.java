package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * Author: MrCrayfish
 */
public record MessageRequestCustomisation()
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageRequestCustomisation> STREAM_CODEC = StreamCodec.unit(new MessageRequestCustomisation());

    public static void handle(MessageRequestCustomisation message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleRequestCustomisation(message, context));
        context.setHandled(true);
    }
}
