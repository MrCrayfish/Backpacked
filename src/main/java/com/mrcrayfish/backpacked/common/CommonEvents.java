package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.common.backpack.WanderingBagBackpack;
import com.mrcrayfish.backpacked.common.tracker.CraftingProgressTracker;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageSyncVillagerBackpack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SCollectItemPacket;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class CommonEvents
{
    @SubscribeEvent
    public static void onPickupItem(EntityItemPickupEvent event)
    {
        if(Config.SERVER.autoEquipBackpackOnPickup.get() && event.getEntityLiving() instanceof ServerPlayerEntity)
        {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
            ItemEntity entity = event.getItem();
            ItemStack stack = entity.getItem();
            if(!(stack.getItem() instanceof BackpackItem))
                return;

            if(Backpacked.getBackpackStack(player).isEmpty())
            {
                event.setCanceled(true);
                if(Backpacked.setBackpackStack(player, stack))
                {
                    ((ServerWorld) entity.level).getChunkSource().broadcast(entity, new SCollectItemPacket(entity.getId(), player.getId(), stack.getCount()));
                    event.setCanceled(true);
                    event.getItem().kill();
                }
            }
        }
    }

    @SubscribeEvent
    public static void onCraftedItem(PlayerEvent.ItemCraftedEvent event)
    {
        if(!(event.getPlayer() instanceof ServerPlayerEntity))
            return;

        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
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
}