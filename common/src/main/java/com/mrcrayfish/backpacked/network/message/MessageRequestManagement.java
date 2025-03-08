package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MessageRequestManagement()
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageRequestManagement> STREAM_CODEC = StreamCodec.unit(new MessageRequestManagement());

    public static void handle(MessageRequestManagement message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleRequestManagement(message, context));
        context.setHandled(true);
    }
}
