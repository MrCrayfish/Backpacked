package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;

public final class MessageSyncAugmentChange extends PlayMessage<MessageSyncAugmentChange> // TODO DONE
{
    private Augments.Position position;
    private Augment<?> augment;

    public MessageSyncAugmentChange() {}

    public MessageSyncAugmentChange(Augments.Position position, Augment<?> augment)
    {
        this.position = position;
        this.augment = augment;
    }

    @Override
    public void encode(MessageSyncAugmentChange message, FriendlyByteBuf buf)
    {
        buf.writeEnum(message.position);
        Augment.encode(buf, message.augment);
    }

    @Override
    public MessageSyncAugmentChange decode(FriendlyByteBuf buf)
    {
        Augments.Position position = buf.readEnum(Augments.Position.class);
        Augment<?> augment = Augment.decode(buf);
        return new MessageSyncAugmentChange(position, augment);
    }

    @Override
    public void handle(MessageSyncAugmentChange message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleSyncAugmentChange(message));
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
