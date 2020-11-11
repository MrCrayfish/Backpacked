package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.proxy.ClientProxy;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageUpdateBackpack implements IMessage<MessageUpdateBackpack>
{
    private int entityId;
    private ItemStack backpack;

    public MessageUpdateBackpack()
    {
    }

    public MessageUpdateBackpack(int entityId, ItemStack backpack)
    {
        this.entityId = entityId;
        this.backpack = backpack;
    }

    @Override
    public void encode(MessageUpdateBackpack message, PacketBuffer buffer)
    {
        buffer.writeInt(message.entityId);
        this.writeItemStackNoTag(buffer, message.backpack);
    }

    @Override
    public MessageUpdateBackpack decode(PacketBuffer buffer)
    {
        return new MessageUpdateBackpack(buffer.readInt(), buffer.readItemStack());
    }

    @Override
    public void handle(MessageUpdateBackpack message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ClientProxy.setPlayerBackpack(message.entityId, message.backpack);
        });
        supplier.get().setPacketHandled(true);
    }

    private void writeItemStackNoTag(PacketBuffer buffer, ItemStack stack)
    {
        boolean empty = stack.isEmpty();
        buffer.writeBoolean(!empty);
        if(!empty)
        {
            Item item = stack.getItem();
            buffer.writeVarInt(Item.getIdFromItem(item));
            buffer.writeByte(stack.getCount());
            buffer.writeCompoundTag(null);
        }
    }
}
