package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public record MessageUnlockSlot(List<Integer> slotIndexes)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageUnlockSlot> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.UNSIGNED_SHORT.apply(ByteBufCodecs.list(255)), MessageUnlockSlot::slotIndexes,
        MessageUnlockSlot::new
    );

    public static void handle(MessageUnlockSlot message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleUnlockSlot(message, context));
        context.setHandled(true);
    }
}
