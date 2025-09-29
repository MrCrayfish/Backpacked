package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record MessageChangeAugment(Augments.Position position, ResourceLocation augmentTypeId)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageChangeAugment> STREAM_CODEC = StreamCodec.composite(
        Augments.Position.STREAM_CODEC, MessageChangeAugment::position,
        ResourceLocation.STREAM_CODEC, MessageChangeAugment::augmentTypeId,
        MessageChangeAugment::new
    );

    public MessageChangeAugment(Augments.Position position, Augment<?> augment)
    {
        this(position, augment.type().id());
    }

    public static void handle(MessageChangeAugment message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleChangeAugment(message, context));
        context.setHandled(true);
    }
}
