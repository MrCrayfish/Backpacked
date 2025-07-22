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
            boolean slotsUnlocked = Config.SERVER.backpack.inventory.slots.unlockAll.get();
            return new BackpackState(cols, rows, slotsUnlocked);
        }
        return new BackpackState(0, 0, true);
    }

    public boolean isChanged()
    {
        if(this.cols != Config.SERVER.backpack.inventory.size.columns.get())
            return true;
        if(this.rows != Config.SERVER.backpack.inventory.size.rows.get())
            return true;
        if(this.slotsUnlocked != Config.SERVER.backpack.inventory.slots.unlockAll.get())
            return true;
        return false;
    }
}
