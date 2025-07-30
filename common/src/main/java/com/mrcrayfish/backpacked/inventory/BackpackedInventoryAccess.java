package com.mrcrayfish.backpacked.inventory;

import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 */
public interface BackpackedInventoryAccess
{
    int backpacked$GetBackpackInventoryCount();

    @Nullable
    BackpackInventory backpacked$GetBackpackInventory(int index);
}
