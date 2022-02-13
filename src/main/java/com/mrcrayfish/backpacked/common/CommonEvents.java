package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.common.backpack.WanderingPackBackpack;
import com.mrcrayfish.backpacked.common.tracker.CraftingProgressTracker;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageSyncVillagerBackpack;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class CommonEvents
{
    @SubscribeEvent
    public static void onPickupItem(EntityItemPickupEvent event)
    {
        if(Config.SERVER.autoEquipBackpackOnPickup.get() && event.getEntityLiving() instanceof ServerPlayer player)
        {
            ItemEntity entity = event.getItem();
            ItemStack stack = entity.getItem();
            if(!(stack.getItem() instanceof BackpackItem))
                return;

            if(Backpacked.getBackpackStack(player).isEmpty())
            {
                event.setCanceled(true);
                if(Backpacked.setBackpackStack(player, stack))
                {
                    ((ServerLevel) entity.level).getChunkSource().broadcast(entity, new ClientboundTakeItemEntityPacket(entity.getId(), player.getId(), stack.getCount()));
                    event.setCanceled(true);
                    event.getItem().discard();
                }
            }
        }
    }

    @SubscribeEvent
    public static void onCraftedItem(PlayerEvent.ItemCraftedEvent event)
    {
        if(!(event.getPlayer() instanceof ServerPlayer player))
            return;

        ItemStack craftedItem = event.getCrafting();
        UnlockTracker.get(event.getPlayer()).ifPresent(unlockTracker ->
        {
            unlockTracker.getProgressTrackerMap().forEach((location, progressTracker) ->
            {
                if(progressTracker instanceof CraftingProgressTracker && !progressTracker.isComplete())
                {
                    ((CraftingProgressTracker) progressTracker).processCrafted(craftedItem, player);
                }
            });
        });
    }

    @SubscribeEvent
    public static void onSpawnEntity(LivingSpawnEvent.SpecialSpawn event)
    {
        if(event.getSpawnReason() != MobSpawnType.EVENT)
            return;

        if(event.getEntityLiving().getType() != EntityType.WANDERING_TRADER)
            return;

        WanderingTrader trader = (WanderingTrader) event.getEntityLiving();
        ItemStack backpack = new ItemStack(ModItems.BACKPACK.get());
        backpack.getOrCreateTag().putString("BackpackModel", WanderingPackBackpack.ID.toString());
        trader.getInventory().addItem(backpack);
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event)
    {
        if(event.getTarget().getType() != EntityType.WANDERING_TRADER)
            return;

        WanderingTrader trader = (WanderingTrader) event.getTarget();
        if(trader.getInventory().countItem(ModItems.BACKPACK.get()) > 0)
        {
            Network.getPlayChannel().send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getPlayer()), new MessageSyncVillagerBackpack(event.getTarget().getId()));
        }
    }
}
