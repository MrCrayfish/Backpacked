package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Author: MrCrayfish
 */
public class MessageSyncVillagerBackpack extends PlayMessage<MessageSyncVillagerBackpack>
{
    private int entityId;

    public MessageSyncVillagerBackpack() {}

    public MessageSyncVillagerBackpack(int entityId)
    {
        this.entityId = entityId;
    }

    @Override
    public void encode(MessageSyncVillagerBackpack message, FriendlyByteBuf buffer)
    {
        buffer.writeInt(message.entityId);
    }

    @Override
    public MessageSyncVillagerBackpack decode(FriendlyByteBuf buffer)
    {
        return new MessageSyncVillagerBackpack(buffer.readInt());
    }

    @Override
    public void handle(MessageSyncVillagerBackpack message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleSyncVillagerBackpack(message));
        context.setHandled(true);
    }

    public int entityId()
    {
        return this.entityId;
    }
}
