package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.ItemSorting;
import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;

public final class MessageSortBackpack extends PlayMessage<MessageSortBackpack>
{
    private ItemSorting sorting;

    public MessageSortBackpack() {}

    public MessageSortBackpack(ItemSorting sorting)
    {
        this.sorting = sorting;
    }

    @Override
    public void encode(MessageSortBackpack message, FriendlyByteBuf buf)
    {
        buf.writeEnum(message.sorting);
    }

    @Override
    public MessageSortBackpack decode(FriendlyByteBuf buf)
    {
        return new MessageSortBackpack(buf.readEnum(ItemSorting.class));
    }

    @Override
    public void handle(MessageSortBackpack message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleSortBackpack(message, context));
        context.setHandled(true);
    }

    public ItemSorting sorting()
    {
        return this.sorting;
    }
}
