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
    public static final RegistryEntry<AugmentType<FunnellingAugment>> FUNNELLING = RegistryEntry.custom(ModRegistries.AUGMENT_TYPES, Utils.rl("funnelling"), () -> FunnellingAugment.TYPE);
    public static final RegistryEntry<AugmentType<QuiverlinkAugment>> QUIVERLINK = RegistryEntry.custom(ModRegistries.AUGMENT_TYPES, Utils.rl("quiverlink"), () -> QuiverlinkAugment.TYPE);
    public static final RegistryEntry<AugmentType<ImbuedHideAugment>> IMBUED_HIDE = RegistryEntry.custom(ModRegistries.AUGMENT_TYPES, Utils.rl("imbued_hide"), () -> ImbuedHideAugment.TYPE);
    public static final RegistryEntry<AugmentType<LootboundAugment>> LOOTBOUND = RegistryEntry.custom(ModRegistries.AUGMENT_TYPES, Utils.rl("lootbound"), () -> LootboundAugment.TYPE);
    public static final RegistryEntry<AugmentType<ReforgeAugment>> REFORGE = RegistryEntry.custom(ModRegistries.AUGMENT_TYPES, Utils.rl("reforge"), () -> ReforgeAugment.TYPE);
    public static final RegistryEntry<AugmentType<ImmortalAugment>> IMMORTAL = RegistryEntry.custom(ModRegistries.AUGMENT_TYPES, Utils.rl("immortal"), () -> ImmortalAugment.TYPE);
    public static final RegistryEntry<AugmentType<LightweaverAugment>> LIGHTWEAVER = RegistryEntry.custom(ModRegistries.AUGMENT_TYPES, Utils.rl("lightweaver"), () -> LightweaverAugment.TYPE);
    public static final RegistryEntry<AugmentType<FarmhandAugment>> FARMHAND = RegistryEntry.custom(ModRegistries.AUGMENT_TYPES, Utils.rl("farmhand"), () -> FarmhandAugment.TYPE);
    public static final RegistryEntry<AugmentType<SeedflowAugment>> SEEDFLOW = RegistryEntry.custom(ModRegistries.AUGMENT_TYPES, Utils.rl("seedflow"), () -> SeedflowAugment.TYPE);
    public static final RegistryEntry<AugmentType<HopperBridgeAugment>> HOPPER_BRIDGE = RegistryEntry.custom(ModRegistries.AUGMENT_TYPES, Utils.rl("hopper_bridge"), () -> HopperBridgeAugment.TYPE);
    public static final RegistryEntry<AugmentType<RecallAugment>> RECALL = RegistryEntry.custom(ModRegistries.AUGMENT_TYPES, Utils.rl("recall"), () -> RecallAugment.TYPE);
}
