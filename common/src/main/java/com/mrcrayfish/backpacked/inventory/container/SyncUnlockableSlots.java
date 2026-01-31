package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.inventory.container.slot.UnlockableSlot;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public interface SyncUnlockableSlots
{
    /**
     * Handles syncing unlocked slots after they were just unlocked by a player.
     *
     * @param player the player that unlocked the slots
     * @param unlockedSlots a list of slots that were unlocked
     */
    void handleSyncSlots(ServerPlayer player, List<UnlockableSlot> unlockedSlots);
}
