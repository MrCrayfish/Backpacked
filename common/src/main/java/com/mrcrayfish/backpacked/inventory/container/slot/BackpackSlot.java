package com.mrcrayfish.backpacked.inventory.container.slot;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.core.Registry;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;

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
        if(Config.getBannedItemsList().contains(Registry.ITEM.getKey(stack.getItem())))
        {
            return false;
        }
        if(!stack.getItem().canFitInsideContainerItems())
        {
            return false;
        }
        return !(stack.getItem() instanceof BackpackItem) && !(Block.byItem(stack.getItem()) instanceof ShulkerBoxBlock) && stack.getItem() != Items.BUNDLE;
    }
}
