package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.data.PickpocketChallenge;
import com.mrcrayfish.backpacked.util.PickpocketUtil;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

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
        return this.trader.isAlive() && PickpocketChallenge.get(this.trader).map(data -> !data.getDetectedPlayers().containsKey(playerIn)).orElse(false) && PickpocketUtil.canPickpocketEntity(this.trader, playerIn, Config.SERVER.pickpocketMaxReachDistance.get() + 0.5);
    }
}
