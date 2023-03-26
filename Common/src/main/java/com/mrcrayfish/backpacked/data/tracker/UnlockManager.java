package com.mrcrayfish.backpacked.data.tracker;

import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageSyncUnlockTracker;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.framework.api.event.PlayerEvents;
import com.mrcrayfish.framework.api.event.TickEvents;
import net.minecraft.nbt.CompoundTag;
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
        PlayerEvents.COPY.register(UnlockManager::onPlayerClone);
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
        return UnlockManager.get(player, false);
    }

    private static Optional<UnlockTracker> get(Player player, boolean old)
    {
        return Optional.ofNullable(Services.BACKPACK.getUnlockTracker(player, old));
    }

    private static void onPlayerClone(Player oldPlayer, Player newPlayer, boolean respawn)
    {
        get(oldPlayer, true).ifPresent(originalTracker ->
        {
            get(newPlayer).ifPresent(newTracker ->
            {
                newTracker.setUnlockedBackpacks(originalTracker.getUnlockedBackpacks());
                originalTracker.getProgressTrackerMap().forEach((location, progressTracker) ->
                {
                    CompoundTag tag = new CompoundTag();
                    progressTracker.write(tag);
                    Optional.ofNullable(newTracker.getProgressTrackerMap().get(location)).ifPresent(t -> t.read(tag));
                });
            });
        });
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
