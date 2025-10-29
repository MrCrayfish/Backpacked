package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record MessagePickpocketBackpack(int entityId)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessagePickpocketBackpack> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, MessagePickpocketBackpack::entityId,
        MessagePickpocketBackpack::new
    );

    public static void handle(MessagePickpocketBackpack message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handlePickpocketBackpack(message, context));
        context.setHandled(true);
    }
}
