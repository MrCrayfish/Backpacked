package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.common.backpack.UnlockedSlots;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.function.Predicate;

public abstract class LockedContainer implements Container
{
    protected final int size;
    protected final NonNullList<ItemStack> items;

    public LockedContainer(int size)
    {
        this.size = size;
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    protected abstract UnlockedSlots getUnlockedSlots();

    @Override
    public int getContainerSize()
    {
        return this.size;
    }

    @Override
    public boolean isEmpty()
    {
        UnlockedSlots slots = this.getUnlockedSlots();
        for(int slot = 0; slot < this.size; slot++)
        {
            if(slots.isUnlocked(slot) && !this.items.get(slot).isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot)
    {
        UnlockedSlots slots = this.getUnlockedSlots();
        return slot >= 0 && slot < this.size && slots.isUnlocked(slot) ? this.items.get(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int count)
    {
        UnlockedSlots slots = this.getUnlockedSlots();
        if(slots.isUnlocked(slot))
        {
            ItemStack stack = ContainerHelper.removeItem(this.items, slot, count);
            if(!stack.isEmpty())
            {
                this.setChanged();
            }
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot)
    {
        UnlockedSlots slots = this.getUnlockedSlots();
        if(slots.isUnlocked(slot))
        {
            ItemStack stack = this.items.get(slot);
            if(!stack.isEmpty())
            {
                this.items.set(slot, ItemStack.EMPTY);
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack)
    {
        UnlockedSlots slots = this.getUnlockedSlots();
        if(slots.isUnlocked(slot))
        {
            this.items.set(slot, stack);
            stack.limitSize(this.getMaxStackSize(stack));
            this.setChanged();
        }
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack)
    {
        return this.getUnlockedSlots().isUnlocked(slot);
    }

    @Override
    public boolean canTakeItem(Container container, int slot, ItemStack stack)
    {
        return this.getUnlockedSlots().isUnlocked(slot);
    }

    @Override
    public int countItem(Item item)
    {
        int count = 0;
        UnlockedSlots slots = this.getUnlockedSlots();
        for(int slot = 0; slot < this.getContainerSize(); slot++)
        {
            if(slots.isUnlocked(slot))
            {
                ItemStack stack = this.items.get(slot);
                if(stack.is(item))
                {
                    count += stack.getCount();
                }
            }
        }
        return count;
    }

    @Override
    public boolean hasAnyMatching(Predicate<ItemStack> predicate)
    {
        UnlockedSlots slots = this.getUnlockedSlots();
        for(int slot = 0; slot < this.getContainerSize(); slot++)
        {
            if(slots.isUnlocked(slot))
            {
                ItemStack stack = this.items.get(slot);
                if(predicate.test(stack))
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean stillValid(Player player)
    {
        return true;
    }

    @Override
    public void clearContent()
    {
        this.items.clear();
        this.setChanged();
    }

    public void copyFrom(ItemContainerContents contents)
    {
        contents.copyInto(this.items);
    }

    public ItemContainerContents createContents()
    {
        return ItemContainerContents.fromItems(this.items);
    }

    public void load(CompoundTag tag, HolderLookup.Provider provider)
    {
        ContainerHelper.loadAllItems(tag, this.items, provider);
    }

    public void save(CompoundTag tag, HolderLookup.Provider provider)
    {
        ContainerHelper.saveAllItems(tag, this.items, provider);
    }
}
