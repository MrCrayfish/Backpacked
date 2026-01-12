package com.mrcrayfish.backpacked.common.tracker.impl;

import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatter;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.framework.api.event.FrameworkPlayerEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.function.Predicate;


public class CraftingProgressTracker implements IProgressTracker
{
    protected int count;
    protected final int totalCount;
    private final ProgressFormatter formatter;
    protected final Predicate<ItemStack> predicate;

    public CraftingProgressTracker(int totalCount, ProgressFormatter formatter, Predicate<ItemStack> predicate)
    {
        this.totalCount = totalCount;
        this.formatter = formatter;
        this.predicate = predicate;
    }

    protected void processCrafted(ItemStack stack, ServerPlayer player)
    {
        if(this.predicate.test(stack))
        {
            this.count += stack.getCount();
            this.markForCompletionTest(player);
        }
    }

    @Override
    public boolean isComplete()
    {
        return this.count >= this.totalCount;
    }

    @Override
    public void read(ValueInput input)
    {
        this.count = input.getIntOr("Count", 0);
    }

    @Override
    public void write(ValueOutput output)
    {
        output.putInt("Count", this.count);
    }

    @Override
    public Component getDisplayComponent()
    {
        return this.formatter.formatter().apply(this.count, this.totalCount);
    }

    @Override
    public double getCompletionProgress()
    {
        return Mth.clamp(this.count / (double) Math.max(1, this.totalCount), 0, 1);
    }

    public static void registerEvent()
    {
        FrameworkPlayerEvents.CRAFTED_ITEM.register((player, stack, inventory) -> {
            if(player.level().isClientSide())
                return;
            UnlockManager.getIncompleteTrackers(player, CraftingProgressTracker.class).forEach(tracker -> {
                tracker.processCrafted(stack, (ServerPlayer) player);
            });
        });
    }
}
