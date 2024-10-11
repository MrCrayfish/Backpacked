package com.mrcrayfish.backpacked.integration;

import com.mrcrayfish.backpacked.core.ModItems;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import io.wispforest.accessories.api.slot.SlotType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.Optional;

// TODO move to common

/**
 * Author: MrCrayfish
 */
public class AccessoriesHelper
{
    public static Optional<ItemStack> getStackInBackpackSlot(Player player)
    {
        // Try get equipped backpack
        ItemStack stack = getBackpackStack(player);
        if(!stack.isEmpty())
        {
            return Optional.of(stack);
        }

        // Try and find an empty slot
        AccessoriesCapability accessories = AccessoriesCapability.get(player);
        if(accessories != null)
        {
            ItemStack found = null;
            for(SlotType type : AccessoriesAPI.getValidSlotTypes(player, new ItemStack(ModItems.BACKPACK.get())))
            {
                AccessoriesContainer container = accessories.getContainer(type);
                if(container == null)
                    continue;

                // Try and find an empty accessories slot
                for(int i = 0; i < container.getSize(); i++)
                {
                    ItemStack accessoryStack = container.getAccessories().getItem(i);
                    if(accessoryStack.isEmpty())
                    {
                        return Optional.of(ItemStack.EMPTY);
                    }

                    // Remember first non-empty stack
                    if(found == null)
                    {
                        found = accessoryStack;
                    }
                }
            }
            return Optional.ofNullable(found);
        }

        // Something went wrong or there are no valid slots for the backpack
        return Optional.empty();
    }

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
