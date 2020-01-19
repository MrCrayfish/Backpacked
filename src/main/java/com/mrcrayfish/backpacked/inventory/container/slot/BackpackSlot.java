package com.mrcrayfish.backpacked.inventory.container.slot;

import com.mrcrayfish.backpacked.core.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class BackpackSlot extends Slot
{
    public BackpackSlot(IInventory inventoryIn, int index, int xPosition, int yPosition)
    {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return stack.getItem() != ModItems.backpack && !(Block.getBlockFromItem(stack.getItem()) instanceof BlockShulkerBox);
    }
}
