package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.common.tracker.impl.BiomeExploreProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.impl.CraftingProgressTracker;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.framework.api.event.PlayerEvents;
import com.mrcrayfish.framework.api.event.TickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


public class BackpackEvents
{
    public static void init()
    {
        PlayerEvents.PICKUP_ITEM.register(BackpackEvents::onPickupItem);
    }

    private static boolean onPickupItem(Player player, ItemEntity entity)
    {
        if(Config.SERVER.common.autoEquipBackpackOnPickup.get() && player instanceof ServerPlayer serverPlayer)
        {
            ItemStack stack = entity.getItem();
            if(!(stack.getItem() instanceof BackpackItem))
                return false;

            if(Services.BACKPACK.getBackpackStack(serverPlayer).isEmpty())
            {
                if(Services.BACKPACK.setBackpackStack(serverPlayer, stack))
                {
                    ((ServerLevel) entity.level()).getChunkSource().broadcast(entity, new ClientboundTakeItemEntityPacket(entity.getId(), serverPlayer.getId(), stack.getCount()));
                    entity.discard();
                }
                return true;
            }
        }
        return false;
    }
}
