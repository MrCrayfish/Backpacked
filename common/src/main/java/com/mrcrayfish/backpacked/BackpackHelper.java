package com.mrcrayfish.backpacked;

import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import com.mrcrayfish.backpacked.inventory.ManagementInventory;
import com.mrcrayfish.backpacked.util.InventoryHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class BackpackHelper
{
    public static ItemStack getSelectedBackpackStack(Player player)
    {
        int selected = ModSyncedDataKeys.SELECTED_BACKPACK.getValue(player);
        return getBackpackStack(player, selected);
    }

    public static int getSelectedBackpackIndex(Player player)
    {
        updateSelectedBackpackIndex(player);
        return ModSyncedDataKeys.SELECTED_BACKPACK.getValue(player);
    }

    private static void updateSelectedBackpackIndex(Player player)
    {
        int selected = ModSyncedDataKeys.SELECTED_BACKPACK.getValue(player);
        if(getBackpackStack(player, selected).isEmpty())
        {
            int radius = 1;
            while(radius < ManagementInventory.getMaxEquipable())
            {
                ItemStack stack = getBackpackStack(player, selected + radius);
                if(!stack.isEmpty())
                {
                    ModSyncedDataKeys.SELECTED_BACKPACK.setValue(player, selected + radius);
                    return;
                }
                stack = getBackpackStack(player, selected - radius);
                if(!stack.isEmpty())
                {
                    ModSyncedDataKeys.SELECTED_BACKPACK.setValue(player, selected - radius);
                    return;
                }
                radius++;
            }
            ModSyncedDataKeys.SELECTED_BACKPACK.setValue(player, -1);
        }
    }

    public static int navigateSelectedBackpackIndex(Player player, int direction)
    {
        int selected = getSelectedBackpackIndex(player);
        direction = Mth.sign(direction);
        int newSelected = selected + direction;
        while(newSelected >= 0 && newSelected < ManagementInventory.getMaxEquipable())
        {
            ItemStack stack = getBackpackStack(player, newSelected);
            if(!stack.isEmpty())
            {
                ModSyncedDataKeys.SELECTED_BACKPACK.setValue(player, newSelected);
                return newSelected;
            }
            newSelected += direction;
        }
        return selected;
    }

    public static ItemStack getBackpackStack(Player player, int index)
    {
        if(index < 0 || index >= ManagementInventory.getMaxEquipable())
            return ItemStack.EMPTY;

        NonNullList<ItemStack> backpacks = getBackpacks(player);
        ItemStack stack = backpacks.get(index);
        if(stack.isEmpty())
        {
            // TODO Temporary until full release
            stack = ModSyncedDataKeys.BACKPACK.getValue(player);
            if(!stack.isEmpty())
            {
                setBackpackStack(player, stack, index);
                ModSyncedDataKeys.BACKPACK.setValue(player, ItemStack.EMPTY);
            }
        }
        return stack;
    }

    public static boolean setBackpackStack(Player player, ItemStack stack, int index)
    {
        if(index < 0 || index >= ManagementInventory.getMaxEquipable())
            return false;

        if(stack.is(ModItems.BACKPACK.get()))
        {
            // Keep in mind that this will not trigger a sync
            getBackpacks(player).set(index, stack);
            return true;
        }
        return false;
    }

    public static NonNullList<ItemStack> getBackpacks(Player player)
    {
        NonNullList<ItemStack> backpacks = ModSyncedDataKeys.BACKPACKS.getValue(player);
        if(backpacks.size() != ManagementInventory.getMaxEquipable())
        {
            NonNullList<ItemStack> newBackpacks = NonNullList.withSize(ManagementInventory.getMaxEquipable(), ItemStack.EMPTY);
            InventoryHelper.mergeItemsOrSpawnIntoLevel(backpacks, newBackpacks, player.level(), player.position());
            ModSyncedDataKeys.BACKPACKS.setValue(player, newBackpacks);
            backpacks = newBackpacks;
        }
        return backpacks;
    }

    public static ItemStack getFirstBackpackStack(Player player)
    {
        return getFirstBackpackStack(player, stack -> true);
    }

    public static ItemStack getFirstBackpackStack(Player player, Predicate<ItemStack> filter)
    {
        for(ItemStack stack : getBackpacks(player))
        {
            if(!stack.isEmpty() && filter.test(stack))
            {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static boolean equipBackpack(Player player, ItemStack stack)
    {
        NonNullList<ItemStack> backpacks = getBackpacks(player);
        for(int i = 0; i < backpacks.size(); i++)
        {
            if(backpacks.get(i).isEmpty())
            {
                ItemStack copy = player.isCreative() ? stack.copy() : stack.copyAndClear();
                backpacks.set(i, copy);
                return true;
            }
        }
        return false;
    }

    public static NonNullList<ItemStack> removeAllBackpacks(Player player)
    {
        NonNullList<ItemStack> backpacks = getBackpacks(player);
        ModSyncedDataKeys.BACKPACKS.setValue(player, NonNullList.withSize(ManagementInventory.getMaxEquipable(), ItemStack.EMPTY));
        return backpacks;
    }
}
