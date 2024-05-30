package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class MessageUnlockBackpack extends PlayMessage<MessageUnlockBackpack>
{
    private ResourceLocation id;

    public MessageUnlockBackpack() {}

    public MessageUnlockBackpack(ResourceLocation id)
    {
        this.id = id;
    }

    @Override
    public void encode(MessageUnlockBackpack message, FriendlyByteBuf buffer)
    {
        buffer.writeResourceLocation(message.id);
    }

    @Override
    public MessageUnlockBackpack decode(FriendlyByteBuf buffer)
    {
        return new MessageUnlockBackpack(buffer.readResourceLocation());
    }

    @Override
    public void handle(MessageUnlockBackpack message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleUnlockBackpack(message));
        context.setHandled(true);
    }

    public ResourceLocation cosmeticId()
    {
        return this.id;
    }
}
