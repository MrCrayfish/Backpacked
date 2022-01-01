package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.common.BackpackModelProperty;
import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.network.PacketHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageCustomiseBackpack implements IMessage<MessageCustomiseBackpack>
{
    private String model;
    private boolean showWithElytra;
    private boolean showEffects;

    public MessageCustomiseBackpack() {}

    public MessageCustomiseBackpack(String model, boolean showWithElytra, boolean showEffects)
    {
        this.model = model;
        this.showWithElytra = showWithElytra;
        this.showEffects = showEffects;
    }

    @Override
    public void encode(MessageCustomiseBackpack message, PacketBuffer buffer)
    {
        buffer.writeUtf(message.model);
        buffer.writeBoolean(message.showWithElytra);
        buffer.writeBoolean(message.showEffects);
    }

    @Override
    public MessageCustomiseBackpack decode(PacketBuffer buffer)
    {
        return new MessageCustomiseBackpack(buffer.readUtf(), buffer.readBoolean(), buffer.readBoolean());
    }

    @Override
    public void handle(MessageCustomiseBackpack message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = supplier.get().getSender();
            if(player != null)
            {
                ItemStack stack = Backpacked.getBackpackStack(player);
                if(!stack.isEmpty())
                {
                    CompoundNBT tag = stack.getOrCreateTag();
                    tag.putString("BackpackModel", message.model);
                    tag.putBoolean(BackpackModelProperty.SHOW_WITH_ELYTRA.getTagName(), message.showWithElytra);
                    tag.putBoolean(BackpackModelProperty.SHOW_EFFECTS.getTagName(), message.showEffects);

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
