package com.mrcrayfish.backpacked.client.augment;

import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;

import java.util.HashMap;
import java.util.Map;

public class AugmentSettingsFactories
{
    private static final Map<AugmentType<?>, AugmentMenuFactory<?>> FACTORIES = new HashMap<>();

    public static <T extends Augment<T>> void registerFactory(AugmentType<T> type, AugmentMenuFactory<T> menu)
    {
        if(FACTORIES.put(type, menu) != null)
        {
            throw new IllegalStateException("Duplicate factory for augment type: " + type.id());
        }
    }

    public static <T extends Augment<T>> boolean hasFactory(AugmentType<T> type)
    {
        return FACTORIES.containsKey(type);
    }

    @SuppressWarnings("unchecked")
    public static AugmentMenuFactory<Augment<?>> getFactory(Augment<?> augment)
    {
        return (AugmentMenuFactory<Augment<?>>) FACTORIES.get(augment.type());
    }
}
