package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public record MessageUnlockBackpack(ResourceLocation cosmeticId)
{
    public static void encode(MessageUnlockBackpack message, FriendlyByteBuf buffer)
    {
        buffer.writeResourceLocation(message.cosmeticId);
    }

    public static MessageUnlockBackpack decode(FriendlyByteBuf buffer)
    {
        return new MessageUnlockBackpack(buffer.readResourceLocation());
    }

    public static void handle(MessageUnlockBackpack message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleUnlockBackpack(message));
        context.setHandled(true);
    }
}
