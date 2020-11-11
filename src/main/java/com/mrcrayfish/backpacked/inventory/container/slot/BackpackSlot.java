package com.mrcrayfish.backpacked.inventory.container.slot;

import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
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
        return !(stack.getItem() instanceof BackpackItem) && !(Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock);
    }
}
