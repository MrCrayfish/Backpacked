package com.mrcrayfish.backpacked.inventory.container.slot;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class BackpackSlot extends Slot
{
    public BackpackSlot(Container inventoryIn, int index, int xPosition, int yPosition)
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
