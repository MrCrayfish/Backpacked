package com.mrcrayfish.backpacked.common;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

/**
 * Author: MrCrayfish
 */
public interface IProgressTracker
{
    boolean isComplete();

    void read(CompoundTag tag);

    void write(CompoundTag tag);

    Component getDisplayComponent();

    default void markForCompletionTest(ServerPlayer player)
    {
        UnlockTracker.queuePlayerForCompletionTest(player);
    }
}
