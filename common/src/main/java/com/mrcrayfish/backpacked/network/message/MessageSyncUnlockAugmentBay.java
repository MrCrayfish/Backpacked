package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MessageSyncUnlockAugmentBay(Augments.Position position)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSyncUnlockAugmentBay> STREAM_CODEC = StreamCodec.composite(
        Augments.Position.STREAM_CODEC, MessageSyncUnlockAugmentBay::position,
        MessageSyncUnlockAugmentBay::new
    );

    public static void handle(MessageSyncUnlockAugmentBay message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleUnlockAugmentBay(message));
        context.setHandled(true);
    }
}
