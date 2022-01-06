package com.mrcrayfish.backpacked.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class InventoryHelper
{
    public static ListTag saveAllItems(ListTag list, SimpleContainer inventory)
    {
        for(int i = 0; i < inventory.getContainerSize(); ++i)
        {
            ItemStack itemstack = inventory.getItem(i);
            if(!itemstack.isEmpty())
            {
                CompoundTag compound = new CompoundTag();
                compound.putByte("Slot", (byte) i);
                itemstack.save(compound);
                list.add(compound);
            }
        }
        return list;
    }

    public static void loadAllItems(ListTag list, SimpleContainer inventory)
    {
        for(int i = 0; i < list.size(); i++)
        {
            CompoundTag compound = list.getCompound(i);
            int slot = compound.getByte("Slot") & 255;
            if(slot < inventory.getContainerSize())
            {
                inventory.setItem(slot, ItemStack.of(compound));
            }
        }
    }
}
