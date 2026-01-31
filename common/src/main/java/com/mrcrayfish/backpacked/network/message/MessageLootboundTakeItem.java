package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public final class MessageLootboundTakeItem extends PlayMessage<MessageLootboundTakeItem>
{
    private int entityId;
    private ItemStack stack;
    private Vec3 pos;
    private boolean sound;

    public MessageLootboundTakeItem() {}

    public MessageLootboundTakeItem(int entityId, ItemStack stack, Vec3 pos, boolean sound)
    {
        this.entityId = entityId;
        this.stack = stack;
        this.pos = pos;
        this.sound = sound;
    }

    @Override
    public void encode(MessageLootboundTakeItem message, FriendlyByteBuf buf)
    {
        buf.writeVarInt(message.entityId);
        buf.writeItem(message.stack);
        buf.writeDouble(message.pos.x);
        buf.writeDouble(message.pos.y);
        buf.writeDouble(message.pos.z);
        buf.writeBoolean(message.sound);
    }

    @Override
    public MessageLootboundTakeItem decode(FriendlyByteBuf buf)
    {
        int entityId = buf.readVarInt();
        ItemStack stack = buf.readItem();
        Vec3 pos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        boolean sound = buf.readBoolean();
        return new MessageLootboundTakeItem(entityId, stack, pos, sound);
    }

    @Override
    public void handle(MessageLootboundTakeItem message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleLootboundTakeItem(message, context));
        context.setHandled(true);
    }

    public int entityId()
    {
        return this.entityId;
    }

    public ItemStack stack()
    {
        return this.stack;
    }

    public Vec3 pos()
    {
        return this.pos;
    }

    public boolean sound()
    {
        return this.sound;
    }
}
