package com.mrcrayfish.backpacked.common.tracker;

import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Author: MrCrayfish
 */
public interface IProgressTracker
{
    boolean isComplete();

    void read(ValueInput input);

    void write(ValueOutput output);

    Component getDisplayComponent();

    double getCompletionProgress();

    default void markForCompletionTest(ServerPlayer player)
    {
        UnlockManager.queuePlayerForCompletionTest(player);
    }
}
