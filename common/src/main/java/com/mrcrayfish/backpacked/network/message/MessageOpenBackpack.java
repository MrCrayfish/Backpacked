package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Author: MrCrayfish
 */
public record MessageOpenBackpack()
{
    public static void encode(MessageOpenBackpack message, FriendlyByteBuf buffer) {}

    public static MessageOpenBackpack decode(FriendlyByteBuf buffer)
    {
        return new MessageOpenBackpack();
    }

    public static void handle(MessageOpenBackpack message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleOpenBackpack(message, context));
        context.setHandled(true);
    }
}
