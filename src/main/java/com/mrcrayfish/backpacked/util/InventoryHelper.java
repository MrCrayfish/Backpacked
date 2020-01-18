package com.mrcrayfish.backpacked.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * Author: MrCrayfish
 */
public class InventoryHelper
{
    public static NBTTagList saveAllItems(NBTTagList list, IInventory inventory)
    {
        for(int i = 0; i < inventory.getSizeInventory(); ++i)
        {
            ItemStack itemstack = inventory.getStackInSlot(i);
            if(!itemstack.isEmpty())
            {
                NBTTagCompound compound = new NBTTagCompound();
                compound.setByte("Slot", (byte) i);
                itemstack.writeToNBT(compound);
                list.appendTag(compound);
            }
        }
        return list;
    }

    public static void loadAllItems(NBTTagList list, IInventory inventory)
    {
        for(int i = 0; i < list.tagCount(); ++i)
        {
            NBTTagCompound compound = list.getCompoundTagAt(i);
            int slot = compound.getByte("Slot") & 255;
            if(slot < inventory.getSizeInventory())
            {
                inventory.setInventorySlotContents(slot, new ItemStack(compound));
            }
        }
    }
}
