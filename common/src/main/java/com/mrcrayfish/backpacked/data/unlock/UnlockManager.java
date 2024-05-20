package com.mrcrayfish.backpacked.data.unlock;

import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import com.mrcrayfish.backpacked.event.BackpackedEvents;
import com.mrcrayfish.backpacked.event.EventType;
import com.mrcrayfish.backpacked.event.block.MinedBlock;
import com.mrcrayfish.backpacked.event.entity.FeedAnimal;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageSyncUnlockTracker;
import com.mrcrayfish.framework.api.event.EntityEvents;
import com.mrcrayfish.framework.api.event.IFrameworkEvent;
import com.mrcrayfish.framework.api.event.PlayerEvents;
import com.mrcrayfish.framework.api.event.ServerEvents;
import com.mrcrayfish.framework.api.event.TickEvents;
import com.mrcrayfish.framework.event.IEntityEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    private final Map<EventType<?>, List<Object>> eventListeners = new HashMap<>();

    private UnlockManager()
    {
        PlayerEvents.LOGGED_IN.register(this::onPlayerLoggedIn);
        PlayerEvents.RESPAWN.register(this::onPlayerRespawn);
        PlayerEvents.CHANGE_DIMENSION.register(this::onPlayerChangedDimension);
        TickEvents.END_SERVER.register(this::onServerTick);
        ServerEvents.STOPPING.register(this::onServerStopped);
        EntityEvents.LIVING_ENTITY_DEATH.register(this::onEntityDeath);
        BackpackedEvents.MINED_BLOCK.register(this::onBlockMined);
        BackpackedEvents.FEED_ANIMAL.register(this::onFeedAnimal);
    }

    public <T extends IFrameworkEvent> void addEventListener(EventType<T> type, T handler)
    {
        this.eventListeners.computeIfAbsent(type, type1 -> new ArrayList<>()).add(handler);
    }

    private List<Object> getEventListeners(EventType<?> type)
    {
        return this.eventListeners.getOrDefault(type, Collections.emptyList());
    }

    private void onBlockMined(BlockState state, Player player)
    {
        this.getEventListeners(EventType.MINED_BLOCK).forEach(o ->
            ((MinedBlock) o).handle(state, player));
    }

    private void onFeedAnimal(Animal animal, Player player)
    {
        this.getEventListeners(EventType.FEED_ANIMAL).forEach(o -> {
            ((FeedAnimal) o).handle(animal, player);
        });
    }

    private boolean onEntityDeath(LivingEntity entity, DamageSource source)
    {
        this.getEventListeners(EventType.LIVING_ENTITY_DEATH).forEach(o -> {
            ((IEntityEvent.LivingEntityDeath) o).handle(entity, source);
        });
        return false;
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

    private void onServerStopped(MinecraftServer server)
    {
        // We need to clear listener on server stop, otherwise we leak memory if client
        this.eventListeners.clear();
    }

    public static void queuePlayerForCompletionTest(ServerPlayer player)
    {
        instance().testForCompletion.add(player);
    }

    public static Optional<UnlockTracker> getTracker(Player player)
    {
        return Optional.ofNullable(ModSyncedDataKeys.UNLOCK_TRACKER.getValue(player));
    }
}
