package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.core.ModContainers;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.inventory.container.slot.BackpackSlot;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
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

    public BackpackContainer(int id, PlayerInventory playerInventory, int rows)
    {
        this(id, playerInventory, new Inventory(9 * rows), rows);
    }

    public BackpackContainer(int id, PlayerInventory playerInventory, IInventory backpackInventory, int rows)
    {
        super(ModContainers.BACKPACK, id);
        assertInventorySize(backpackInventory, rows * 9);
        this.backpackInventory = backpackInventory;
        this.rows = rows;
        backpackInventory.openInventory(playerInventory.player);
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
    public boolean canInteractWith(PlayerEntity playerIn)
    {
        return this.backpackInventory.isUsableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
    {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if(slot != null && slot.getHasStack())
        {
            ItemStack slotStack = slot.getStack();
            copy = slotStack.copy();
            if (index < this.rows * 9)
            {
                if(!this.mergeItemStack(slotStack, this.rows * 9, this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(!this.mergeItemStack(slotStack, 0, this.rows * 9, false))
            {
                return ItemStack.EMPTY;
            }

            if(slotStack.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
        }
        return copy;
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn)
    {
        super.onContainerClosed(playerIn);
        this.backpackInventory.closeInventory(playerIn);
    }

    public int getRows()
    {
        return rows;
    }
}
