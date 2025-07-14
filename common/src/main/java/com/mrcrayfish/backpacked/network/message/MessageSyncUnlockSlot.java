package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Author: MrCrayfish
 */
public record MessageSyncUnlockSlot(int slot)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSyncUnlockSlot> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, MessageSyncUnlockSlot::slot,
        MessageSyncUnlockSlot::new
    );

    public static void handle(MessageSyncUnlockSlot message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleUnlockSlot(message));
        context.setHandled(true);
    }
}
