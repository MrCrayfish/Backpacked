package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.common.backpack.UnlockedSlots;
import com.mrcrayfish.backpacked.core.ModContainers;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.container.data.BackpackContainerData;
import com.mrcrayfish.backpacked.inventory.container.slot.LockedSlot;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class BackpackContainerMenu extends CustomContainerMenu implements LockedSlotController
{
    public static final int MAX_COLUMNS = 13;
    public static final int MAX_ROWS = 7;

    private final Container backpackInventory;
    private final int cols;
    private final int rows;
    private final boolean owner;
    private UnlockedSlots unlockedSlots;

    public BackpackContainerMenu(int id, Inventory playerInventory, BackpackContainerData data)
    {
        this(id, playerInventory, new SimpleContainer(Mth.clamp(data.columns(), 1, MAX_COLUMNS) * Mth.clamp(data.rows(), 1, MAX_ROWS)), data.columns(), data.rows(), data.owner(), data.slots());
    }

    public BackpackContainerMenu(int id, Inventory playerInventory, Container backpackContainer, int cols, int rows, boolean owner, UnlockedSlots slots)
    {
        super(ModContainers.BACKPACK.get(), id);
        this.backpackInventory = backpackContainer;
        this.cols = Mth.clamp(cols, 1, MAX_COLUMNS);
        this.rows = Mth.clamp(rows, 1, MAX_ROWS);
        this.owner = owner;
        this.unlockedSlots = slots;

        checkContainerSize(backpackContainer, this.cols * this.rows);
        backpackContainer.startOpen(playerInventory.player);

        int backpackWidth = 11 + Math.max(9 * 18, this.cols * 18) + 11;
        int backpackSlotWidth = this.cols * 18;
        int backpackSlotsX = Math.max((backpackWidth - backpackSlotWidth) / 2, 0) + 1;
        int backpackSlotsY = 28;

        for(int y = 0; y < rows; y++)
        {
            for(int x = 0; x < cols; x++)
            {
                this.addSlot(new LockedSlot(this, backpackContainer, x + y * cols, backpackSlotsX + x * 18, backpackSlotsY + y * 18));
            }
        }

        int inventorySlotsWidth = 9 * 18;
        int inventorySlotsX = Math.max((backpackWidth - inventorySlotsWidth) / 2, 0) + 1;
        int inventorySlotsY = 26 + this.rows * 18 + 15 + 3 + 19;
        this.addPlayerInventorySlots(playerInventory, inventorySlotsX, inventorySlotsY);
    }

    public Container getBackpackInventory()
    {
        return this.backpackInventory;
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

    public UnlockedSlots getUnlockedSlots()
    {
        return this.unlockedSlots;
    }

    @Override
    public void unlockSlot(int slot)
    {
        this.unlockedSlots = this.unlockedSlots.unlockSlot(slot);
    }

    @Override
    public boolean isSlotUnlocked(int slot)
    {
        return this.unlockedSlots.isUnlocked(slot);
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
        if(slot.hasItem())
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
    public void removed(Player playerIn)
    {
        super.removed(playerIn);
        this.backpackInventory.stopOpen(playerIn);
    }

    public ItemStack getBackpackStack()
    {
        if(this.backpackInventory instanceof ShelfBlockEntity.BackpackShelfContainer container)
        {
            return container.getBlockEntity().getBackpack();
        }
        if(this.backpackInventory instanceof BackpackInventory inventory)
        {
            return inventory.getBackpackStack();
        }
        return ItemStack.EMPTY;
    }

    public void openManagement(ServerPlayer player)
    {
        if(this.backpackInventory instanceof ShelfBlockEntity.BackpackShelfContainer container)
        {
            container.getBlockEntity().openShelfManagement(player);
        }
        else if(this.backpackInventory instanceof BackpackInventory)
        {
            BackpackItem.openBackpackManagement(player);
        }
    }
}
