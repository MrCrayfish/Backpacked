package com.mrcrayfish.backpacked.common.backpack;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.world.item.ItemStack;

public record BackpackState(int cols, int rows, boolean slotsUnlocked)
{
    public static BackpackState create(ItemStack stack)
    {
        if(stack.getItem() instanceof BackpackItem item)
        {
            int cols = item.getColumnCount();
            int rows = item.getRowCount();
            boolean slotsUnlocked = Config.BACKPACK.inventory.slots.unlockAllSlots.get();
            return new BackpackState(cols, rows, slotsUnlocked);
        }
        return new BackpackState(0, 0, true);
    }

    public boolean isChanged()
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
