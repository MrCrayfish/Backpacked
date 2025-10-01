package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.common.augment.impl.EmptyAugment;
import com.mrcrayfish.backpacked.common.augment.impl.FunnellingAugment;
import com.mrcrayfish.backpacked.common.augment.impl.GiantAugment;
import com.mrcrayfish.backpacked.util.Utils;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;

@RegistryContainer
public class ModAugmentTypes
{
    public static final RegistryEntry<AugmentType<EmptyAugment>> EMPTY = RegistryEntry.custom(ModRegistries.AUGMENT_TYPES, Utils.rl("empty"), () -> EmptyAugment.TYPE);
    public static final RegistryEntry<AugmentType<GiantAugment>> GIANT = RegistryEntry.custom(ModRegistries.AUGMENT_TYPES, Utils.rl("giant"), () -> GiantAugment.TYPE);
    public static final RegistryEntry<AugmentType<FunnellingAugment>> FUNNELLING = RegistryEntry.custom(ModRegistries.AUGMENT_TYPES, Utils.rl("funnelling"), () -> FunnellingAugment.TYPE);
}
