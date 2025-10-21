package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.common.augment.impl.*;
import com.mrcrayfish.backpacked.util.Utils;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;

@RegistryContainer
public class ModAugmentTypes
{
    public static final RegistryEntry<AugmentType<EmptyAugment>> EMPTY = RegistryEntry.custom(ModRegistries.AUGMENT_TYPES, Utils.rl("empty"), () -> EmptyAugment.TYPE);
    public static final RegistryEntry<AugmentType<GiantAugment>> GIANT = RegistryEntry.custom(ModRegistries.AUGMENT_TYPES, Utils.rl("giant"), () -> GiantAugment.TYPE);
    public static final RegistryEntry<AugmentType<FunnellingAugment>> FUNNELLING = RegistryEntry.custom(ModRegistries.AUGMENT_TYPES, Utils.rl("funnelling"), () -> FunnellingAugment.TYPE);
    public static final RegistryEntry<AugmentType<QuiverlinkAugment>> QUIVERLINK = RegistryEntry.custom(ModRegistries.AUGMENT_TYPES, Utils.rl("quiverlink"), () -> QuiverlinkAugment.TYPE);
    public static final RegistryEntry<AugmentType<ImbuedHideAugment>> IMBUED_HIDE = RegistryEntry.custom(ModRegistries.AUGMENT_TYPES, Utils.rl("imbued_hide"), () -> ImbuedHideAugment.TYPE);
    public static final RegistryEntry<AugmentType<LootboundAugment>> LOOTBOUND = RegistryEntry.custom(ModRegistries.AUGMENT_TYPES, Utils.rl("lootbound"), () -> LootboundAugment.TYPE);
}
