package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.common.augment.SavedAugments;
import com.mrcrayfish.backpacked.common.backpack.CosmeticProperties;
import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
import com.mrcrayfish.backpacked.util.Utils;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public class ModDataComponents
{
    public static final RegistryEntry<DataComponentType<CosmeticProperties>> COSMETIC_PROPERTIES = RegistryEntry.dataComponentType(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "cosmetic_properties"), builder -> {
        return builder.persistent(CosmeticProperties.CODEC).networkSynchronized(CosmeticProperties.STREAM_CODEC);
    });

    public static final RegistryEntry<DataComponentType<UnlockableSlots>> UNLOCKABLE_SLOTS = RegistryEntry.dataComponentType(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "unlockable_slots"), builder -> {
        return builder.persistent(UnlockableSlots.CODEC).networkSynchronized(UnlockableSlots.STREAM_CODEC);
    });

    public static final RegistryEntry<DataComponentType<UnlockableSlots>> UNLOCKABLE_AUGMENT_BAYS = RegistryEntry.dataComponentType(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "unlockable_augment_bays"), builder -> {
        return builder.persistent(UnlockableSlots.CODEC).networkSynchronized(UnlockableSlots.STREAM_CODEC);
    });

    public static final RegistryEntry<DataComponentType<Augments>> AUGMENTS = RegistryEntry.dataComponentType(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "augments"), builder -> {
        return builder.persistent(Augments.CODEC).networkSynchronized(Augments.STREAM_CODEC);
    });

    public static final RegistryEntry<DataComponentType<SavedAugments>> SAVED_AUGMENTS = RegistryEntry.dataComponentType(Utils.rl("saved_augments"), builder -> {
        return builder.persistent(SavedAugments.CODEC).networkSynchronized(SavedAugments.STREAM_CODEC);
    });
}
