package com.mrcrayfish.backpacked.inventory.container.slot;

import com.mrcrayfish.backpacked.inventory.container.LockedSlotController;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class LockedSlot extends Slot
{
    private final int slot;
    private final LockedSlotController controller;

    public LockedSlot(LockedSlotController controller, Container container, int index, int x, int y)
    {
        super(container, index, x, y);
        this.slot = index;
        this.controller = controller;
    }

    public boolean isUnlocked()
    {
        return this.controller.isSlotUnlocked(this.slot);
    }

    @Override
    public boolean isActive()
    {
        return this.controller.isSlotUnlocked(this.slot);
    }

    @Override
    public boolean mayPickup(Player player)
    {
        return this.controller.isSlotUnlocked(this.slot);
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        return this.controller.isSlotUnlocked(this.slot);
    }

    @Override
    public boolean isHighlightable()
    {
        return this.controller.isSlotUnlocked(this.slot);
    }
}
