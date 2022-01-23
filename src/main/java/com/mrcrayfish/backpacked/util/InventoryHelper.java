package com.mrcrayfish.backpacked.util;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

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

    public static void loadAllItems(ListNBT list, Inventory inventory, World world, Vector3d pos)
    {
        for(int i = 0; i < list.size(); i++)
        {
            CompoundNBT compound = list.getCompound(i);
            int slot = compound.getByte("Slot") & 255;
            if(slot < inventory.getContainerSize())
            {
                inventory.setItem(slot, ItemStack.of(compound));
            }
            else if(!world.isClientSide())
            {
                ItemStack stack = ItemStack.of(compound);
                ItemEntity entity = new ItemEntity(world, pos.x, pos.y, pos.z, inventory.addItem(stack));
                entity.setDefaultPickUpDelay();
                world.addFreshEntity(entity);
            }
        }
    }

    public static void mergeInventory(Inventory source, Inventory target, World world, Vector3d pos)
    {
        for(int i = 0; i < source.getContainerSize(); i++)
        {
            if(i < target.getContainerSize())
            {
                target.setItem(i, source.getItem(i).copy());
            }
            else if(!world.isClientSide())
            {
                ItemStack stack = source.getItem(i).copy();
                ItemEntity entity = new ItemEntity(world, pos.x, pos.y, pos.z, target.addItem(stack));
                entity.setDefaultPickUpDelay();
                world.addFreshEntity(entity);
            }
        }
    }
}
