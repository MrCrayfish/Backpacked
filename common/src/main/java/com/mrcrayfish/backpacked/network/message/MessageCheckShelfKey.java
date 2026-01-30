package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;

public final class MessageCheckShelfKey extends PlayMessage<MessageCheckShelfKey> // TODO DONE
{
    private int backpackIndex;
    private Augments.Position position;

    public MessageCheckShelfKey() {}

    public MessageCheckShelfKey(int backpackIndex, Augments.Position position)
    {
        this.backpackIndex = backpackIndex;
        this.position = position;
    }

    @Override
    public void encode(MessageCheckShelfKey message, FriendlyByteBuf buf)
    {
        buf.writeInt(message.backpackIndex);
        buf.writeEnum(message.position);
    }

    @Override
    public MessageCheckShelfKey decode(FriendlyByteBuf buf)
    {
        int backpackIndex = buf.readInt();
        Augments.Position position = buf.readEnum(Augments.Position.class);
        return new MessageCheckShelfKey(backpackIndex, position);
    }

    @Override
    public void handle(MessageCheckShelfKey message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleMessageCheckShelfKey(message, context));
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
}
