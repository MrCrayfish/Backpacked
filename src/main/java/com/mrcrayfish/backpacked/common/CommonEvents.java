package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.common.tracker.BiomeExploreProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.CraftingProgressTracker;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class CommonEvents
{
    @SubscribeEvent
    public static void onPickupItem(EntityItemPickupEvent event)
    {
        if(Config.SERVER.autoEquipBackpackOnPickup.get() && event.getEntity() instanceof ServerPlayer player)
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
        if(!(event.getEntity() instanceof ServerPlayer player))
            return;

        ItemStack craftedItem = event.getCrafting();
        UnlockTracker.get(event.getEntity()).ifPresent(unlockTracker ->
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
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.phase != TickEvent.Phase.END)
            return;

        if(event.player.level.isClientSide())
            return;

        if(event.player.tickCount % 20 != 0)
            return;

        ServerPlayer player = (ServerPlayer) event.player;
        ServerLevel world = player.getLevel();
        BlockPos playerPosition = player.blockPosition();
        world.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getResourceKey(world.getBiome(playerPosition).value()).ifPresent(key ->
        {
            UnlockTracker.get(player).ifPresent(unlockTracker ->
            {
                unlockTracker.getProgressTrackerMap().forEach((location, progressTracker) ->
                {
                    if(progressTracker instanceof BiomeExploreProgressTracker && !progressTracker.isComplete())
                    {
                        ((BiomeExploreProgressTracker) progressTracker).explore(key, player);
                    }
                });
            });
        });
    }
}
