package com.mrcrayfish.backpacked.event.entity;

import com.mrcrayfish.framework.api.event.IFrameworkEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;

/**
 * Author: MrCrayfish
 */
public interface MerchantTrade extends IFrameworkEvent
{
    void handle(Merchant merchant, Player player, ItemStack stack);
}
