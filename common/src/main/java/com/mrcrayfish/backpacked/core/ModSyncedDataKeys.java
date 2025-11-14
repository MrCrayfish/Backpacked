package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.CustomDataSerializers;
import com.mrcrayfish.backpacked.common.backpack.CosmeticProperties;
import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
import com.mrcrayfish.backpacked.data.pickpocket.TraderPickpocketing;
import com.mrcrayfish.backpacked.data.unlock.UnlockTracker;
import com.mrcrayfish.framework.api.sync.Serializers;
import com.mrcrayfish.framework.api.sync.SyncedClassKey;
import com.mrcrayfish.framework.api.sync.SyncedDataKey;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class ModSyncedDataKeys
{
    public static final SyncedDataKey<Player, Integer> SELECTED_BACKPACK = SyncedDataKey.builder(SyncedClassKey.PLAYER, Serializers.INTEGER)
            .id(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "selected_backpack"))
            .defaultValueSupplier(() -> 0)
            .syncMode(SyncedDataKey.SyncMode.SELF_ONLY)
            .saveToFile()
            .build();

    public static final SyncedDataKey<Player, ItemStack> BACKPACK = SyncedDataKey.builder(SyncedClassKey.PLAYER, CustomDataSerializers.ITEM_STACK)
            .id(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack"))
            .defaultValueSupplier(() -> ItemStack.EMPTY)
            .syncMode(SyncedDataKey.SyncMode.NONE)
            .saveToFile()
            .build();

    public static final SyncedDataKey<Player, NonNullList<ItemStack>> BACKPACKS = SyncedDataKey.builder(SyncedClassKey.PLAYER, CustomDataSerializers.BACKPACKS)
            .id(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpacks"))
            .defaultValueSupplier(() -> NonNullList.withSize(1, ItemStack.EMPTY))
            .syncMode(SyncedDataKey.SyncMode.NONE)
            .saveToFile()
            .build();

    public static final SyncedDataKey<Player, Optional<CosmeticProperties>> COSMETIC_PROPERTIES = SyncedDataKey.builder(SyncedClassKey.PLAYER, CustomDataSerializers.OPTIONAL_COSMETIC_PROPERTIES)
            .id(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "cosmetic_backpack"))
            .defaultValueSupplier(Optional::empty)
            .syncMode(SyncedDataKey.SyncMode.ALL)
            .build();

    public static final SyncedDataKey<Player, UnlockTracker> UNLOCK_TRACKER = SyncedDataKey.builder(SyncedClassKey.PLAYER, UnlockTracker.SERIALIZER)
            .id(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "unlock_tracker"))
            .defaultValueSupplier(UnlockTracker::new)
            .syncMode(SyncedDataKey.SyncMode.SELF_ONLY)
            .saveToFile()
            .build();

    public static final SyncedDataKey<Player, UnlockableSlots> UNLOCKABLE_BACKPACK_SLOTS = SyncedDataKey.builder(SyncedClassKey.PLAYER, UnlockableSlots.SERIALIZER)
            .id(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "unlockable_backpack_slots"))
            .defaultValueSupplier(() -> new UnlockableSlots(1))
            .syncMode(SyncedDataKey.SyncMode.NONE)
            .saveToFile()
            .build();

    public static final SyncedDataKey<WanderingTrader, TraderPickpocketing> TRADER_PICKPOCKETING = SyncedDataKey.builder(SyncedClassKey.WANDERING_TRADER, TraderPickpocketing.SERIALIZER)
            .id(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "trader_pickpocketing"))
            .defaultValueSupplier(TraderPickpocketing::new)
            .syncMode(SyncedDataKey.SyncMode.TRACKING_ONLY)
            .saveToFile()
            .build();
}
