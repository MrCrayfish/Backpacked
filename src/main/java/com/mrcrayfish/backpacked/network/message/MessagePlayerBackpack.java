package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainer;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.util.PickpocketUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessagePlayerBackpack implements IMessage<MessagePlayerBackpack>
{
    private int entityId;

    public MessagePlayerBackpack() {}

    public MessagePlayerBackpack(int entityId)
    {
        this.entityId = entityId;
    }

    @Override
    public void encode(MessagePlayerBackpack message, PacketBuffer buffer)
    {
        buffer.writeInt(message.entityId);
    }

    @Override
    public MessagePlayerBackpack decode(PacketBuffer buffer)
    {
        return new MessagePlayerBackpack(buffer.readInt());
    }

    @Override
    public void handle(MessagePlayerBackpack message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            if(!Config.SERVER.pickpocketBackpacks.get())
                return;

            ServerPlayerEntity player = supplier.get().getSender();
            if(player == null)
                return;

            Entity entity = player.level.getEntity(message.entityId);
            if(!(entity instanceof PlayerEntity))
                return;

            PlayerEntity otherPlayer = (PlayerEntity) entity;
            if(!PickpocketUtil.canSeeBackpack(otherPlayer, player))
                return;

            ItemStack backpack = Backpacked.getBackpackStack(otherPlayer);
            if(!backpack.isEmpty())
            {
                BackpackInventory backpackInventory = ((BackpackedInventoryAccess) otherPlayer).getBackpackedInventory();
                if(backpackInventory == null)
                    return;
                ITextComponent title = backpack.hasCustomHoverName() ? backpack.getHoverName() : BackpackItem.BACKPACK_TRANSLATION;
                int rows = ((BackpackItem) backpack.getItem()).getRowCount();
                NetworkHooks.openGui(player, new SimpleNamedContainerProvider((id, playerInventory, entity1) -> {
                    return new BackpackContainer(id, player.inventory, backpackInventory, rows);
                }, title), buffer -> buffer.writeVarInt(rows));
                otherPlayer.displayClientMessage(new TranslationTextComponent("message.backpacked.player_opened"), true);
                player.level.playSound(player, otherPlayer.getX(), otherPlayer.getY() + 1.0, otherPlayer.getZ(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 0.75F, 1.0F);
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
