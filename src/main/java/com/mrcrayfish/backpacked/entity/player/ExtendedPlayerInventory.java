package com.mrcrayfish.backpacked.entity.player;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;

import java.util.Iterator;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class ExtendedPlayerInventory extends PlayerInventory
{
    public final NonNullList<ItemStack> backpackInventory = NonNullList.withSize(1, ItemStack.EMPTY);
    private final List<NonNullList<ItemStack>> allInventories = ImmutableList.of(this.mainInventory, this.armorInventory, this.offHandInventory, this.backpackInventory);

    public ExtendedPlayerInventory(PlayerEntity player)
    {
        super(player);
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        NonNullList<ItemStack> targetInventory = null;
        for(NonNullList<ItemStack> inventory : this.allInventories)
        {
            if(index < inventory.size())
            {
                targetInventory = inventory;
                break;
            }
            index -= inventory.size();
        }
        return targetInventory != null && !targetInventory.get(index).isEmpty() ? ItemStackHelper.getAndSplit(targetInventory, index, count) : ItemStack.EMPTY;
    }

    @Override
    public void deleteStack(ItemStack stack)
    {
        for(NonNullList<ItemStack> inventory : this.allInventories)
        {
            for(int i = 0; i < inventory.size(); ++i)
            {
                if(inventory.get(i) == stack)
                {
                    inventory.set(i, ItemStack.EMPTY);
                    break;
                }
            }
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        NonNullList<ItemStack> targetInventory = null;
        for(NonNullList<ItemStack> inventory : this.allInventories)
        {
            if(index < inventory.size())
            {
                targetInventory = inventory;
                break;
            }
            index -= inventory.size();
        }

        if(targetInventory != null && !targetInventory.get(index).isEmpty())
        {
            ItemStack stack = targetInventory.get(index);
            targetInventory.set(index, ItemStack.EMPTY);
            return stack;
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        NonNullList<ItemStack> targetInventory = null;
        for(NonNullList<ItemStack> inventory : this.allInventories)
        {
            if(index < inventory.size())
            {
                targetInventory = inventory;
                break;
            }
            index -= inventory.size();
        }
        if(targetInventory != null)
        {
            targetInventory.set(index, stack);
        }
    }

    @Override
    public ItemStack getStackInSlot(int index)
    {
        List<ItemStack> list = null;
        for(NonNullList<ItemStack> inventory : this.allInventories)
        {
            if(index < inventory.size())
            {
                list = inventory;
                break;
            }
            index -= inventory.size();
        }
        return list == null ? ItemStack.EMPTY : list.get(index);
    }

    @Override
    public ListNBT write(ListNBT list)
    {
        list = super.write(list);
        for(int i = 0; i < this.backpackInventory.size(); i++)
        {
            if(!this.backpackInventory.get(i).isEmpty())
            {
                CompoundNBT compound = new CompoundNBT();
                compound.putByte("Slot", (byte) (i + 200));
                this.backpackInventory.get(i).write(compound);
                list.add(compound);
            }
        }
        return list;
    }

    @Override
    public void read(ListNBT list)
    {
        super.read(list);
        for(int i = 0; i < list.size(); ++i)
        {
            CompoundNBT compound = list.getCompound(i);
            int slot = compound.getByte("Slot") & 255;
            ItemStack stack = ItemStack.read(compound);
            if(!stack.isEmpty())
            {
                if(slot >= 200 && slot < this.backpackInventory.size() + 200)
                {
                    this.backpackInventory.set(slot - 200, stack);
                }
            }
        }
    }

    @Override
    public int getSizeInventory()
    {
        return super.getSizeInventory() + this.backpackInventory.size() + 1;
    }

    @Override
    public boolean isEmpty()
    {
        for(ItemStack stack : this.backpackInventory)
        {
            if(!stack.isEmpty())
            {
                return false;
            }
        }
        return super.isEmpty();
    }

    @Override
    public boolean hasItemStack(ItemStack targetStack)
    {
        for(NonNullList<ItemStack> inventory : this.allInventories)
        {
            Iterator iterator = inventory.iterator();
            while(true)
            {
                if(!iterator.hasNext())
                {
                    return false;
                }
                ItemStack stack = (ItemStack) iterator.next();
                if(!stack.isEmpty() && stack.isItemEqual(targetStack))
                {
                    break;
                }
            }
            return true;
        }
        return false;
    }
}
