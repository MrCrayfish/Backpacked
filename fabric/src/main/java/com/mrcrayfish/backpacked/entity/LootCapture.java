package com.mrcrayfish.backpacked.entity;

import net.minecraft.server.level.ServerPlayer;

/**
 * Author: MrCrayfish
 */
public interface LootCapture
{
    void backpacked$StartCapturingDrop(ServerPlayer player);

    void backpacked$EndCapturingDrop();
}
