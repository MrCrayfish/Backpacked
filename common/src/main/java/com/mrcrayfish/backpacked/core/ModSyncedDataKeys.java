package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.data.pickpocket.TraderPickpocketing;
import com.mrcrayfish.backpacked.data.unlock.UnlockTracker;
import com.mrcrayfish.framework.api.sync.DataSerializer;
import com.mrcrayfish.framework.api.sync.Serializers;
import com.mrcrayfish.framework.api.sync.SyncedClassKey;
import com.mrcrayfish.framework.api.sync.SyncedDataKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class ModSyncedDataKeys
{
    public static final SyncedDataKey<Player, ItemStack> BACKPACK = SyncedDataKey.builder(SyncedClassKey.PLAYER, Serializers.ITEM_STACK)
        .id(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack"))
        .defaultValueSupplier(() -> ItemStack.EMPTY)
        .syncMode(SyncedDataKey.SyncMode.NONE)
        .saveToFile()
        .build();

    public static final SyncedDataKey<Player, UnlockTracker> UNLOCK_TRACKER = SyncedDataKey.builder(SyncedClassKey.PLAYER, UnlockTracker.SERIALIZER)
        .id(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "unlock_tracker"))
        .defaultValueSupplier(UnlockTracker::new)
        .syncMode(SyncedDataKey.SyncMode.SELF_ONLY)
        .saveToFile()
        .build();

    public static final SyncedDataKey<WanderingTrader, TraderPickpocketing> TRADER_PICKPOCKETING = SyncedDataKey.builder(SyncedClassKey.WANDERING_TRADER, TraderPickpocketing.SERIALIZER)
        .id(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "trader_pickpocketing"))
        .defaultValueSupplier(TraderPickpocketing::new)
        .syncMode(SyncedDataKey.SyncMode.TRACKING_ONLY)
        .saveToFile()
        .build();
}
