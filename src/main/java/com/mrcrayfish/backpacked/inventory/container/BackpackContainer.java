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
import net.minecraft.util.math.MathHelper;

/**
 * Author: MrCrayfish
 */
public class BackpackContainer extends Container
{
    public static final int MAX_COLUMNS = 13;
    public static final int MAX_ROWS = 7;

    private final IInventory backpackInventory;
    private final int cols;
    private final int rows;
    private final boolean owner;

    public BackpackContainer(int id, PlayerInventory playerInventory, int cols, int rows, boolean owner)
    {
        this(id, playerInventory, new Inventory(MathHelper.clamp(cols, 1, MAX_COLUMNS) * MathHelper.clamp(rows, 1, MAX_ROWS)), cols, rows, owner);
    }

    public BackpackContainer(int id, PlayerInventory playerInventory, IInventory backpackInventory, int cols, int rows, boolean owner)
    {
        super(ModContainers.BACKPACK.get(), id);
        this.backpackInventory = backpackInventory;
        this.cols = MathHelper.clamp(cols, 1, MAX_COLUMNS);
        this.rows = MathHelper.clamp(rows, 1, MAX_ROWS);
        this.owner = owner;
        checkContainerSize(backpackInventory, this.cols * this.rows);
        backpackInventory.startOpen(playerInventory.player);
        int playerInventoryOffset = this.rows * 18 + 17 + 14 + 1;
        int backpackSlotWidth = this.cols * 18;
        int minSlotWidth = 9 * 18;
        int backpackStartX = Math.max((minSlotWidth - backpackSlotWidth) / 2, 0);
        int inventoryStartX = Math.max((backpackSlotWidth - minSlotWidth) / 2, 0);

        for(int j = 0; j < rows; j++)
        {
            for(int i = 0; i < cols; ++i)
            {
                this.addSlot(new BackpackSlot(backpackInventory, i + j * cols, 8 + backpackStartX + i * 18, 18 + j * 18));
            }
        }

        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + inventoryStartX + j * 18, i * 18 + playerInventoryOffset));
            }
        }

        for(int i = 0; i < 9; i++)
        {
            this.addSlot(new Slot(playerInventory, i, 8 + inventoryStartX + i * 18, playerInventoryOffset + 58));
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
            if (index < this.rows * this.cols)
            {
                if(!this.moveItemStackTo(slotStack, this.rows * this.cols, this.slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(!this.moveItemStackTo(slotStack, 0, this.rows * this.cols, false))
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

    public int getCols()
    {
        return this.cols;
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
