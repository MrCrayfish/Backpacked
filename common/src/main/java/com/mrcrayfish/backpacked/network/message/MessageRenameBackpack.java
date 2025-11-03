package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record MessageRenameBackpack(String value)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageRenameBackpack> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        MessageRenameBackpack::value,
        MessageRenameBackpack::new
    );

    public static void handle(MessageRenameBackpack message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleRenameBackpack(message, context));
        context.setHandled(true);
    }
}
