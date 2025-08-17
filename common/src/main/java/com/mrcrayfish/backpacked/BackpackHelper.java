package com.mrcrayfish.backpacked;

import com.mojang.datafixers.util.Pair;
import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import com.mrcrayfish.backpacked.inventory.ManagementInventory;
import com.mrcrayfish.backpacked.util.InventoryHelper;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class BackpackHelper
{
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

        UnlockableSlots slots = getBackpackUnlockableSlots(player);
        if(!slots.isUnlocked(index))
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

        UnlockableSlots slots = getBackpackUnlockableSlots(player);
        if(!slots.isUnlocked(index))
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

    public static UnlockableSlots getBackpackUnlockableSlots(Player player)
    {
        if(Config.BACKPACK.equipable.unlockAllEquipableSlots.get())
            return UnlockableSlots.ALL;

        UnlockableSlots slots = ModSyncedDataKeys.UNLOCKABLE_BACKPACK_SLOTS.getValue(player);
        if(slots.getMaxSlots() != ManagementInventory.getMaxEquipable())
        {
            slots = slots.setMaxSlots(ManagementInventory.getMaxEquipable());
            ModSyncedDataKeys.UNLOCKABLE_BACKPACK_SLOTS.setValue(player, slots);
        }
        if(Config.BACKPACK.equipable.unlockFirstEquipableSlot.get())
        {
            if(!slots.isUnlocked(0))
            {
                slots = slots.unlockSlot(0);
                ModSyncedDataKeys.UNLOCKABLE_BACKPACK_SLOTS.setValue(player, slots);
            }
        }
        return slots;
    }

    public static void setBackpackUnlockableSlots(Player player, UnlockableSlots slots)
    {
        if(Config.BACKPACK.equipable.unlockAllEquipableSlots.get())
            return;

        if(slots.getMaxSlots() != ManagementInventory.getMaxEquipable())
        {
            slots = slots.setMaxSlots(ManagementInventory.getMaxEquipable());
        }
        ModSyncedDataKeys.UNLOCKABLE_BACKPACK_SLOTS.setValue(player, slots);
    }

    public static ItemStack getFirstBackpackStack(Player player)
    {
        return getFirstBackpackStack(player, stack -> true);
    }

    public static ItemStack getFirstBackpackStack(Player player, Predicate<ItemStack> filter)
    {
        UnlockableSlots slots = getBackpackUnlockableSlots(player);
        NonNullList<ItemStack> backpacks = getBackpacks(player);
        for(int i = 0; i < backpacks.size(); i++)
        {
            if(!slots.isUnlocked(i))
                continue;

            ItemStack stack = backpacks.get(i);
            if(!stack.isEmpty() && filter.test(stack))
            {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static boolean equipBackpack(Player player, ItemStack stack)
    {
        UnlockableSlots slots = getBackpackUnlockableSlots(player);
        NonNullList<ItemStack> backpacks = getBackpacks(player);
        for(int i = 0; i < backpacks.size(); i++)
        {
            if(slots.isUnlocked(i) && backpacks.get(i).isEmpty())
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

    public static Pair<Integer, Integer> createPaginationInfo(Player player)
    {
        IntList list = new IntArrayList();
        UnlockableSlots slots = getBackpackUnlockableSlots(player);
        NonNullList<ItemStack> backpacks = getBackpacks(player);
        for(int i = 0; i < backpacks.size(); i++)
        {
            if(slots.isUnlocked(i) && !backpacks.get(i).isEmpty())
            {
                list.add(i);
            }
        }
        int selected = getSelectedBackpackIndex(player);
        return Pair.of(list.indexOf(selected), list.size());
    }
}
