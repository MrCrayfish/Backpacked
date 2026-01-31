package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public final class MessageFarmhandPlant extends PlayMessage<MessageFarmhandPlant>
{
    private ItemStack stack;
    private int entityId;
    private BlockPos pos;

    public MessageFarmhandPlant() {}

    public MessageFarmhandPlant(ItemStack stack, int entityId, BlockPos pos)
    {
        this.stack = stack;
        this.entityId = entityId;
        this.pos = pos;
    }

    @Override
    public void encode(MessageFarmhandPlant message, FriendlyByteBuf buf)
    {
        buf.writeItem(message.stack);
        buf.writeInt(message.entityId);
        buf.writeBlockPos(message.pos);
    }

    @Override
    public MessageFarmhandPlant decode(FriendlyByteBuf buf)
    {
        ItemStack stack = buf.readItem();
        int entityId = buf.readInt();
        BlockPos pos = buf.readBlockPos();
        return new MessageFarmhandPlant(stack, entityId, pos);
    }

    @Override
    public void handle(MessageFarmhandPlant message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleFarmhandPlant(message, context));
        context.setHandled(true);
    }

    public ItemStack stack()
    {
        return this.stack;
    }

    public int entityId()
    {
        return this.entityId;
    }

    public BlockPos pos()
    {
        return this.pos;
    }
}
