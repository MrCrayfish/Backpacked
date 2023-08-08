package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Author: MrCrayfish
 */
public class MessageEntityBackpack extends PlayMessage<MessageEntityBackpack>
{
    private int entityId;

    public MessageEntityBackpack() {}

    public MessageEntityBackpack(int entityId)
    {
        this.entityId = entityId;
    }

    @Override
    public void encode(MessageEntityBackpack message, FriendlyByteBuf buffer)
    {
        buffer.writeInt(message.entityId);
    }

    @Override
    public MessageEntityBackpack decode(FriendlyByteBuf buffer)
    {
        return new MessageEntityBackpack(buffer.readInt());
    }

    @Override
    public void handle(MessageEntityBackpack message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleEntityBackpack(message, context.getPlayer()));
        context.setHandled(true);
    }

    public int getEntityId()
    {
        return this.entityId;
    }
}
