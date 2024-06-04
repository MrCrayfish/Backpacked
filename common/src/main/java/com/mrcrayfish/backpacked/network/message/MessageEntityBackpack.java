package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Author: MrCrayfish
 */
public record MessageEntityBackpack(int entityId)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageEntityBackpack> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, MessageEntityBackpack::entityId,
        MessageEntityBackpack::new
    );

    public static void handle(MessageEntityBackpack message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleEntityBackpack(message, context));
        context.setHandled(true);
    }
}
