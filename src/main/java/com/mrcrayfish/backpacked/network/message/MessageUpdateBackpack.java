package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.BackpackModelProperty;
import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

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
    public void encode(MessageUpdateBackpack message, FriendlyByteBuf buffer)
    {
        buffer.writeInt(message.entityId);
        this.writeBackpackStack(buffer, message.backpack);
    }

    @Override
    public MessageUpdateBackpack decode(FriendlyByteBuf buffer)
    {
        return new MessageUpdateBackpack(buffer.readInt(), buffer.readItem());
    }

    @Override
    public void handle(MessageUpdateBackpack message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> ClientPlayHandler.handleUpdateBackpack(message));
        supplier.get().setPacketHandled(true);
    }

    private void writeBackpackStack(FriendlyByteBuf buffer, ItemStack stack)
    {
        boolean empty = stack.isEmpty();
        buffer.writeBoolean(!empty);
        if(!empty)
        {
            Item item = stack.getItem();
            buffer.writeVarInt(Item.getId(item));
            buffer.writeByte(stack.getCount());
            CompoundTag realTag = stack.getOrCreateTag();
            CompoundTag tag = new CompoundTag();
            tag.putString("BackpackModel", realTag.getString("BackpackModel"));
            for(BackpackModelProperty property : BackpackModelProperty.values())
            {
                String tagName = property.getTagName();
                boolean value = realTag.contains(tagName, Tag.TAG_BYTE) ? realTag.getBoolean(tagName) : property.getDefaultValue();
                tag.putBoolean(tagName, value);
            }
            buffer.writeNbt(tag);
        }
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public ItemStack getBackpack()
    {
        return this.backpack;
    }
}
