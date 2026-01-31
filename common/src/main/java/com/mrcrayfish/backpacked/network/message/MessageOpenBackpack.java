package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Author: MrCrayfish
 */
public final class MessageOpenBackpack extends PlayMessage<MessageOpenBackpack>
{
    private int backpackIndex;

    public MessageOpenBackpack() {}

    public MessageOpenBackpack(int backpackIndex)
    {
        this.backpackIndex = backpackIndex;
    }

    @Override
    public void encode(MessageOpenBackpack message, FriendlyByteBuf buf)
    {
        buf.writeInt(message.backpackIndex);
    }

    @Override
    public MessageOpenBackpack decode(FriendlyByteBuf buf)
    {
        return new MessageOpenBackpack(buf.readInt());
    }

    @Override
    public void handle(MessageOpenBackpack message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleOpenBackpack(message, context));
        context.setHandled(true);
    }

    public int backpackIndex()
    {
        return this.backpackIndex;
    }
}
