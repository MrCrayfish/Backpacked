package com.mrcrayfish.backpacked.common;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class PaymentItem
{
    private final Supplier<String> idSupplier;
    private Item cachedItem;

    public PaymentItem(Supplier<String> idSupplier)
    {
        this.idSupplier = idSupplier;
    }

    public Item getItem()
    {
        if(this.cachedItem == null)
        {
            Identifier id = Identifier.tryParse(this.idSupplier.get());
            this.cachedItem = BuiltInRegistries.ITEM.getValue(id);
        }
        return this.cachedItem;
    }

    public void clearItem()
    {
        this.cachedItem = null;
    }
}
