package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.data.PickpocketChallenge;
import com.mrcrayfish.backpacked.util.PickpocketUtil;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

/**
 * Author: MrCrayfish
 */
public class WanderingTraderContainer extends BackpackContainer
{
    private final WanderingTraderEntity trader;

    public WanderingTraderContainer(int id, PlayerInventory playerInventory, WanderingTraderEntity trader)
    {
        super(id, playerInventory, trader.getInventory(), 8, 1, false);
        this.trader = trader;
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn)
    {
        return this.trader.isAlive() && PickpocketChallenge.get(this.trader).map(data -> !data.getDetectedPlayers().containsKey(playerIn)).orElse(false) && PickpocketUtil.canPickpocketEntity(this.trader, playerIn, Config.SERVER.pickpocketMaxReachDistance.get() + 0.5);
    }
}
