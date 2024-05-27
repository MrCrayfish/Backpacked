package com.mrcrayfish.backpacked;

import com.mrcrayfish.backpacked.common.BackpackEvents;
import com.mrcrayfish.backpacked.common.WanderingTraderEvents;
import com.mrcrayfish.backpacked.common.challenge.impl.BreedAnimalChallenge;
import com.mrcrayfish.backpacked.common.challenge.impl.ExploreBiomeChallenge;
import com.mrcrayfish.backpacked.common.challenge.impl.FeedAnimalChallenge;
import com.mrcrayfish.backpacked.common.challenge.impl.InteractWithBlockChallenge;
import com.mrcrayfish.backpacked.common.challenge.impl.InteractWithEntityChallenge;
import com.mrcrayfish.backpacked.common.challenge.impl.KillMobChallenge;
import com.mrcrayfish.backpacked.common.challenge.impl.MineBlockChallenge;
import com.mrcrayfish.backpacked.common.challenge.impl.TravelDistanceChallenge;
import com.mrcrayfish.backpacked.common.tracker.impl.CraftingProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.impl.UniqueCraftingProgressTracker;
import com.mrcrayfish.backpacked.core.ModCommands;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.enchantment.RepairmanEnchantment;
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
        Network.init();
        UnlockManager.instance();
        BackpackEvents.init();
        RepairmanEnchantment.init();
        WanderingTraderEvents.init();
        Config.init();
        ModCommands.init();

        // Register progress trackers
        ExploreBiomeChallenge.Tracker.registerEvent();
        FeedAnimalChallenge.Tracker.registerEvent();
        BreedAnimalChallenge.Tracker.registerEvent();
        InteractWithBlockChallenge.Tracker.registerEvent();
        InteractWithEntityChallenge.Tracker.registerEvent();
        TravelDistanceChallenge.Tracker.registerEvent();
        KillMobChallenge.Tracker.registerEvent();
        MineBlockChallenge.Tracker.registerEvent();
        CraftingProgressTracker.registerEvent();
        UniqueCraftingProgressTracker.registerEvent();
    }
}
