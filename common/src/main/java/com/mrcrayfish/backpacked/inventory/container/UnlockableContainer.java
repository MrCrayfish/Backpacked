package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
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

public abstract class UnlockableContainer implements Container
{
    protected final int size;
    protected final NonNullList<ItemStack> items;

    public UnlockableContainer(int size)
    {
        this.size = size;
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    protected abstract UnlockableSlots getUnlockableSlots();

    @Override
    public int getContainerSize()
    {
        return this.size;
    }

    @Override
    public boolean isEmpty()
    {
        UnlockableSlots slots = this.getUnlockableSlots();
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
        UnlockableSlots slots = this.getUnlockableSlots();
        return slot >= 0 && slot < this.size && slots.isUnlocked(slot) ? this.items.get(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int count)
    {
        UnlockableSlots slots = this.getUnlockableSlots();
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
        UnlockableSlots slots = this.getUnlockableSlots();
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
        UnlockableSlots slots = this.getUnlockableSlots();
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
        return this.getUnlockableSlots().isUnlocked(slot);
    }

    @Override
    public boolean canTakeItem(Container container, int slot, ItemStack stack)
    {
        return this.getUnlockableSlots().isUnlocked(slot);
    }

    @Override
    public int countItem(Item item)
    {
        int count = 0;
        UnlockableSlots slots = this.getUnlockableSlots();
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
        UnlockableSlots slots = this.getUnlockableSlots();
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

    public ItemStack addItem(ItemStack stack)
    {
        if(!stack.isEmpty())
        {
            ItemStack copy = stack.copy();
            this.mergeIntoExistingStacks(copy);
            if(!copy.isEmpty())
            {
                this.moveStackIntoFirstEmptyUnlockedSlot(copy);
            }
            return copy.isEmpty() ? ItemStack.EMPTY : copy;
        }
        return ItemStack.EMPTY;
    }

    protected void mergeIntoExistingStacks(ItemStack stack)
    {
        for(int i = 0; i < this.size; i++)
        {
            ItemStack slotStack = this.getItem(i);
            if(!ItemStack.isSameItemSameComponents(slotStack, stack))
                continue;

            int maxStackSize = this.getMaxStackSize(slotStack);
            int growSize = Math.min(stack.getCount(), maxStackSize - slotStack.getCount());
            if(growSize <= 0)
                continue;

            slotStack.grow(growSize);
            stack.shrink(growSize);
            this.setChanged();
        }
    }

    protected void moveStackIntoFirstEmptyUnlockedSlot(ItemStack stack)
    {
        UnlockableSlots slots = this.getUnlockableSlots();
        for(int i = 0; i < this.size; i++)
        {
            ItemStack slotStack = this.getItem(i);
            if(slots.isUnlocked(i) && slotStack.isEmpty() && this.canPlaceItem(i, stack))
            {
                this.setItem(i, stack.copyAndClear());
                break;
            }
        }
    }
}
