package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;

public final class MessageResponseShelfKey extends PlayMessage<MessageResponseShelfKey> // TODO DONE
{
    private int backpackIndex;
    private Augments.Position position;
    private boolean valid;

    public MessageResponseShelfKey() {}

    public MessageResponseShelfKey(int backpackIndex, Augments.Position position, boolean valid)
    {
        this.backpackIndex = backpackIndex;
        this.position = position;
        this.valid = valid;
    }

    @Override
    public void encode(MessageResponseShelfKey message, FriendlyByteBuf buf)
    {
        buf.writeVarInt(message.backpackIndex);
        buf.writeEnum(message.position);
        buf.writeBoolean(message.valid);
    }

    @Override
    public MessageResponseShelfKey decode(FriendlyByteBuf buf)
    {
        int backpackIndex = buf.readVarInt();
        Augments.Position position = buf.readEnum(Augments.Position.class);
        boolean valid = buf.readBoolean();
        return new MessageResponseShelfKey(backpackIndex, position, valid);
    }

    @Override
    public void handle(MessageResponseShelfKey message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleMessageResponseShelfKey(message, context));
        context.setHandled(true);
    }

    public int backpackIndex()
    {
        return this.backpackIndex;
    }

    public Augments.Position position()
    {
        return this.position;
    }

    public boolean valid()
    {
        return this.valid;
    }
}
