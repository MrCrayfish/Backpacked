package com.mrcrayfish.backpacked.inventory.container;

import net.minecraft.server.level.ServerPlayer;

public interface UnlockableController
{
    boolean isSlotUnlocked(int slot);

    boolean canUnlockSlot(int slot);

    void unlockSlot(int slot);

    void handleUnlockSlot(ServerPlayer player, int slotIndex, int containerIndex);

    int getNextUnlockCost();
}
