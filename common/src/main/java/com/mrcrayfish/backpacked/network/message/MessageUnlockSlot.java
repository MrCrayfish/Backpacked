package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Author: MrCrayfish
 */
public record MessageUnlockSlot(int slotIndex)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageUnlockSlot> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, MessageUnlockSlot::slotIndex,
        MessageUnlockSlot::new
    );

    public static void handle(MessageUnlockSlot message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleUnlockSlot(message, context));
        context.setHandled(true);
    }
}
