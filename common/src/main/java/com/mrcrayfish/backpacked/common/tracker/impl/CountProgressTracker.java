package com.mrcrayfish.backpacked.common.tracker.impl;

import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

/**
 * Author: MrCrayfish
 */
public class CountProgressTracker implements IProgressTracker
{
    private final ProgressFormatter formatter;
    private final int maxCount;
    private int count;

    public CountProgressTracker(int maxCount, ProgressFormatter formatter)
    {
        this.maxCount = maxCount;
        this.formatter = formatter;
    }

    public void increment(ServerPlayer player)
    {
        this.count++;
        this.markForCompletionTest(player);
    }

    public void increment(int amount, ServerPlayer player)
    {
        this.count += amount;
        this.markForCompletionTest(player);
    }

    @Override
    public boolean isComplete()
    {
        return this.count >= this.maxCount;
    }

    @Override
    public void read(CompoundTag tag)
    {
        this.count = tag.getInt("Count");
    }

    @Override
    public void write(CompoundTag tag)
    {
        tag.putInt("Count", this.count);
    }

    @Override
    public Component getDisplayComponent()
    {
        return this.formatter.formatter().apply(this.count, this.maxCount);
    }

    @Override
    public double getCompletionProgress()
    {
        return Mth.clamp(this.count / (double) Math.max(1, this.maxCount), 0, 1);
    }
}
