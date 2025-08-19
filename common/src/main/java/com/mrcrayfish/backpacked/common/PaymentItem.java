package com.mrcrayfish.backpacked.common;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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
            ResourceLocation id = ResourceLocation.tryParse(this.idSupplier.get());
            this.cachedItem = BuiltInRegistries.ITEM.get(id);
        }
        return this.cachedItem;
    }

    public void clearItem()
    {
        this.cachedItem = null;
    }
}
