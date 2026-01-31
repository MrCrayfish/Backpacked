package com.mrcrayfish.backpacked.inventory.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

public abstract class CustomContainerMenu extends AbstractContainerMenu
{
    protected CustomContainerMenu(@Nullable MenuType<?> type, int windowId)
    {
        super(type, windowId);
    }

    protected void addPlayerInventorySlots(Inventory inventory, int x, int y)
    {
        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, x + j * 18, y + i * 18));
            }
        }
        for(int i = 0; i < 9; i++)
        {
            this.addSlot(new Slot(inventory, i, x + i * 18, y + 58));
        }
    }
}
