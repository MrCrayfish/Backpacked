package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;

public final class MessageShowEquipHint extends PlayMessage<MessageShowEquipHint> // TODO DONE
{
    public static final MessageShowEquipHint INSTANCE = new MessageShowEquipHint();

    @Override
    public void encode(MessageShowEquipHint message, FriendlyByteBuf buffer) {}

    @Override
    public MessageShowEquipHint decode(FriendlyByteBuf buffer)
    {
        return INSTANCE;
    }

    @Override
    public void handle(MessageShowEquipHint message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleMessageShowEquipHint(message, context));
        context.setHandled(true);
    }
}
