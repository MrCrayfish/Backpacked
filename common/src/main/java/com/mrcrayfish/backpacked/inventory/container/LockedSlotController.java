package com.mrcrayfish.backpacked.inventory.container;

public interface LockedSlotController
{
    boolean isSlotUnlocked(int slot);

    void unlockSlot(int slot);
}
