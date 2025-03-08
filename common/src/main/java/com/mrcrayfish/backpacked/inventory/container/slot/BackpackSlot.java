package com.mrcrayfish.backpacked.inventory.container.slot;

import com.mrcrayfish.backpacked.Config;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Author: MrCrayfish
 */
public class BackpackSlot extends ConditionalSlot
{
    public BackpackSlot(Container inventoryIn, int index, int x, int y)
    {
        super(inventoryIn, index, x, y, stack -> !isBannedItem(stack));
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
