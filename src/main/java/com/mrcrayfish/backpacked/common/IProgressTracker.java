package com.mrcrayfish.backpacked.common;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;

/**
 * Author: MrCrayfish
 */
public interface IProgressTracker
{
    boolean isComplete();

    void read(CompoundNBT tag);

    void write(CompoundNBT tag);

    ITextComponent getDisplayComponent();

    default void markForCompletionTest(ServerPlayerEntity player)
    {
        UnlockTracker.queuePlayerForCompletionTest(player);
    }
}
