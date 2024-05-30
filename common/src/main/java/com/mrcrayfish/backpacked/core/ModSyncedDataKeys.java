package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.data.pickpocket.TraderPickpocketing;
import com.mrcrayfish.backpacked.data.unlock.UnlockTracker;
import com.mrcrayfish.framework.api.sync.SyncedClassKey;
import com.mrcrayfish.framework.api.sync.SyncedDataKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;

/**
 * Author: MrCrayfish
 */
public class ModSyncedDataKeys
{
    public static final SyncedDataKey<Player, UnlockTracker> UNLOCK_TRACKER = SyncedDataKey.builder(SyncedClassKey.PLAYER, UnlockTracker.SERIALIZER)
        .id(new ResourceLocation(Constants.MOD_ID, "unlock_tracker"))
        .defaultValueSupplier(UnlockTracker::new)
        .syncMode(SyncedDataKey.SyncMode.SELF_ONLY)
        .saveToFile()
        .build();

    public static final SyncedDataKey<WanderingTrader, TraderPickpocketing> TRADER_PICKPOCKETING = SyncedDataKey.builder(SyncedClassKey.WANDERING_TRADER, TraderPickpocketing.SERIALIZER)
        .id(new ResourceLocation(Constants.MOD_ID, "trader_pickpocketing"))
        .defaultValueSupplier(TraderPickpocketing::new)
        .syncMode(SyncedDataKey.SyncMode.TRACKING_ONLY)
        .saveToFile()
        .build();
}
