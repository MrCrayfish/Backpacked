package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public record MessageUnlockAugmentBay(Augments.Position position)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageUnlockAugmentBay> STREAM_CODEC = StreamCodec.composite(
        Augments.Position.STREAM_CODEC, MessageUnlockAugmentBay::position,
        MessageUnlockAugmentBay::new
    );

    public static void handle(MessageUnlockAugmentBay message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleUnlockAugmentBay(message, context));
        context.setHandled(true);
    }
}
