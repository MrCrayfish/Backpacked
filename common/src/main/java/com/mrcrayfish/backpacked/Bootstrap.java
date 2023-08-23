package com.mrcrayfish.backpacked;

import com.mrcrayfish.backpacked.common.BackpackEvents;
import com.mrcrayfish.backpacked.common.WanderingTraderEvents;
import com.mrcrayfish.backpacked.core.ModCommands;
import com.mrcrayfish.backpacked.data.tracker.UnlockManager;
import com.mrcrayfish.backpacked.enchantment.RepairmanEnchantment;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.platform.Services;
import net.minecraft.world.item.CreativeModeTab;

/**
 * Author: MrCrayfish
 */
public class Bootstrap
{
    public static void init()
    {
        Network.init();
        UnlockManager.init();
        BackpackEvents.init();
        RepairmanEnchantment.init();
        WanderingTraderEvents.init();
        Config.init();
        ModCommands.init();
    }
}
