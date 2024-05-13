package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Author: MrCrayfish
 */
public record MessageSyncVillagerBackpack(int entityId)
{
    public static void encode(MessageSyncVillagerBackpack message, FriendlyByteBuf buffer)
    {
        buffer.writeInt(message.entityId);
    }

    public static MessageSyncVillagerBackpack decode(FriendlyByteBuf buffer)
    {
        return new MessageSyncVillagerBackpack(buffer.readInt());
    }

    public static void handle(MessageSyncVillagerBackpack message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleSyncVillagerBackpack(message));
        context.setHandled(true);
    }
}
