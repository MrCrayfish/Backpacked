package com.mrcrayfish.backpacked.data.unlock;

import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageSyncUnlockTracker;
import com.mrcrayfish.framework.api.event.PlayerEvents;
import com.mrcrayfish.framework.api.event.ServerEvents;
import com.mrcrayfish.framework.api.event.TickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Author: MrCrayfish
 */
public final class UnlockManager
{
    private static UnlockManager instance;

    public static UnlockManager instance()
    {
        if(instance == null)
        {
            instance = new UnlockManager();
        }
        return instance;
    }

    private final Set<ServerPlayer> testForCompletion = new HashSet<>();

    private UnlockManager()
    {
        PlayerEvents.LOGGED_IN.register(this::onPlayerLoggedIn);
        PlayerEvents.RESPAWN.register(this::onPlayerRespawn);
        PlayerEvents.CHANGE_DIMENSION.register(this::onPlayerChangedDimension);
        TickEvents.END_SERVER.register(this::onServerTick);
        TickEvents.END_PLAYER.register(this::onPlayerTick);
        /*EntityEvents.LIVING_ENTITY_DEATH.register(this::onEntityDeath);
        BackpackedEvents.BRED_ANIMAL.register(this::onBredAnimal);
        PlayerEvents.CRAFT_ITEM.register(this::onCraftedItem);*/
    }

    private void onPlayerTick(Player player)
    {
        if(player.level().isClientSide() || player.tickCount % 20 != 0)
            return;

        Level world = player.level();
        BlockPos playerPosition = player.blockPosition();
        Biome biome = world.getBiome(playerPosition).value();
        /*world.registryAccess().registryOrThrow(Registries.BIOME).getResourceKey(biome).ifPresent(key -> {
            this.getEventListeners(EventType.EXPLORE_UPDATE).forEach(o ->
                ((ExploreUpdate) o).handle(key, player));
        });*/
    }

    private void onPlayerLoggedIn(Player player)
    {
        getTracker(player).ifPresent(unlockTracker -> {
            Network.getPlay().sendToPlayer(() -> (ServerPlayer) player, new MessageSyncUnlockTracker(unlockTracker.getUnlockedBackpacks()));
            queuePlayerForCompletionTest((ServerPlayer) player); // Safeguard to complete challenges
        });
    }

    private void onPlayerRespawn(Player player, boolean finishedGame)
    {
        getTracker(player).ifPresent(unlockTracker -> {
            Network.getPlay().sendToPlayer(() -> (ServerPlayer) player, new MessageSyncUnlockTracker(unlockTracker.getUnlockedBackpacks()));
        });
    }

    private void onPlayerChangedDimension(Player player, ResourceKey<Level> oldDimension, ResourceKey<Level> newDimension)
    {
        getTracker(player).ifPresent(unlockTracker -> {
            Network.getPlay().sendToPlayer(() -> (ServerPlayer) player, new MessageSyncUnlockTracker(unlockTracker.getUnlockedBackpacks()));
        });
    }

    private void onServerTick(MinecraftServer server)
    {
        if(this.testForCompletion.isEmpty())
            return;

        for(ServerPlayer player : this.testForCompletion)
        {
            getTracker(player).ifPresent(unlockTracker ->
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
        this.testForCompletion.clear();
    }

    public static void queuePlayerForCompletionTest(ServerPlayer player)
    {
        instance().testForCompletion.add(player);
    }

    public static Optional<UnlockTracker> getTracker(Player player)
    {
        return Optional.ofNullable(ModSyncedDataKeys.UNLOCK_TRACKER.getValue(player));
    }

    public static <T> List<T> getTrackers(Player player, Class<T> trackerClass)
    {
        UnlockTracker tracker = ModSyncedDataKeys.UNLOCK_TRACKER.getValue(player);
        return tracker != null ? tracker.getProgressTrackers(trackerClass) : Collections.emptyList();
    }
}
