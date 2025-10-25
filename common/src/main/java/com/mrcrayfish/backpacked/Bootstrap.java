package com.mrcrayfish.backpacked;

import com.mrcrayfish.backpacked.common.BackpackEvents;
import com.mrcrayfish.backpacked.common.WanderingTraderEvents;
import com.mrcrayfish.backpacked.common.augment.AugmentHandler;
import com.mrcrayfish.backpacked.common.challenge.impl.*;
import com.mrcrayfish.backpacked.common.tracker.impl.CraftingProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.impl.UniqueCraftingProgressTracker;
import com.mrcrayfish.backpacked.core.ModCommands;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.framework.api.FrameworkAPI;

/**
 * Author: MrCrayfish
 */
public class Bootstrap
{
    public static void init()
    {
        FrameworkAPI.registerSyncedDataKey(ModSyncedDataKeys.UNLOCK_TRACKER);
        FrameworkAPI.registerSyncedDataKey(ModSyncedDataKeys.TRADER_PICKPOCKETING);
        FrameworkAPI.registerSyncedDataKey(ModSyncedDataKeys.BACKPACK);
        FrameworkAPI.registerSyncedDataKey(ModSyncedDataKeys.BACKPACKS);
        FrameworkAPI.registerSyncedDataKey(ModSyncedDataKeys.COSMETIC_PROPERTIES);
        FrameworkAPI.registerSyncedDataKey(ModSyncedDataKeys.SELECTED_BACKPACK);
        FrameworkAPI.registerSyncedDataKey(ModSyncedDataKeys.UNLOCKABLE_BACKPACK_SLOTS);

        Network.init();
        UnlockManager.instance();
        BackpackEvents.init();
        WanderingTraderEvents.init();
        Config.init();
        ModCommands.init();
        AugmentHandler.init();

        // Register progress trackers
        ExploreBiomeChallenge.Tracker.registerEvent();
        FeedAnimalChallenge.Tracker.registerEvent();
        BreedAnimalChallenge.Tracker.registerEvent();
        InteractWithBlockChallenge.Tracker.registerEvent();
        InteractWithEntityChallenge.Tracker.registerEvent();
        TravelDistanceChallenge.Tracker.registerEvent();
        KillMobChallenge.Tracker.registerEvent();
        MineBlockChallenge.Tracker.registerEvent();
        CraftItemChallenge.Tracker.registerEvent();
        MerchantTradeChallenge.Tracker.registerEvent();
        CraftingProgressTracker.registerEvent();
        UniqueCraftingProgressTracker.registerEvent();
    }
}
