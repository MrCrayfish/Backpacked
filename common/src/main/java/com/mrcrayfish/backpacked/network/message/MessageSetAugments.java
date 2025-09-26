package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MessageSetAugments(Augments augments)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSetAugments> STREAM_CODEC = StreamCodec.composite(
        Augments.STREAM_CODEC, MessageSetAugments::augments,
        MessageSetAugments::new
    );

    public static void handle(MessageSetAugments message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleSetAugments(message, context));
        context.setHandled(true);
    }
}
