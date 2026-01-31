package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;

public final class MessageRenameBackpack extends PlayMessage<MessageRenameBackpack>
{
    private String value;

    public MessageRenameBackpack() {}

    public MessageRenameBackpack(String value)
    {
        this.value = value;
    }

    @Override
    public void encode(MessageRenameBackpack message, FriendlyByteBuf buf)
    {
        buf.writeUtf(message.value);
    }

    @Override
    public MessageRenameBackpack decode(FriendlyByteBuf buffer)
    {
        return new MessageRenameBackpack(buffer.readUtf());
    }

    @Override
    public void handle(MessageRenameBackpack message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleRenameBackpack(message, context));
        context.setHandled(true);
    }

    public String value()
    {
        return this.value;
    }
}
