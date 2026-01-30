package com.mrcrayfish.backpacked.common;

import net.minecraft.world.item.Item;

public interface FilterableItems<T extends FilterableItems<T>> // TODO DONE
{
    T addItemFilter(Item item);

    T removeItemFilter(Item item);

    boolean isFilteringItem(Item item);

    boolean isFilterFull();
}
