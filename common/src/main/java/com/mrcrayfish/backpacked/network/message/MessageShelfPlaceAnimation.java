package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public final class MessageShelfPlaceAnimation extends PlayMessage<MessageShelfPlaceAnimation>
{
    private BlockPos pos;

    public MessageShelfPlaceAnimation() {}

    public MessageShelfPlaceAnimation(BlockPos pos)
    {
        this.pos = pos;
    }

    @Override
    public void encode(MessageShelfPlaceAnimation message, FriendlyByteBuf buf)
    {
        buf.writeBlockPos(message.pos);
    }

    @Override
    public MessageShelfPlaceAnimation decode(FriendlyByteBuf buf)
    {
        return new MessageShelfPlaceAnimation(buf.readBlockPos());
    }

    @Override
    public void handle(MessageShelfPlaceAnimation message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleMessageShelfPlaceAnimation(message, context));
        context.setHandled(true);
    }

    public BlockPos pos()
    {
        return this.pos;
    }
}
