package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
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
            if(player != null && player.inventory instanceof ExtendedPlayerInventory)
            {
                ExtendedPlayerInventory inventory = (ExtendedPlayerInventory) player.inventory;
                if(!inventory.getBackpackItems().get(0).isEmpty())
                {
                    player.openContainer(new SimpleNamedContainerProvider((id, playerInventory, entity) -> {
                        return new ChestContainer(ContainerType.GENERIC_9X1, id, player.inventory, inventory.getBackpackInventory(), 1);
                    }, BACKPACK_TRANSLATION));
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
