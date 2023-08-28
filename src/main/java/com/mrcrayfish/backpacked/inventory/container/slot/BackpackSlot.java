package com.mrcrayfish.backpacked.inventory.container.slot;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.core.ModItems;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

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
        return !isBannedItem(stack);
    }

    public static boolean isBannedItem(ItemStack stack)
    {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if(Backpacked.getBannedItemsList().contains(id))
        {
            return true;
        }
        if(stack.getItem() instanceof BlockItem)
        {
            BlockItem blockItem = (BlockItem) stack.getItem();
            if(blockItem.getBlock() instanceof ShulkerBoxBlock)
            {
                return true;
            }
        }
        return stack.getItem() == ModItems.BACKPACK.get();
    }
}
