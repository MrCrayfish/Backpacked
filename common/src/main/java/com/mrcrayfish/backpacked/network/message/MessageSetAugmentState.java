package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;

public final class MessageSetAugmentState extends PlayMessage<MessageSetAugmentState>
{
    private Augments.Position position;
    private boolean state;

    public MessageSetAugmentState() {}

    public MessageSetAugmentState(Augments.Position position, boolean state)
    {
        this.position = position;
        this.state = state;
    }

    @Override
    public void encode(MessageSetAugmentState message, FriendlyByteBuf buf)
    {
        buf.writeEnum(message.position);
        buf.writeBoolean(message.state);
    }

    @Override
    public MessageSetAugmentState decode(FriendlyByteBuf buf)
    {
        Augments.Position position = buf.readEnum(Augments.Position.class);
        boolean state = buf.readBoolean();
        return new MessageSetAugmentState(position, state);
    }

    @Override
    public void handle(MessageSetAugmentState message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleSetAugmentState(message, context));
        context.setHandled(true);
    }

    public Augments.Position position()
    {
        return this.position;
    }

    public boolean state()
    {
        return this.state;
    }
}
