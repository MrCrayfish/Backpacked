package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.core.ModContainers;
import com.mrcrayfish.backpacked.core.ModItems;
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

    public BackpackContainer(int id, PlayerInventory playerInventory)
    {
        this(id, playerInventory, new Inventory(9));
    }

    public BackpackContainer(int id, PlayerInventory playerInventory, IInventory backpackInventory)
    {
        super(ModContainers.BACKPACK, id);
        this.backpackInventory = backpackInventory;
        backpackInventory.openInventory(playerInventory.player);

        for(int i = 0; i < 9; ++i)
        {
            this.addSlot(new Slot(backpackInventory, i, 8 + i * 18, 18)
            {
                @Override
                public boolean isItemValid(ItemStack stack)
                {
                    return stack.getItem() != ModItems.BACKPACK && !(Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock);
                }
            });
        }

        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 50 + i * 18));
            }
        }

        for(int i = 0; i < 9; i++)
        {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 108));
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
    public void onContainerClosed(PlayerEntity playerIn)
    {
        super.onContainerClosed(playerIn);
        this.backpackInventory.closeInventory(playerIn);
    }
}
