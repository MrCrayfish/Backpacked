package com.mrcrayfish.backpacked.platform.services;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

/**
 * Author: MrCrayfish
 */
public interface IScreenHelper
{
    void openConfigScreen();

    int getScreenLeftPos(AbstractContainerScreen<?> screen);

    int getScreenTopPos(AbstractContainerScreen<?> screen);
}
