package com.mrcrayfish.backpacked.platform.services;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;

/**
 * Author: MrCrayfish
 */
public interface IScreenHelper
{
    void openConfigScreen();

    int getScreenLeftPos(AbstractContainerScreen<?> screen);

    int getScreenTopPos(AbstractContainerScreen<?> screen);

    Slot createCreativeSlotWrapper(Slot slot, int index, int x, int y);
}
