package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Author: MrCrayfish
 */
public record MessageOpenBackpack(int backpackIndex)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageOpenBackpack> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, MessageOpenBackpack::backpackIndex,
        MessageOpenBackpack::new
    );

    public MessageOpenBackpack()
    {
        this(-1);
    }

    public static void handle(MessageOpenBackpack message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleOpenBackpack(message, context));
        context.setHandled(true);
    }
}
