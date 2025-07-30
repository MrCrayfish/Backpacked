package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record MessageNavigateBackpackIndex(boolean forward)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageNavigateBackpackIndex> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            MessageNavigateBackpackIndex::forward,
            MessageNavigateBackpackIndex::new
    );

    public static void handle(MessageNavigateBackpackIndex message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleNavigateBackpackIndex(message, context));
        context.setHandled(true);
    }
}
