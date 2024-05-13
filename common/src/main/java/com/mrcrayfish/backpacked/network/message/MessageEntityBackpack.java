package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Author: MrCrayfish
 */
public record MessageEntityBackpack(int entityId)
{
    public static void encode(MessageEntityBackpack message, FriendlyByteBuf buffer)
    {
        buffer.writeInt(message.entityId);
    }

    public static MessageEntityBackpack decode(FriendlyByteBuf buffer)
    {
        return new MessageEntityBackpack(buffer.readInt());
    }

    public static void handle(MessageEntityBackpack message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleEntityBackpack(message, context));
        context.setHandled(true);
    }
}
