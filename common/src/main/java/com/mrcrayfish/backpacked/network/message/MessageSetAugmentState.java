package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record MessageSetAugmentState(Augments.Position position, boolean state)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSetAugmentState> STREAM_CODEC = StreamCodec.composite(
        Augments.Position.STREAM_CODEC, MessageSetAugmentState::position,
        ByteBufCodecs.BOOL, MessageSetAugmentState::state,
        MessageSetAugmentState::new
    );

    public static void handle(MessageSetAugmentState message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleSetAugmentState(message, context));
        context.setHandled(true);
    }
}
