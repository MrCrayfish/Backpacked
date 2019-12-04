package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.capability.ICurioItemHandler;

import java.util.concurrent.atomic.AtomicReference;
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
                if(!Backpacked.getBackpackStack(player).isEmpty())
                {
                    player.openContainer(new SimpleNamedContainerProvider((id, playerInventory, entity) ->
                        new BackpackContainer(id, player.inventory, new BackpackInventory()), BACKPACK_TRANSLATION));
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
