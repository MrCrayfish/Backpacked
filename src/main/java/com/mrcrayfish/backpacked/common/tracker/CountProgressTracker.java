package com.mrcrayfish.backpacked.common.tracker;

import com.mrcrayfish.backpacked.common.IProgressTracker;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;

import java.util.function.BiFunction;

/**
 * Author: MrCrayfish
 */
public class CountProgressTracker implements IProgressTracker
{
    private final BiFunction<Integer, Integer, ITextComponent> formatter;
    private final int maxCount;
    private int count;

    public CountProgressTracker(int maxCount, BiFunction<Integer, Integer, ITextComponent> formatter)
    {
        this.maxCount = maxCount;
        this.formatter = formatter;
    }

    public void increment(ServerPlayerEntity player)
    {
        this.count++;
        this.markForCompletionTest(player);
    }

    public void increment(int amount, ServerPlayerEntity player)
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
    public void read(CompoundNBT tag)
    {
        this.count = tag.getInt("Count");
    }

    @Override
    public void write(CompoundNBT tag)
    {
        tag.putInt("Count", this.count);
    }

    @Override
    public ITextComponent getDisplayComponent()
    {
        return this.formatter.apply(this.count, this.maxCount);
    }
}
