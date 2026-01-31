package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.Navigate;
import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;

public final class MessageNavigateBackpack extends PlayMessage<MessageNavigateBackpack>
{
    private Navigate navigate;

    public MessageNavigateBackpack() {}

    public MessageNavigateBackpack(Navigate navigate)
    {
        this.navigate = navigate;
    }

    @Override
    public void encode(MessageNavigateBackpack message, FriendlyByteBuf buf)
    {
        buf.writeEnum(message.navigate);
    }

    @Override
    public MessageNavigateBackpack decode(FriendlyByteBuf buf)
    {
        return new MessageNavigateBackpack(buf.readEnum(Navigate.class));
    }

    @Override
    public void handle(MessageNavigateBackpack message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleNavigateBackpack(message, context));
        context.setHandled(true);
    }

    public Navigate navigate()
    {
        return this.navigate;
    }
}
