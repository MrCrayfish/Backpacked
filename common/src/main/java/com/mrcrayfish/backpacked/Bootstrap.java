package com.mrcrayfish.backpacked;

import com.mrcrayfish.backpacked.common.BackpackEvents;
import com.mrcrayfish.backpacked.common.WanderingTraderEvents;
import com.mrcrayfish.backpacked.common.augment.AugmentHandler;
import com.mrcrayfish.backpacked.common.challenge.impl.*;
import com.mrcrayfish.backpacked.common.tracker.impl.CraftingProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.impl.UniqueCraftingProgressTracker;
import com.mrcrayfish.backpacked.core.ModCommands;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.network.Network;

/**
 * Author: MrCrayfish
 */
public class Bootstrap
{
    public static void init()
    {
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
