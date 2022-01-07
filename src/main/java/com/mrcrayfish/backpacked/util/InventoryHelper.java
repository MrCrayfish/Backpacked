package com.mrcrayfish.backpacked.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

/**
 * Author: MrCrayfish
 */
public class InventoryHelper
{
    public static ListNBT saveAllItems(ListNBT list, Inventory inventory)
    {
        for(int i = 0; i < inventory.getContainerSize(); ++i)
        {
            ItemStack itemstack = inventory.getItem(i);
            if(!itemstack.isEmpty())
            {
                CompoundNBT compound = new CompoundNBT();
                compound.putByte("Slot", (byte) i);
                itemstack.save(compound);
                list.add(compound);
            }
        }
        return list;
    }

    public static void loadAllItems(ListNBT list, Inventory inventory, PlayerEntity player)
    {
        for(int i = 0; i < list.size(); i++)
        {
            CompoundNBT compound = list.getCompound(i);
            int slot = compound.getByte("Slot") & 255;
            if(slot < inventory.getContainerSize())
            {
                inventory.setItem(slot, ItemStack.of(compound));
            }
            else if(player instanceof ServerPlayerEntity)
            {
                ItemStack stack = ItemStack.of(compound);
                player.spawnAtLocation(inventory.addItem(stack));
            }
        }
    }
}
