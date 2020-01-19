package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.container.slot.BackpackSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class BackpackContainer extends Container
{
    private final IInventory backpackInventory;
    private final int rows;

    public BackpackContainer(InventoryPlayer playerInventory, int rows)
    {
        this(playerInventory, new BackpackInventory(rows), rows);
    }

    public BackpackContainer(InventoryPlayer playerInventory, IInventory backpackInventory, int rows)
    {
        assert backpackInventory.getSizeInventory() == rows * 9;
        this.backpackInventory = backpackInventory;
        this.rows = rows;
        backpackInventory.openInventory(playerInventory.player);
        int offset = (this.rows - 4) * 18;

        for(int j = 0; j < rows; j++)
        {
            for(int i = 0; i < 9; ++i)
            {
                this.addSlotToContainer(new BackpackSlot(backpackInventory, i + j * 9, 8 + i * 18, 18 + j * 18));
            }
        }

        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 103 + i * 18 + offset));
            }
        }

        for(int i = 0; i < 9; i++)
        {
            this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 161 + offset));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.backpackInventory.isUsableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if(slot != null && slot.getHasStack())
        {
            ItemStack slotStack = slot.getStack();
            copy = slotStack.copy();
            if(index < this.rows * 9)
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
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);
        this.backpackInventory.closeInventory(playerIn);
    }
}
