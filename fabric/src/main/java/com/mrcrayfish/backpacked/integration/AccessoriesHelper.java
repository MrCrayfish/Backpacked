package com.mrcrayfish.backpacked.integration;

import com.mrcrayfish.backpacked.core.ModItems;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import io.wispforest.accessories.api.slot.SlotType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class AccessoriesHelper
{
    public static ItemStack getBackpackStack(Player player)
    {
        AccessoriesCapability accessories = AccessoriesCapability.get(player);
        if(accessories != null)
        {
            // Just find the first equipped stack. We will always target the first backpack found
            SlotEntryReference reference = accessories.getFirstEquipped(ModItems.BACKPACK.get());
            if(reference != null)
            {
                return reference.stack();
            }
        }
        return ItemStack.EMPTY;
    }

    public static void setBackpackStack(Player player, ItemStack stack)
    {
        AccessoriesCapability accessories = AccessoriesCapability.get(player);
        if(accessories != null)
        {
            // First try and find the first backpack that is equipped
            SlotEntryReference reference = accessories.getFirstEquipped(ModItems.BACKPACK.get());
            if(reference != null)
            {
                reference.reference().setStack(stack);
                return;
            }

            // Otherwise try to find slots that backpack can be equipped
            for(SlotType type : AccessoriesAPI.getValidSlotTypes(player, new ItemStack(ModItems.BACKPACK.get())))
            {
                AccessoriesContainer container = accessories.getContainer(type);
                if(container != null)
                {
                    for(int i = 0; i < container.getSize(); i++)
                    {
                        if(container.getAccessories().getItem(i).isEmpty())
                        {
                            container.getAccessories().setItem(i, stack.copy());
                            return;
                        }
                    }
                }
            }
        }
    }

    public static boolean isBackpackVisible(Player player)
    {
        AccessoriesCapability accessories = AccessoriesCapability.get(player);
        if(accessories != null)
        {
            // Just find the first equipped stack. We will always target the first backpack found
            SlotEntryReference reference = accessories.getFirstEquipped(ModItems.BACKPACK.get());
            if(reference != null)
            {
                AccessoriesContainer container = reference.reference().slotContainer();
                if(container != null)
                {
                    int index = reference.reference().slot();
                    return container.shouldRender(index);
                }
            }
        }
        return false;
    }
}
