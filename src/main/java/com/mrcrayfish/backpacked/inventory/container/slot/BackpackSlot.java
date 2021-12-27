package com.mrcrayfish.backpacked.inventory.container.slot;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Config;
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
    public boolean mayPlace(ItemStack stack)
    {
        if(Backpacked.getBannedItemsList().contains(stack.getItem().getRegistryName()))
        {
            return false;
        }
        return !(stack.getItem() instanceof BackpackItem) && !(Block.byItem(stack.getItem()) instanceof ShulkerBoxBlock);
    }
}
