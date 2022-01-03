package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.core.ModContainers;
import com.mrcrayfish.backpacked.inventory.container.slot.BackpackSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class BackpackContainerMenu extends AbstractContainerMenu
{
    private final Container backpackInventory;
    private final int rows;

    public BackpackContainerMenu(int id, Inventory playerInventory, int rows)
    {
        this(id, playerInventory, new SimpleContainer(9 * rows), rows);
    }

    public BackpackContainerMenu(int id, Inventory playerInventory, Container backpackContainer, int rows)
    {
        super(ModContainers.BACKPACK.get(), id);
        checkContainerSize(backpackContainer, rows * 9);
        this.backpackInventory = backpackContainer;
        this.rows = rows;
        backpackContainer.startOpen(playerInventory.player);
        int offset = (this.rows - 4) * 18;

        for(int j = 0; j < rows; j++)
        {
            for(int i = 0; i < 9; ++i)
            {
                this.addSlot(new BackpackSlot(backpackContainer, i + j * 9, 8 + i * 18, 18 + j * 18));
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
    public boolean stillValid(Player playerIn)
    {
        return this.backpackInventory.stillValid(playerIn);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index)
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
    public void removed(Player playerIn)
    {
        super.removed(playerIn);
        this.backpackInventory.stopOpen(playerIn);
    }

    public int getRows()
    {
        return rows;
    }
}
