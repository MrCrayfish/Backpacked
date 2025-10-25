package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MessageSyncAugmentChange(Augments.Position position, Augment<?> augment)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSyncAugmentChange> STREAM_CODEC = StreamCodec.composite(
        Augments.Position.STREAM_CODEC, MessageSyncAugmentChange::position,
        Augment.STREAM_CODEC, MessageSyncAugmentChange::augment,
        MessageSyncAugmentChange::new
    );

    public static void handle(MessageSyncAugmentChange message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleSyncAugmentChange(message));
        context.setHandled(true);
    }
}
