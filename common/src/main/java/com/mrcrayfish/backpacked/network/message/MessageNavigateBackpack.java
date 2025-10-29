package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.Navigate;
import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MessageNavigateBackpack(Navigate navigate)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageNavigateBackpack> STREAM_CODEC = StreamCodec.composite(
         Navigate.STREAM_CODEC, MessageNavigateBackpack::navigate,
         MessageNavigateBackpack::new
    );

    public static void handle(MessageNavigateBackpack message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleNavigateBackpack(message, context));
        context.setHandled(true);
    }
}
