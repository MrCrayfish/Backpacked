package com.mrcrayfish.backpacked.common.tracker;

import com.mrcrayfish.backpacked.common.IProgressTracker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

/**
 * Author: MrCrayfish
 */
public class CountProgressTracker implements IProgressTracker
{
    private final BiFunction<Integer, Integer, Component> formatter;
    private final int maxCount;
    private int count;

    public CountProgressTracker(int maxCount, BiFunction<Integer, Integer, Component> formatter)
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
        return this.formatter.apply(this.count, this.maxCount);
    }
}
