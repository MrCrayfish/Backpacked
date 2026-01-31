package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;

public final class MessagePickpocketBackpack extends PlayMessage<MessagePickpocketBackpack>
{
    private int entityId;

    public MessagePickpocketBackpack() {}

    public MessagePickpocketBackpack(int entityId)
    {
        this.entityId = entityId;
    }

    @Override
    public void encode(MessagePickpocketBackpack message, FriendlyByteBuf buf)
    {
        buf.writeVarInt(this.entityId);
    }

    @Override
    public MessagePickpocketBackpack decode(FriendlyByteBuf buf)
    {
        return new MessagePickpocketBackpack(buf.readVarInt());
    }

    @Override
    public void handle(MessagePickpocketBackpack message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handlePickpocketBackpack(message, context));
        context.setHandled(true);
    }

    public int entityId()
    {
        return this.entityId;
    }
}
