package com.mrcrayfish.backpacked.data.tracker;

import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageSyncUnlockTracker;
import com.mrcrayfish.framework.api.event.PlayerEvents;
import com.mrcrayfish.framework.api.event.TickEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Author: MrCrayfish
 */
public class UnlockManager
{
    private static final Set<ServerPlayer> testForCompletion = new HashSet<>();

    public static void init()
    {
        PlayerEvents.LOGGED_IN.register(UnlockManager::onPlayerLoggedIn);
        PlayerEvents.RESPAWN.register(UnlockManager::onPlayerRespawn);
        PlayerEvents.CHANGE_DIMENSION.register(UnlockManager::onPlayerChangedDimension);
        TickEvents.END_SERVER.register(UnlockManager::onServerTick);
    }

    static void queuePlayerForCompletionTest(ServerPlayer player)
    {
        testForCompletion.add(player);
    }

    public static Optional<UnlockTracker> get(Player player)
    {
        return Optional.ofNullable(ModSyncedDataKeys.UNLOCK_TRACKER.getValue(player));
    }

    private static void onPlayerLoggedIn(Player player)
    {
        get(player).ifPresent(unlockTracker -> {
            Network.getPlay().sendToPlayer(() -> (ServerPlayer) player, new MessageSyncUnlockTracker(unlockTracker.getUnlockedBackpacks()));
        });
    }

    private static void onPlayerRespawn(Player player, boolean finishedGame)
    {
        get(player).ifPresent(unlockTracker -> {
            Network.getPlay().sendToPlayer(() -> (ServerPlayer) player, new MessageSyncUnlockTracker(unlockTracker.getUnlockedBackpacks()));
        });
    }

    private static void onPlayerChangedDimension(Player player, ResourceKey<Level> oldDimension, ResourceKey<Level> newDimension)
    {
        get(player).ifPresent(unlockTracker -> {
            Network.getPlay().sendToPlayer(() -> (ServerPlayer) player, new MessageSyncUnlockTracker(unlockTracker.getUnlockedBackpacks()));
        });
    }

    private static void onServerTick(MinecraftServer server)
    {
        if(testForCompletion.isEmpty())
            return;

        for(ServerPlayer player : testForCompletion)
        {
            get(player).ifPresent(unlockTracker ->
            {
                unlockTracker.getProgressTrackerMap().forEach((location, progressTracker) ->
                {
                    if(!unlockTracker.getUnlockedBackpacks().contains(location) && progressTracker.isComplete())
                    {
                        BackpackManager.instance().unlockBackpack(player, location);
                    }
                });
            });
        }
        testForCompletion.clear();
    }
}
