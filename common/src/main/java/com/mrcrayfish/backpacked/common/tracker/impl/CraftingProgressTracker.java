package com.mrcrayfish.backpacked.common.tracker.impl;

import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatters;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.event.EventType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;


public class CraftingProgressTracker implements IProgressTracker
{
    protected int count;
    protected final int totalCount;
    protected final Predicate<ItemStack> predicate;

    public CraftingProgressTracker(int totalCount, Predicate<ItemStack> predicate)
    {
        this.totalCount = totalCount;
        this.predicate = predicate;
        UnlockManager.instance().addEventListener(EventType.CRAFTED_ITEM, (player, stack, inventory) -> {
            if(this.isComplete() || player.level().isClientSide())
                return;
            this.processCrafted(stack, (ServerPlayer) player);
        });
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
        return ProgressFormatters.CRAFT_X_OF_X.apply(this.count, this.totalCount);
    }
}
