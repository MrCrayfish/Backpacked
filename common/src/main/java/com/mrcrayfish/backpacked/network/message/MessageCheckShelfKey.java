package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record MessageCheckShelfKey(int backpackIndex, Augments.Position position)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageCheckShelfKey> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, MessageCheckShelfKey::backpackIndex,
        Augments.Position.STREAM_CODEC, MessageCheckShelfKey::position,
        MessageCheckShelfKey::new
    );

    public static void handle(MessageCheckShelfKey message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleMessageCheckShelfKey(message, context));
        context.setHandled(true);
    }
}
