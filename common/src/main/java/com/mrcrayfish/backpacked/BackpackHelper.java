package com.mrcrayfish.backpacked;

import com.mrcrayfish.backpacked.common.InventoryAugmentSnapshot;
import com.mrcrayfish.backpacked.common.Navigate;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.common.Pagination;
import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import com.mrcrayfish.backpacked.inventory.ManagementInventory;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.util.InventoryHelper;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
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
            if(selected == -1)
            {
                selected = 0;
                radius = 0;
            }
            while(radius <= ManagementInventory.getMaxEquipable())
            {
                ItemStack stack = getBackpackStack(player, selected + radius);
                if(!stack.isEmpty())
                {
                    ModSyncedDataKeys.SELECTED_BACKPACK.setValue(player, selected + radius);
                    return;
                }
                if(radius > 0)
                {
                    stack = getBackpackStack(player, selected - radius);
                    if(!stack.isEmpty())
                    {
                        ModSyncedDataKeys.SELECTED_BACKPACK.setValue(player, selected - radius);
                        return;
                    }
                }
                radius++;
            }
            ModSyncedDataKeys.SELECTED_BACKPACK.setValue(player, -1);
        }
    }

    public static int navigateBackpackIndex(Player player, int currentIndex, Navigate navigate)
    {
        int newSelected = currentIndex + navigate.step();
        if(newSelected != currentIndex)
        {
            while(newSelected >= 0 && newSelected < ManagementInventory.getMaxEquipable())
            {
                ItemStack stack = getBackpackStack(player, newSelected);
                if(!stack.isEmpty())
                {
                    return newSelected;
                }
                newSelected += navigate.step();
            }
        }
        return currentIndex;
    }

    public static int firstAvailableBackpackIndex(Player player)
    {
        for(int i = 0; i < ManagementInventory.getMaxEquipable(); i++)
        {
            ItemStack stack = getBackpackStack(player, i);
            if(!stack.isEmpty())
            {
                return i;
            }
        }
        return -1;
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

    /**
     * Gets the first backpack the given player has equipped. If the player has no backpacks equipped,
     * this method will return an empty ItemStack. Keep in mind that this is not a copy, so changes
     * made to the ItemStack, like DataComponents, will be applied.
     *
     * @param player the player to get the backpack from
     * @return The ItemStack of the first equipped backpack, otherwise an empty ItemStack
     */
    public static ItemStack getFirstBackpackStack(Player player)
    {
        return getFirstBackpackStack(player, stack -> true);
    }

    /**
     * Gets the first backpack the given player has equipped and also matches the given filter. If
     * the player has no backpacks equipped, this method will return an empty ItemStack. Keep in
     * mind that this is not a copy, so changes made to the ItemStack, like DataComponents, will be
     * applied.
     *
     * @param player the player to get the backpack from
     * @param filter a predicate to test on the equipped backpack ItemStack
     * @return The ItemStack of the first equipped backpack, otherwise an empty ItemStack
     */
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

    /**
     * Equips the given stack into the first empty and unlocked equippable backpack slot of the
     * given player. If the stack is not able to be equipped, either due to no empty slots or the
     * slots are locked, this method will do nothing and simply return false. If the backpack was
     * equipped successfully, this method will return true.
     *
     * @param player the player that is attempting to equip the stack
     * @param stack  the stack to equip to the player (must be a backpack item)
     * @return True if the backpack was successfully equipped
     */
    public static boolean equipBackpack(Player player, ItemStack stack)
    {
        UnlockableSlots slots = getBackpackUnlockableSlots(player);
        NonNullList<ItemStack> backpacks = getBackpacks(player);
        for(int i = 0; i < backpacks.size(); i++)
        {
            if(slots.isUnlocked(i) && backpacks.get(i).isEmpty())
            {
                backpacks.set(i, stack.copyAndClear());
                return true;
            }
        }
        return false;
    }

    /**
     * Removes all equipped backpacks from the player and returns a NonNullList containing the
     * removed backpacks.
     *
     * @param player the player to remove the backpacks from
     * @return a NonNullList of ItemStacks (which should be backpack items)
     */
    public static NonNullList<ItemStack> removeAllBackpacks(Player player)
    {
        NonNullList<ItemStack> backpacks = getBackpacks(player);
        ModSyncedDataKeys.BACKPACKS.setValue(player, NonNullList.withSize(ManagementInventory.getMaxEquipable(), ItemStack.EMPTY));
        return backpacks;
    }

    /**
     * Creates pagination information used for displaying in the backpack inventory GUI. Due to the
     * underlying structure of how backpacks are stored in memory, extra logic is needed to determine
     * the "current page" and "total pages". The "total pages" is the count of equipped backpacks.
     * To get the "current page", the index of each equipped backpack needs to be stored in an ordered
     * list, then it is simply the index of the index in the list. This method will return a pair, the
     * first being the "current page", the second being the "total pages". If no backpacks are
     * equipped, this method will always return pair of 0 and 0.
     *
     * @param owner the player with the equipped backpacks
     * @return A pair with the current page (first) and total pages (second)
     */
    public static Pagination createPaginationInfo(Player owner, int backpackIndex)
    {
        IntList list = new IntArrayList();
        UnlockableSlots slots = getBackpackUnlockableSlots(owner);
        NonNullList<ItemStack> backpacks = getBackpacks(owner);
        for(int i = 0; i < backpacks.size(); i++)
        {
            if(slots.isUnlocked(i) && !backpacks.get(i).isEmpty())
            {
                list.add(i);
            }
        }
        int currentPage = !list.isEmpty() ? list.indexOf(backpackIndex) : 0;
        int totalPages = list.size();
        return new Pagination(currentPage + 1, totalPages);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends Augment<T>> T findAugment(ItemStack stack, AugmentType<T> type)
    {
        if(Config.getDisabledAugments().contains(type.id()))
            return null;
        Augments augments = Augments.get(stack);
        UnlockableSlots bays = getUnlockableAugmentBays(stack);
        if(bays.isUnlocked(0) && augments.firstState() && augments.firstAugment().type() == type)
            return (T) augments.firstAugment();
        if(bays.isUnlocked(1) && augments.secondState() && augments.secondAugment().type() == type)
            return (T) augments.secondAugment();
        if(bays.isUnlocked(2) && augments.thirdState() && augments.thirdAugment().type() == type)
            return (T) augments.thirdAugment();
        if(bays.isUnlocked(3) && augments.fourthState() && augments.fourthAugment().type() == type)
            return (T) augments.fourthAugment();
        return null;
    }

    public static UnlockableSlots getUnlockableAugmentBays(ItemStack stack)
    {
        if(stack.getItem() instanceof BackpackItem item)
        {
            return item.getUnlockableAugmentBays(stack);
        }
        return UnlockableSlots.NONE;
    }

    public static <T extends Augment<T>> List<InventoryAugmentSnapshot.One<T>> getBackpackInventoriesWithAugment(Player player, AugmentType<T> type)
    {
        BackpackedInventoryAccess access = (BackpackedInventoryAccess) player;
        return access.backpacked$streamNonNullBackpackInventories().map(inventory -> {
            T augment = findAugment(inventory.getBackpackStack(), type);
            return new InventoryAugmentSnapshot.One<>(inventory, augment);
        }).filter(result -> Objects.nonNull(result.augment())).toList();
    }

    public static <T extends Augment<T>, R extends Augment<R>> List<InventoryAugmentSnapshot.Two<T, R>> getBackpackInventoriesWithAugment(Player player, AugmentType<T> firstType, AugmentType<R> secondType)
    {
        BackpackedInventoryAccess access = (BackpackedInventoryAccess) player;
        return access.backpacked$streamNonNullBackpackInventories().map(inventory -> {
            ItemStack stack = inventory.getBackpackStack();
            T firstAugment = findAugment(stack, firstType);
            R secondAugment = findAugment(stack, secondType);
            if(firstAugment == null || secondAugment == null)
                return null;
            return new InventoryAugmentSnapshot.Two<>(inventory, firstAugment, secondAugment);
        }).filter(Objects::nonNull).toList();
    }
}
