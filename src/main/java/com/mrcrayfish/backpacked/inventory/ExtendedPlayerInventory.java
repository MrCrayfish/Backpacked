package com.mrcrayfish.backpacked.inventory;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.backpacked.Config;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Iterator;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class ExtendedPlayerInventory extends Inventory
{
    public final NonNullList<ItemStack> backpackArray = NonNullList.withSize(1, ItemStack.EMPTY);
    public final NonNullList<ItemStack> backpackInventory = NonNullList.withSize(1, ItemStack.EMPTY);
    private final List<NonNullList<ItemStack>> allInventories = ImmutableList.of(this.items, this.armor, this.offhand, this.backpackInventory);

    public ExtendedPlayerInventory(Player player)
    {
        super(player);
    }

    public NonNullList<ItemStack> getBackpackItems()
    {
        return this.backpackInventory;
    }

    public void copyBackpack(ExtendedPlayerInventory inventory)
    {
        this.backpackInventory.set(0, inventory.backpackInventory.get(0).copy());
    }

    @Override
    public ItemStack removeItem(int index, int count)
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
        return targetInventory != null && !targetInventory.get(index).isEmpty() ? ContainerHelper.removeItem(targetInventory, index, count) : ItemStack.EMPTY;
    }

    @Override
    public void removeItem(ItemStack stack)
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
    public ItemStack removeItemNoUpdate(int index)
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
    public void setItem(int index, ItemStack stack)
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
    public ItemStack getItem(int index)
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
    public ListTag save(ListTag list)
    {
        list = super.save(list);
        for(int i = 0; i < this.backpackInventory.size(); i++)
        {
            if(!this.backpackInventory.get(i).isEmpty())
            {
                CompoundTag compound = new CompoundTag();
                compound.putByte("Slot", (byte) (i + 200));
                this.backpackInventory.get(i).save(compound);
                list.add(compound);
            }
        }
        return list;
    }

    @Override
    public void load(ListTag list)
    {
        super.load(list);
        for(int i = 0; i < list.size(); ++i)
        {
            CompoundTag compound = list.getCompound(i);
            int slot = compound.getByte("Slot") & 255;
            ItemStack stack = ItemStack.of(compound);
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
    public int getContainerSize()
    {
        return super.getContainerSize() + this.backpackInventory.size();
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
    public boolean contains(ItemStack targetStack)
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
                if(!stack.isEmpty() && stack.sameItem(targetStack))
                {
                    break;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void clearContent()
    {
        for(List<ItemStack> list : this.allInventories)
        {
            list.clear();
        }
    }

    @Override
    public void dropAll()
    {
        if(Config.COMMON.keepBackpackOnDeath.get())
        {
            super.dropAll();
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
                        this.player.drop(itemstack, true, false);
                        list.set(i, ItemStack.EMPTY);
                    }
                }
            }
        }
    }
}
