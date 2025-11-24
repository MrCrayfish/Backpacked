package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record MessageResponseShelfKey(int backpackIndex, Augments.Position position, boolean valid)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageResponseShelfKey> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, MessageResponseShelfKey::backpackIndex,
        Augments.Position.STREAM_CODEC, MessageResponseShelfKey::position,
        ByteBufCodecs.BOOL, MessageResponseShelfKey::valid,
        MessageResponseShelfKey::new
    );

    public static void handle(MessageResponseShelfKey message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleMessageResponseShelfKey(message, context));
        context.setHandled(true);
    }
}
