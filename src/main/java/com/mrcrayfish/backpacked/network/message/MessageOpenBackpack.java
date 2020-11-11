package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainer;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;
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
