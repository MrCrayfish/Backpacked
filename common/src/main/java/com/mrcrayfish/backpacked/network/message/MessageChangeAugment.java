package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public final class MessageChangeAugment extends PlayMessage<MessageChangeAugment>
{
    private Augments.Position position;
    private ResourceLocation augmentTypeId;

    public MessageChangeAugment() {}

    public MessageChangeAugment(Augments.Position position, ResourceLocation augmentTypeId)
    {
        this.position = position;
        this.augmentTypeId = augmentTypeId;
    }

    public MessageChangeAugment(Augments.Position position, Augment<?> augment)
    {
        this(position, augment.type().id());
    }

    @Override
    public void encode(MessageChangeAugment message, FriendlyByteBuf buf)
    {
        buf.writeEnum(message.position);
        buf.writeResourceLocation(message.augmentTypeId);
    }

    @Override
    public MessageChangeAugment decode(FriendlyByteBuf buf)
    {
        Augments.Position position = buf.readEnum(Augments.Position.class);
        ResourceLocation augmentTypeId = buf.readResourceLocation();
        return new MessageChangeAugment(position, augmentTypeId);
    }

    @Override
    public void handle(MessageChangeAugment message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleChangeAugment(message, context));
        context.setHandled(true);
    }

    public Augments.Position position()
    {
        return this.position;
    }

    public ResourceLocation augmentTypeId()
    {
        return this.augmentTypeId;
    }
}
