package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Author: MrCrayfish
 */
public record MessageSyncVillagerBackpack(int entityId)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSyncVillagerBackpack> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, MessageSyncVillagerBackpack::entityId,
        MessageSyncVillagerBackpack::new
    );

    public static void handle(MessageSyncVillagerBackpack message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleSyncVillagerBackpack(message));
        context.setHandled(true);
    }
}
