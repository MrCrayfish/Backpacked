package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;

import java.util.function.Supplier;

public interface InventoryAugmentSnapshot // TODO DONE
{
    BackpackInventory inventory();

    record One<T extends Augment<T>>(BackpackInventory inventory, T augment) implements InventoryAugmentSnapshot {}

    record Two<T extends Augment<T>, R extends Augment<R>>(BackpackInventory inventory, T firstAugment, R secondAugment) implements InventoryAugmentSnapshot {}
}
