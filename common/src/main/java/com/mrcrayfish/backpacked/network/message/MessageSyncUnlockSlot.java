package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public record MessageSyncUnlockSlot(List<Integer> unlockedSlotIndexes)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSyncUnlockSlot> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.UNSIGNED_SHORT.apply(ByteBufCodecs.list()), MessageSyncUnlockSlot::unlockedSlotIndexes,
        MessageSyncUnlockSlot::new
    );

    public static void handle(MessageSyncUnlockSlot message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleUnlockSlot(message));
        context.setHandled(true);
    }
}
