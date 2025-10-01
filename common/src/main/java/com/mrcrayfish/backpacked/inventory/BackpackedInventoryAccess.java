package com.mrcrayfish.backpacked.inventory;

import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

/**
 * Author: MrCrayfish
 */
public interface BackpackedInventoryAccess
{
    int backpacked$GetBackpackInventoryCount();

    @Nullable
    BackpackInventory backpacked$GetBackpackInventory(int index);

    Stream<BackpackInventory> backpacked$streamNonNullBackpackInventories();
}
