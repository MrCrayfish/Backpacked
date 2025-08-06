package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.common.CostModel;
import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
import net.minecraft.server.level.ServerPlayer;

public abstract class UnlockableController
{
    protected UnlockableSlots slots;

    public UnlockableController(UnlockableSlots slots)
    {
        this.slots = slots;
    }

    public final void unlockSlot(int slot)
    {
        this.slots = this.slots.unlockSlot(slot);
    }

    public final boolean isSlotUnlocked(int slot)
    {
        return this.slots.isUnlocked(slot);
    }

    public final boolean canUnlockSlot(int slot)
    {
        return this.slots.isUnlockable(slot);
    }

    public final int getNextUnlockCost()
    {
        return this.slots.nextUnlockCost(this.costModel());
    }

    public abstract CostModel costModel();

    public abstract void handleUnlockSlot(ServerPlayer player, int slotIndex, int containerIndex);
}
