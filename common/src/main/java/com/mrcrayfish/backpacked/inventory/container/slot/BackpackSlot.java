package com.mrcrayfish.backpacked.inventory.container.slot;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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
        return !isBannedItem(stack);
    }

    public static boolean isBannedItem(ItemStack stack)
    {
        // Special case for bundles
        if(stack.getItem() == Items.BUNDLE)
        {
            return true;
        }
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if(Config.getBannedItemsList().contains(id))
        {
            return true;
        }
        return !stack.getItem().canFitInsideContainerItems();
    }
}
