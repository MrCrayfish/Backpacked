package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.core.ModContainers;
import com.mrcrayfish.backpacked.inventory.container.slot.BackpackSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class BackpackContainer extends Container
{
    private final IInventory backpackInventory;
    private final int rows;
    private final boolean owner;

    public BackpackContainer(int id, PlayerInventory playerInventory, int rows, boolean owner)
    {
        this(id, playerInventory, new Inventory(9 * rows), rows, owner);
    }

    public BackpackContainer(int id, PlayerInventory playerInventory, IInventory backpackInventory, int rows, boolean owner)
    {
        super(ModContainers.BACKPACK.get(), id);
        checkContainerSize(backpackInventory, rows * 9);
        this.backpackInventory = backpackInventory;
        this.rows = rows;
        this.owner = owner;
        backpackInventory.startOpen(playerInventory.player);
        int offset = (this.rows - 4) * 18;

        for(int j = 0; j < rows; j++)
        {
            for(int i = 0; i < 9; ++i)
            {
                this.addSlot(new BackpackSlot(backpackInventory, i + j * 9, 8 + i * 18, 18 + j * 18));
            }
        }

        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 103 + i * 18 + offset));
            }
        }

        for(int i = 0; i < 9; i++)
        {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 161 + offset));
        }
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn)
    {
        return this.backpackInventory.stillValid(playerIn);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index)
    {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if(slot != null && slot.hasItem())
        {
            ItemStack slotStack = slot.getItem();
            copy = slotStack.copy();
            if (index < this.rows * 9)
            {
                if(!this.moveItemStackTo(slotStack, this.rows * 9, this.slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(!this.moveItemStackTo(slotStack, 0, this.rows * 9, false))
            {
                return ItemStack.EMPTY;
            }

            if(slotStack.isEmpty())
            {
                slot.set(ItemStack.EMPTY);
            }
            else
            {
                slot.setChanged();
            }
        }
        return copy;
    }

    @Override
    public void removed(PlayerEntity playerIn)
    {
        super.removed(playerIn);
        this.backpackInventory.stopOpen(playerIn);
    }

    public int getRows()
    {
        return this.rows;
    }

    public boolean isOwner()
    {
        return this.owner;
    }
}
