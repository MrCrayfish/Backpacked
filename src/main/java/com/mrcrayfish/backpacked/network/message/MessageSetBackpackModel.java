package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.Backpacked;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageSetBackpackModel implements IMessage<MessageSetBackpackModel>
{
    private String model;

    public MessageSetBackpackModel() {}

    public MessageSetBackpackModel(String model)
    {
        this.model = model;
    }

    @Override
    public void encode(MessageSetBackpackModel message, PacketBuffer buffer)
    {
        buffer.writeUtf(message.model);
    }

    @Override
    public MessageSetBackpackModel decode(PacketBuffer buffer)
    {
        return new MessageSetBackpackModel(buffer.readUtf());
    }

    @Override
    public void handle(MessageSetBackpackModel message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = supplier.get().getSender();
            if(player != null)
            {
                ItemStack stack = Backpacked.getBackpackStack(player);
                if(!stack.isEmpty())
                {
                    stack.getOrCreateTag().putString("BackpackModel", message.model);
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
