package com.mrcrayfish.backpacked.common.tracker;

import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * Author: MrCrayfish
 */
public interface IProgressTracker // TODO DONE
{
    boolean isComplete();

    void read(CompoundTag tag);

    void write(CompoundTag tag);

    Component getDisplayComponent();

    double getCompletionProgress();

    default void markForCompletionTest(ServerPlayer player)
    {
        UnlockManager.queuePlayerForCompletionTest(player);
    }
}
