package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageOpenBackpack implements IMessage<MessageOpenBackpack>
{
    public static final TranslationTextComponent BACKPACK_TRANSLATION = new TranslationTextComponent("container.backpack");

    @Override
    public void encode(MessageOpenBackpack message, PacketBuffer buffer) {}

    @Override
    public MessageOpenBackpack decode(PacketBuffer buffer)
    {
        return new MessageOpenBackpack();
    }

    @Override
    public void handle(MessageOpenBackpack message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = supplier.get().getSender();
            if(player != null)
            {
                ItemStack backpack = Backpacked.getBackpackStack(player);
                if(backpack.getItem() instanceof BackpackItem)
                {
                    ((BackpackItem) backpack.getItem()).showInventory(player);
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
