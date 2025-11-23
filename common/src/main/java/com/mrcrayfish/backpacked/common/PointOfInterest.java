package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;

import java.util.function.Supplier;

public record PointOfInterest(ResourceKey<PoiType> key, Supplier<PoiType> supplier)
{
    public PointOfInterest(String name, Supplier<PoiType> supplier)
    {
        this(ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name)), supplier);
    }

    public PoiType value()
    {
        return this.supplier.get();
    }

    @Override
    public boolean equals(Object o)
    {
        if(o == null || this.getClass() != o.getClass()) return false;
        PointOfInterest that = (PointOfInterest) o;
        return this.key.equals(that.key);
    }

    @Override
    public int hashCode()
    {
        return this.key.hashCode();
    }
}
