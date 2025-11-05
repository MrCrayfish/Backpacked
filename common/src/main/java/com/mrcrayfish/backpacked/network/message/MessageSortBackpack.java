package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.ItemSorting;
import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MessageSortBackpack(ItemSorting sorting)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSortBackpack> STREAM_CODEC = StreamCodec.composite(
        ItemSorting.STREAM_CODEC, MessageSortBackpack::sorting,
        MessageSortBackpack::new
    );

    public static void handle(MessageSortBackpack message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleSortBackpack(message, context));
        context.setHandled(true);
    }
}
