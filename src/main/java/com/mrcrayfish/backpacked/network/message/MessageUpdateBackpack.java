package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.BackpackModelProperty;
import com.mrcrayfish.backpacked.proxy.ClientProxy;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageUpdateBackpack implements IMessage<MessageUpdateBackpack>
{
    private int entityId;
    private ItemStack backpack;

    public MessageUpdateBackpack() {}

    public MessageUpdateBackpack(int entityId, ItemStack backpack)
    {
        this.entityId = entityId;
        this.backpack = backpack;
    }

    @Override
    public void encode(MessageUpdateBackpack message, PacketBuffer buffer)
    {
        buffer.writeInt(message.entityId);
        this.writeBackpackStack(buffer, message.backpack);
    }

    @Override
    public MessageUpdateBackpack decode(PacketBuffer buffer)
    {
        return new MessageUpdateBackpack(buffer.readInt(), buffer.readItem());
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

    private void writeBackpackStack(PacketBuffer buffer, ItemStack stack)
    {
        boolean empty = stack.isEmpty();
        buffer.writeBoolean(!empty);
        if(!empty)
        {
            Item item = stack.getItem();
            buffer.writeVarInt(Item.getId(item));
            buffer.writeByte(stack.getCount());
            CompoundNBT realTag = stack.getOrCreateTag();
            CompoundNBT tag = new CompoundNBT();
            tag.putString("BackpackModel", realTag.getString("BackpackModel"));
            for(BackpackModelProperty property : BackpackModelProperty.values())
            {
                String tagName = property.getTagName();
                boolean value = realTag.contains(tagName, Constants.NBT.TAG_BYTE) ? realTag.getBoolean(tagName) : property.getDefaultValue();
                tag.putBoolean(tagName, value);
            }
            buffer.writeNbt(tag);
        }
    }
}
