package com.mrcrayfish.backpacked.inventory.container.slot;

import com.mrcrayfish.backpacked.Backpacked;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

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
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if(Backpacked.getBannedItemsList().contains(id))
        {
            return true;
        }
        return !stack.getItem().canFitInsideContainerItems();
    }
}
