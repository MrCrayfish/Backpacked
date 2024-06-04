package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * Author: MrCrayfish
 */
public record MessageOpenBackpack()
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageOpenBackpack> STREAM_CODEC = StreamCodec.unit(new MessageOpenBackpack());

    public static void handle(MessageOpenBackpack message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleOpenBackpack(message, context));
        context.setHandled(true);
    }
}
