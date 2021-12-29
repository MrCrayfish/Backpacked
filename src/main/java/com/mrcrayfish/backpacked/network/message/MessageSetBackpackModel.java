package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.network.PacketHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

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

                    if(Backpacked.isCuriosLoaded())
                        return;

                    if(player.inventory instanceof ExtendedPlayerInventory)
                    {
                        ItemStack backpack = ((ExtendedPlayerInventory) player.inventory).getBackpackItems().get(0);
                        if(!backpack.isEmpty() && backpack.getItem() instanceof BackpackItem)
                        {
                            PacketHandler.instance.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new MessageUpdateBackpack(player.getId(), backpack));
                        }
                    }
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
