package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
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

    public BackpackContainer(InventoryPlayer playerInventory)
    {
        this(playerInventory, new BackpackInventory());
    }

    public BackpackContainer(InventoryPlayer playerInventory, IInventory backpackInventory)
    {
        this.backpackInventory = backpackInventory;
        backpackInventory.openInventory(playerInventory.player);

        for(int i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(backpackInventory, i, 8 + i * 18, 18)
            {
                @Override
                public boolean isItemValid(ItemStack stack)
                {
                    return stack.getItem() != ModItems.backpack && !(Block.getBlockFromItem(stack.getItem()) instanceof BlockShulkerBox);
                }
            });
        }

        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 50 + i * 18));
            }
        }

        for(int i = 0; i < 9; i++)
        {
            this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 108));
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
            if(index < 9)
            {
                if(!this.mergeItemStack(slotStack, 9, this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(!this.mergeItemStack(slotStack, 0, 9, false))
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

    public IInventory getBackpackInventory()
    {
        return backpackInventory;
    }
}
