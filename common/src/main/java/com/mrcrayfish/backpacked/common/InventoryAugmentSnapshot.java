package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;

public record InventoryAugmentSnapshot<T extends Augment<T>>(BackpackInventory inventory, T augment)
{
}
