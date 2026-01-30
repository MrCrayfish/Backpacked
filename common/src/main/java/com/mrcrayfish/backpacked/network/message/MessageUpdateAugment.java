package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;

public class MessageUpdateAugment extends PlayMessage<MessageUpdateAugment> // TODO DONE
{
    private Augments.Position position;
    private Augment<?> augment;

    public MessageUpdateAugment() {}

    public MessageUpdateAugment(Augments.Position position, Augment<?> augment)
    {
        this.position = position;
        this.augment = augment;
    }

    @Override
    public void encode(MessageUpdateAugment message, FriendlyByteBuf buf)
    {
        buf.writeEnum(message.position);
        Augment.encode(buf, message.augment);
    }

    @Override
    public MessageUpdateAugment decode(FriendlyByteBuf buf)
    {
        Augments.Position position = buf.readEnum(Augments.Position.class);
        Augment<?> augment = Augment.decode(buf);
        return new MessageUpdateAugment(position, augment);
    }

    @Override
    public void handle(MessageUpdateAugment message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleUpdateAugment(message, context));
        context.setHandled(true);
    }

    public Augments.Position position()
    {
        return this.position;
    }

    public Augment<?> augment()
    {
        return this.augment;
    }
}
