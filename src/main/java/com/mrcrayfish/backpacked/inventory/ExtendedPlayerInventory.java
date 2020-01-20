package com.mrcrayfish.backpacked.inventory;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.backpacked.Backpacked;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;

import java.util.Iterator;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class ExtendedPlayerInventory extends InventoryPlayer
{
    public final NonNullList<ItemStack> backpackArray = NonNullList.withSize(1, ItemStack.EMPTY);
    public final NonNullList<ItemStack> backpackInventory = NonNullList.withSize(1, ItemStack.EMPTY);
    private final List<NonNullList<ItemStack>> allInventories = ImmutableList.of(this.mainInventory, this.armorInventory, this.offHandInventory, this.backpackInventory);

    public ExtendedPlayerInventory(EntityPlayer player)
    {
        super(player);
    }

    public NonNullList<ItemStack> getBackpackItems()
    {
        return backpackInventory;
    }

    public void copyBackpack(ExtendedPlayerInventory inventory)
    {
        this.backpackInventory.set(0, inventory.backpackInventory.get(0));
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
    public NBTTagList writeToNBT(NBTTagList tagList)
    {
        tagList = super.writeToNBT(tagList);
        for(int i = 0; i < this.backpackInventory.size(); i++)
        {
            if(!this.backpackInventory.get(i).isEmpty())
            {
                NBTTagCompound compound = new NBTTagCompound();
                compound.setByte("Slot", (byte) (i + 200));
                this.backpackInventory.get(i).writeToNBT(compound);
                tagList.appendTag(compound);
            }
        }
        return tagList;
    }

    @Override
    public void readFromNBT(NBTTagList tagList)
    {
        super.readFromNBT(tagList);
        for(int i = 0; i < tagList.tagCount(); ++i)
        {
            NBTTagCompound compound = tagList.getCompoundTagAt(i);
            int slot = compound.getByte("Slot") & 255;
            if(slot >= 200 && slot < this.backpackInventory.size() + 200)
            {
                ItemStack stack = new ItemStack(compound);
                if(!stack.isEmpty())
                {
                    this.backpackInventory.set(slot - 200, stack);
                }
            }
        }
    }

    @Override
    public int getSizeInventory()
    {
        return super.getSizeInventory() + this.backpackInventory.size();
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

    @Override
    public void clear()
    {
        for(List<ItemStack> list : this.allInventories)
        {
            list.clear();
        }
    }

    @Override
    public void dropAllItems()
    {
        if(Backpacked.keepBackpackOnDeath)
        {
            super.dropAllItems();
        }
        else
        {
            for(List<ItemStack> list : this.allInventories)
            {
                for(int i = 0; i < list.size(); ++i)
                {
                    ItemStack itemstack = list.get(i);
                    if(!itemstack.isEmpty())
                    {
                        this.player.dropItem(itemstack, true, false);
                        list.set(i, ItemStack.EMPTY);
                    }
                }
            }
        }
    }
}
