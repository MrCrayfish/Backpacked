package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.PlayMessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Author: MrCrayfish
 */
public record MessageRequestCustomisation(int backpackIndex)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageRequestCustomisation> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            MessageRequestCustomisation::backpackIndex,
            MessageRequestCustomisation::new
    );

    public static void handle(MessageRequestCustomisation message, PlayMessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleRequestCustomisation(message, context));
        context.setHandled(true);
    }
}
