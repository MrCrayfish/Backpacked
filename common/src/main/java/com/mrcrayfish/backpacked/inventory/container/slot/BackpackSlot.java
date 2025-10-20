package com.mrcrayfish.backpacked.inventory.container.slot;

import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.container.UnlockableController;
import net.minecraft.world.Container;

/**
 * Author: MrCrayfish
 */
public class BackpackSlot extends UnlockableSlot
{
    public BackpackSlot(UnlockableController controller, Container container, int index, int x, int y)
    {
        super(controller, container, index, x, y);
        this.setPredicate(BackpackInventory::isAllowedItem);
    }
}
