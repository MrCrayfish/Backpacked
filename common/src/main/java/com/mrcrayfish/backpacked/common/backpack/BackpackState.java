package com.mrcrayfish.backpacked.common.backpack;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.world.item.ItemStack;

/**
 * Holds a record of the current backpack configuration. This is used in part to determine if a
 * backpack container is still valid. If the config changes, the backpack inventory needs to be
 * updated with the new changes, and the container will need to be reopened by the player.
 *
 * @param cols          the column count of the backpack inventory
 * @param rows          the row count of the backpack inventory
 * @param slotsUnlocked if all slots in the backpack are unlocked
 */
public record BackpackState(int cols, int rows, boolean slotsUnlocked)
{
    public static final BackpackState UNKNOWN = new BackpackState(0, 0, true);

    /**
     * Creates a BackpackState from an ItemStack. If the item of the stack is not a backpack,
     * the BackpackState will always be {@link #UNKNOWN}.
     *
     * @param stack an ItemStack of a backpack
     * @return a new BackpackState based on the given backpack ItemStack
     */
    public static BackpackState create(ItemStack stack)
    {
        if(stack.getItem() instanceof BackpackItem item)
        {
            int cols = item.getColumnCount();
            int rows = item.getRowCount();
            boolean slotsUnlocked = Config.BACKPACK.inventory.slots.unlockAllSlots.get();
            return new BackpackState(cols, rows, slotsUnlocked);
        }
        return UNKNOWN;
    }

    /**
     * @return True if this BackpackState is invalid
     */
    public boolean isInvalid()
    {
        if(this.cols != Config.BACKPACK.inventory.size.columns.get())
            return true;
        if(this.rows != Config.BACKPACK.inventory.size.rows.get())
            return true;
        if(this.slotsUnlocked != Config.BACKPACK.inventory.slots.unlockAllSlots.get())
            return true;
        return false;
    }
}
