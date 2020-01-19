package com.mrcrayfish.backpacked.util;

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
        for(int i = 0; i < inventory.getSizeInventory(); i++)
        {
            ItemStack itemstack = inventory.getStackInSlot(i);
            if(!itemstack.isEmpty())
            {
                CompoundNBT compound = new CompoundNBT();
                compound.putByte("Slot", (byte) i);
                itemstack.write(compound);
                list.add(compound);
            }
        }
        return list;
    }

    public static void loadAllItems(ListNBT list, Inventory inventory)
    {
        for(int i = 0; i < list.size(); i++)
        {
            CompoundNBT compound = list.getCompound(i);
            int slot = compound.getByte("Slot") & 255;
            if(slot < inventory.getSizeInventory())
            {
                inventory.setInventorySlotContents(slot, ItemStack.read(compound));
            }
        }
    }
}
