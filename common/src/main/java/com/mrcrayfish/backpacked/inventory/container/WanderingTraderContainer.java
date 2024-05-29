package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import com.mrcrayfish.backpacked.util.PickpocketUtil;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class WanderingTraderContainer extends BackpackContainerMenu
{
    private final WanderingTrader trader;

    public WanderingTraderContainer(int id, Inventory playerInventory, WanderingTrader trader)
    {
        super(id, playerInventory, trader.getInventory(), 8, 1, false);
        this.trader = trader;
    }

    @Override
    public boolean stillValid(Player playerIn)
    {
        return this.trader.isAlive() && Optional.ofNullable(ModSyncedDataKeys.TRADER_PICKPOCKETING.getValue(this.trader)).map(data -> !data.getDetectedPlayers().containsKey(playerIn)).orElse(false) && PickpocketUtil.canPickpocketEntity(this.trader, playerIn, Config.SERVER.pickpocketing.maxReachDistance.get() + 0.5);
    }
}
