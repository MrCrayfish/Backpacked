package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MessageUpdateAugment(Augments.Position position, Augment<?> augment)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageUpdateAugment> STREAM_CODEC = StreamCodec.composite(
        Augments.Position.STREAM_CODEC, MessageUpdateAugment::position,
        Augment.STREAM_CODEC, MessageUpdateAugment::augment,
        MessageUpdateAugment::new
    );

    public static void handle(MessageUpdateAugment message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleUpdateAugment(message, context));
        context.setHandled(true);
    }
}
